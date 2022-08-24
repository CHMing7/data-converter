package com.chm.converter.json.fastjson.serializer;

import com.alibaba.fastjson.serializer.FieldSerializer;
import com.alibaba.fastjson.serializer.JavaBeanSerializer;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.UseRawJudge;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codec.UniversalCodecAdapterCreator;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.ArrayUtil;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.core.utils.CollStreamUtil;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.core.utils.ReflectUtil;
import com.chm.converter.json.fastjson.FastjsonCoreCodec;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-08
 **/
public class FastjsonSerializeConfig extends SerializeConfig {

    private final Class<? extends Converter> converterClass;

    private final UniversalGenerate<Codec> generate;

    private final UseRawJudge useRawJudge;

    private final NameFilter nameFilter;

    private final PropertyFilter propertyFilter;

    public FastjsonSerializeConfig(Converter<?> converter, UseRawJudge useRawJudge) {
        this(converter, null, useRawJudge);
    }

    public FastjsonSerializeConfig(Converter<?> converter, UniversalGenerate<Codec> generate, UseRawJudge useRawJudge) {
        this.converterClass = converter != null ? converter.getClass() : null;
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);
        this.useRawJudge = useRawJudge;
        nameFilter = new FastjsonNameFilter(converterClass);
        propertyFilter = new FastjsonPropertyFilter(converterClass);
    }


    @Override
    public ObjectSerializer getObjectWriter(Class<?> clazz) {
        ObjectSerializer objectWriter = super.getObjectWriter(clazz);
        if (objectWriter instanceof FastjsonJavaBeanSerializer ||
                objectWriter instanceof FastjsonCoreCodec) {
            return objectWriter;
        }
        // 校验制定类或其父类集中是否存在Fastjson框架注解
        if (useRawJudge.useRawImpl(clazz)) {
            return objectWriter;
        }

        ObjectSerializer suitableSerializer = getSuitableSerializer(clazz, objectWriter);
        if (suitableSerializer != null) {
            return suitableSerializer;
        }

        return objectWriter;
    }

    private ObjectSerializer getSuitableSerializer(Class clazz, ObjectSerializer rawSerializer) {
        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz, converterClass);
        return UniversalCodecAdapterCreator.createSuitable(this.generate, clazz,
                (type, codec) -> {
                    FastjsonCoreCodec coreCodec = new FastjsonCoreCodec(codec);
                    put(clazz, coreCodec);
                    return coreCodec;
                },
                rawSerializer instanceof JavaBeanSerializer &&
                        CollUtil.isNotEmpty(javaBeanInfo.getSortedFieldList()),
                (type, codec) -> {
                    FastjsonJavaBeanSerializer beanSerializer = new FastjsonJavaBeanSerializer(clazz);
                    put(clazz, beanSerializer);
                    beanSerializer.init(clazz);
                    return beanSerializer;
                });
    }

    private class FastjsonJavaBeanSerializer extends JavaBeanSerializer {

        public FastjsonJavaBeanSerializer(Class<?> beanType) {
            super(beanType);
        }

        private void init(Class<?> beanType) {
            JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(beanType, converterClass);
            List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
            if (CollUtil.isEmpty(sortedFieldList)) {
                return;
            }
            // 过滤不需要序列化的属性
            List<FieldSerializer> sortedGetterList = ListUtil.toList(this.sortedGetters);
            Map<String, FieldSerializer> fieldSerializerMap = CollStreamUtil.toMap(sortedGetterList,
                    fieldSerializer -> fieldSerializer.fieldInfo.name, Function.identity());

            sortedGetterList = sortedFieldList.stream().filter(FieldInfo::isSerialize).map(fieldInfo -> {
                // 使用自定义属性序列化类
                FieldSerializer fieldSerializer = fieldSerializerMap.get(fieldInfo.getFieldName());
                ObjectSerializer objectSerializer = getFieldSerializer(fieldInfo);
                return new FastjsonFieldSerializer(beanType, fieldSerializer.fieldInfo, objectSerializer);
            }).collect(Collectors.toList());

            FieldSerializer[] sortedGetters = ArrayUtil.toArray(sortedGetterList, FieldSerializer.class);
            // 使用反射重新赋值
            ReflectUtil.setFieldValue(this, "sortedGetters", sortedGetters);

            this.addFilter(nameFilter);
            this.addFilter(propertyFilter);
        }

        private ObjectSerializer getFieldSerializer(FieldInfo fieldInfo) {
            ObjectSerializer objectSerializer = FastjsonSerializeConfig.this.getObjectWriter(fieldInfo.getFieldClass());
            if (objectSerializer instanceof WithFormat) {
                String format = fieldInfo.getFormat();
                objectSerializer = (ObjectSerializer) ((WithFormat) objectSerializer).withDatePattern(format);
            }
            return objectSerializer;
        }
    }

    /**
     * 修改key值
     */
    private static class FastjsonNameFilter implements NameFilter {

        private final Class<? extends Converter> converterClass;

        public FastjsonNameFilter(Class<? extends Converter> converterClass) {
            this.converterClass = converterClass;
        }

        @Override
        public String process(Object object, String name, Object value) {
            Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(ClassUtil.getClass(object), converterClass);
            FieldInfo fieldInfo = fieldInfoMap.get(name);
            return fieldInfo != null && fieldInfo.getName() != null ? fieldInfo.getName() : name;
        }
    }

    /**
     * 检查属性是否需要序列化
     */
    private static class FastjsonPropertyFilter implements PropertyFilter {

        private final Class<? extends Converter> converterClass;

        public FastjsonPropertyFilter(Class<? extends Converter> converterClass) {
            this.converterClass = converterClass;
        }

        @Override
        public boolean apply(Object object, String name, Object value) {
            Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(ClassUtil.getClass(object), converterClass);
            FieldInfo fieldInfo = fieldInfoMap.get(name);
            return fieldInfo.isSerialize();
        }
    }

}
