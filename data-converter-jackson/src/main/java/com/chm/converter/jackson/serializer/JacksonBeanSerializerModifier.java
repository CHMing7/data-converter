package com.chm.converter.jackson.serializer;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.UseRawJudge;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codec.UniversalCodecAdapterCreator;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.StringUtil;
import com.chm.converter.jackson.AbstractModule;
import com.chm.converter.jackson.PropertyNameTransformer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.impl.UnsupportedTypeSerializer;
import com.fasterxml.jackson.databind.util.NameTransformer;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-17
 **/
public class JacksonBeanSerializerModifier extends BeanSerializerModifier {

    private final Class<? extends Converter> converterClass;

    private final UniversalGenerate<Codec> generate;

    private final UseRawJudge useRawJudge;

    public JacksonBeanSerializerModifier(Converter<?> converter, UseRawJudge useRawJudge) {
        this(converter, null, useRawJudge);
    }

    public JacksonBeanSerializerModifier(Converter<?> converter, UniversalGenerate<Codec> generate, UseRawJudge useRawJudge) {
        this.converterClass = converter != null ? converter.getClass() : null;
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);
        this.useRawJudge = useRawJudge;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        Class<?> beanClass = beanDesc.getBeanClass();
        if (useRawJudge.useRawImpl(beanClass)) {
            return super.changeProperties(config, beanDesc, beanProperties);
        }
        List<BeanPropertyWriter> resultList = new LinkedList<>();
        Map<String, BeanPropertyWriter> propertyWriterMap = beanProperties.stream()
                .collect(Collectors.toMap(BeanPropertyWriter::getName, beanPropertyWriter -> beanPropertyWriter));
        NameTransformer nameTransformer = PropertyNameTransformer.get(beanClass, converterClass);
        TypeToken<?> typeToken = AbstractModule.jacksonTypeToLangType(beanDesc.getType());
        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(typeToken, converterClass);
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        // 去除不序列化的属性
        sortedFieldList.stream().filter(FieldInfo::isSerialize).forEach(fieldInfo -> {
            BeanPropertyWriter beanPropertyWriter = propertyWriterMap.get(fieldInfo.getFieldName());
            // 修改时间类型序列化类
            JsonSerializer<Object> coreCodecSerializer = createCoreCodecSerializer(fieldInfo);
            if (coreCodecSerializer != null) {
                beanPropertyWriter.assignSerializer(coreCodecSerializer);
            }
            // 修改key值
            BeanPropertyWriter newWriter = beanPropertyWriter.rename(nameTransformer);
            resultList.add(newWriter);
        });
        return resultList;
    }

    private JsonSerializer<Object> createCoreCodecSerializer(FieldInfo fieldInfo) {
        if (fieldInfo == null) {
            return null;
        }
        // 时间类型需设置format
        String format = fieldInfo.getFormat();
        JacksonCoreCodecSerializer coreCodecSerializer = UniversalCodecAdapterCreator.createPriorityUse(this.generate,
                fieldInfo.getFieldType(), (type, codec) -> {
                    if (codec instanceof WithFormat && StringUtil.isNotBlank(format)) {
                        return new JacksonCoreCodecSerializer<>((Codec) ((WithFormat) codec).withDatePattern(format));
                    }
                    return new JacksonCoreCodecSerializer<>(codec);
                });

        return coreCodecSerializer;
    }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        Class<?> beanClass = beanDesc.getBeanClass();
        JsonSerializer<?> jsonSerializer = super.modifySerializer(config, beanDesc, serializer);
        if (beanClass == Object.class
                || useRawJudge.useRawImpl(beanClass)
                || jsonSerializer instanceof JacksonCoreCodecSerializer) {
            return jsonSerializer;
        }

        TypeToken<?> typeToken = AbstractModule.jacksonTypeToLangType(beanDesc.getType());
        JacksonCoreCodecSerializer<?> coreCodecSerializer = UniversalCodecAdapterCreator.create(this.generate, typeToken,
                (type, codec) -> new JacksonCoreCodecSerializer<>(codec));

        if (coreCodecSerializer != null) {
            if (coreCodecSerializer.getCodec().isPriorityUse() ||
                    jsonSerializer instanceof UnsupportedTypeSerializer) {
                return coreCodecSerializer;
            }
        }

        return jsonSerializer;
    }

    @Override
    public JsonSerializer<?> modifyEnumSerializer(SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        if (serializer instanceof JacksonCoreCodecSerializer) {
            return serializer;
        }
        Codec codec = this.generate.get(valueType.getRawClass());
        return new JacksonCoreCodecSerializer<>(codec);
    }
}
