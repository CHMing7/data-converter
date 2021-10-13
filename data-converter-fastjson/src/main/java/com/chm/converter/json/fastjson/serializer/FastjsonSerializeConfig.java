package com.chm.converter.json.fastjson.serializer;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.serializer.*;
import com.chm.converter.core.*;
import com.chm.converter.json.fastjson.FastjsonDefaultDateCodec;
import com.chm.converter.json.fastjson.FastjsonJdk8DateCodec;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
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

    private final UseOriginalJudge useOriginalJudge;

    private final Class<? extends Converter> converterClass;

    private final NameFilter nameFilter;

    private final PropertyFilter propertyFilter;

    public FastjsonSerializeConfig(Converter<?> converter, UseOriginalJudge useOriginalJudge) {
        super();
        this.converterClass = converter != null ? converter.getClass() : null;
        this.useOriginalJudge = useOriginalJudge;
        nameFilter = new FastjsonNameFilter(converterClass);
        propertyFilter = new FastjsonPropertyFilter(converterClass);
        // Java8 Time Serializer
        put(Instant.class, new FastjsonJdk8DateCodec<>(Instant.class, converter));
        put(LocalDate.class, new FastjsonJdk8DateCodec<>(LocalDate.class, converter));
        put(LocalDateTime.class, new FastjsonJdk8DateCodec<>(LocalDateTime.class, converter));
        put(LocalTime.class, new FastjsonJdk8DateCodec<>(LocalTime.class, converter));
        put(OffsetDateTime.class, new FastjsonJdk8DateCodec<>(OffsetDateTime.class, converter));
        put(OffsetTime.class, new FastjsonJdk8DateCodec<>(OffsetTime.class, converter));
        put(ZonedDateTime.class, new FastjsonJdk8DateCodec<>(ZonedDateTime.class, converter));
        put(MonthDay.class, new FastjsonJdk8DateCodec<>(MonthDay.class, converter));
        put(YearMonth.class, new FastjsonJdk8DateCodec<>(YearMonth.class, converter));
        put(Year.class, new FastjsonJdk8DateCodec<>(Year.class, converter));
        put(ZoneOffset.class, new FastjsonJdk8DateCodec<>(ZoneOffset.class, converter));

        // Default Date Serializer
        put(java.sql.Date.class, new FastjsonDefaultDateCodec<>(java.sql.Date.class, converter));
        put(Timestamp.class, new FastjsonDefaultDateCodec<>(Timestamp.class, converter));
        put(Date.class, new FastjsonDefaultDateCodec<>(Date.class, converter));
    }

    @Override
    public ObjectSerializer getObjectWriter(Class<?> clazz) {
        ObjectSerializer objectWriter = super.getObjectWriter(clazz);
        if (objectWriter instanceof FastjsonJavaBeanSerializer) {
            return objectWriter;
        }
        // 校验制定类或其父类集中是否存在Fastjson框架注解
        if (useOriginalJudge.useOriginalImpl(clazz)) {
            return objectWriter;
        }

        if (objectWriter instanceof JavaBeanSerializer) {
            JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz, converterClass);
            if (CollectionUtil.isNotEmpty(javaBeanInfo.getSortedFieldList())) {
                put(clazz, objectWriter = new FastjsonJavaBeanSerializer(clazz));
                return objectWriter;
            }
        }

        return objectWriter;
    }

    private class FastjsonJavaBeanSerializer extends JavaBeanSerializer {

        public FastjsonJavaBeanSerializer(Class<?> beanType) {
            super(beanType);
            JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(beanType, converterClass);
            List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
            if (CollectionUtil.isEmpty(sortedFieldList)) {
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

            if (objectSerializer instanceof FastjsonJdk8DateCodec) {
                String format = fieldInfo.getFormat();
                objectSerializer = ((FastjsonJdk8DateCodec<?>) objectSerializer).withDatePattern(format);
            }
            if (objectSerializer instanceof FastjsonDefaultDateCodec) {
                String format = fieldInfo.getFormat();
                objectSerializer = ((FastjsonDefaultDateCodec<?>) objectSerializer).withDatePattern(format);
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
