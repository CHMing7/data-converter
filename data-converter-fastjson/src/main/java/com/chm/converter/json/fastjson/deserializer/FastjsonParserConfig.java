package com.chm.converter.json.fastjson.deserializer;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-09
 **/
public class FastjsonParserConfig extends ParserConfig {

    private final Converter<?> converter;

    private final Class<? extends Converter> converterClass;

    private final UniversalGenerate<Codec> generate;

    private final UseRawJudge useRawJudge;

    public FastjsonParserConfig(Converter<?> converter, UseRawJudge useRawJudge) {
        this(converter, null, useRawJudge);
    }

    public FastjsonParserConfig(Converter<?> converter, UniversalGenerate<Codec> generate, UseRawJudge useRawJudge) {
        this.converter = converter;
        this.converterClass = converter != null ? converter.getClass() : null;
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);
        this.useRawJudge = useRawJudge;
    }

    @Override
    public ObjectDeserializer getDeserializer(Type type) {
        Class<?> clazz = ClassUtil.getClassByType(type);
        // ?????????????????????????????????????????????Fastjson????????????
        if (useRawJudge.useRawImpl(clazz)) {
            return super.getDeserializer(type);
        }
        FastjsonCoreCodec priorityUse = UniversalCodecAdapterCreator.createPriorityUse(this.generate, type, (t, codec) -> {
            ObjectDeserializer cacheDeserializer = get(t.getType());
            if (cacheDeserializer instanceof FastjsonCoreCodec) {
                return (FastjsonCoreCodec) cacheDeserializer;
            }
            FastjsonCoreCodec coreCodec = new FastjsonCoreCodec(codec);
            putDeserializer(clazz, coreCodec);
            return coreCodec;
        });
        if (priorityUse != null) {
            return priorityUse;
        }

        ObjectDeserializer deserializer = super.getDeserializer(type);
        if (deserializer instanceof FastjsonJavaBeanDeserializer ||
                deserializer instanceof FastjsonCoreCodec) {
            return deserializer;
        }

        ObjectDeserializer suitableDeserializer = getSuitableDeserializer(clazz, deserializer);
        if (suitableDeserializer != null) {
            return suitableDeserializer;
        }

        return deserializer;
    }

    private ObjectDeserializer getSuitableDeserializer(Class clazz, ObjectDeserializer rawDeserializer) {
        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz, converterClass);
        return UniversalCodecAdapterCreator.createSuitable(this.generate, clazz,
                (type, codec) -> {
                    FastjsonCoreCodec coreCodec = new FastjsonCoreCodec(codec);
                    putDeserializer(clazz, coreCodec);
                    return coreCodec;
                },
                rawDeserializer instanceof JavaBeanDeserializer &&
                        CollUtil.isNotEmpty(javaBeanInfo.getSortedFieldList()),
                (type, codec) -> {
                    FastjsonJavaBeanDeserializer beanDeserializer = new FastjsonJavaBeanDeserializer(this, clazz);
                    putDeserializer(clazz, beanDeserializer);
                    beanDeserializer.init(this, clazz);
                    return beanDeserializer;
                });
    }

    private class FastjsonJavaBeanDeserializer extends JavaBeanDeserializer {

        public <T> FastjsonJavaBeanDeserializer(ParserConfig config, Class<T> clazz) {
            super(config, clazz);
        }

        private <T> void init(ParserConfig config, Class<T> clazz) {
            JavaBeanInfo<T> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz, converterClass);
            List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
            if (CollUtil.isEmpty(sortedFieldList)) {
                return;
            }
            // ????????????????????????????????????????????????
            // ?????????????????????????????????
            List<FieldDeserializer> sortedFieldDeserializerList = ListUtil.toList(this.sortedFieldDeserializers);
            Map<String, FieldDeserializer> fieldDeserializerMap = CollStreamUtil.toMap(sortedFieldDeserializerList,
                    fieldDeserializer -> fieldDeserializer.fieldInfo.name, Function.identity());

            sortedFieldDeserializerList = sortedFieldList.stream().filter(FieldInfo::isDeserialize).map(fieldInfo -> {
                // ?????????????????????????????????
                FieldDeserializer fieldDeserializer = fieldDeserializerMap.get(fieldInfo.getFieldName());
                ObjectDeserializer objectDeserializer = getFieldDeserializer(fieldInfo);
                ReflectUtil.setFieldValue(fieldDeserializer.fieldInfo, "name", fieldInfo.getName());
                ReflectUtil.setFieldValue(fieldDeserializer.fieldInfo, "name_chars", genFieldNameChars(fieldInfo.getName()));
                ReflectUtil.setFieldValue(fieldDeserializer.fieldInfo, "format", fieldInfo.getFormat());
                return new FastjsonFieldDeserializer(config, fieldInfo.getFieldClass(), fieldDeserializer.fieldInfo, objectDeserializer);
            }).collect(Collectors.toList());

            FieldDeserializer[] sortedFieldDeserializers = ArrayUtil.toArray(sortedFieldDeserializerList, FieldDeserializer.class);
            // ????????????????????????
            ReflectUtil.setFieldValue(this, "sortedFieldDeserializers", sortedFieldDeserializers);
        }

        private ObjectDeserializer getFieldDeserializer(FieldInfo fieldInfo) {
            ObjectDeserializer objectDeserializer = FastjsonParserConfig.this.getDeserializer(fieldInfo.getFieldClass());
            if (objectDeserializer instanceof WithFormat) {
                String format = fieldInfo.getFormat();
                objectDeserializer = (ObjectDeserializer) ((WithFormat) objectDeserializer).withDatePattern(format);
            }
            return objectDeserializer;
        }

        protected char[] genFieldNameChars(String name) {
            int nameLen = name.length();
            char[] nameChars = new char[nameLen + 3];
            name.getChars(0, name.length(), nameChars, 1);
            nameChars[0] = '"';
            nameChars[nameLen + 1] = '"';
            nameChars[nameLen + 2] = ':';
            return nameChars;
        }
    }

}
