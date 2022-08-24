package com.chm.converter.xml.jackson.serializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.UseRawJudge;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codec.UniversalCodecAdapterCreator;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.StringUtil;
import com.chm.converter.jackson.PropertyNameTransformer;
import com.chm.converter.jackson.serializer.JacksonCoreCodecSerializer;
import com.chm.converter.xml.XmlClassInfoStorage;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.UnsupportedTypeSerializer;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.dataformat.xml.ser.XmlBeanPropertyWriter;
import com.fasterxml.jackson.dataformat.xml.ser.XmlBeanSerializerBase;
import com.fasterxml.jackson.dataformat.xml.ser.XmlBeanSerializerModifier;
import com.fasterxml.jackson.dataformat.xml.util.TypeUtil;
import com.fasterxml.jackson.dataformat.xml.util.XmlInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-08
 **/
public class JacksonXmlBeanSerializerModifier extends XmlBeanSerializerModifier {

    private final Class<? extends Converter> converterClass;

    private final UniversalGenerate<Codec> generate;

    private final UseRawJudge useRawJudge;

    public JacksonXmlBeanSerializerModifier(Converter<?> converter, UseRawJudge useRawJudge) {
        this.converterClass = converter != null ? converter.getClass() : null;
        this.generate = DataCodecGenerate.getDataCodecGenerate(converter);
        this.useRawJudge = useRawJudge;
    }

    public JacksonXmlBeanSerializerModifier(Converter<?> converter, UniversalGenerate<Codec> generate, UseRawJudge useRawJudge) {
        this.converterClass = converter != null ? converter.getClass() : null;
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);
        this.useRawJudge = useRawJudge;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        if (useRawJudge.useRawImpl(beanDesc.getBeanClass())) {
            return super.changeProperties(config, beanDesc, beanProperties);
        }
        List<BeanPropertyWriter> resultList = new LinkedList<>();
        Map<String, BeanPropertyWriter> propertyWriterMap = beanProperties.stream()
                .collect(Collectors.toMap(BeanPropertyWriter::getName, beanPropertyWriter -> beanPropertyWriter));
        NameTransformer nameTransformer = PropertyNameTransformer.get(beanDesc.getBeanClass(), converterClass);
        JavaBeanInfo javaBeanInfo = XmlClassInfoStorage.INSTANCE.getJavaBeanInfo(beanDesc.getBeanClass(), converterClass);
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        // 去除不序列化的属性
        sortedFieldList.stream().filter(FieldInfo::isSerialize).forEach(fieldInfo -> {
            BeanPropertyWriter bpw = propertyWriterMap.get(fieldInfo.getFieldName());
            // 修改时间类型序列化类
            JsonSerializer<Object> dateTimeSerializer = createCoreCodecSerializer(fieldInfo);
            if (dateTimeSerializer != null) {
                bpw.assignSerializer(dateTimeSerializer);
            }
            // 修改key值
            boolean isCData = (boolean) fieldInfo.getExpandProperty("isCData", false);
            boolean isAttribute = (boolean) fieldInfo.getExpandProperty("isAttribute", false);
            boolean isText = (boolean) fieldInfo.getExpandProperty("isText", false);
            String namespace = (String) fieldInfo.getExpandProperty("namespace");
            bpw.setInternalSetting(XmlBeanSerializerBase.KEY_XML_INFO,
                    new XmlInfo(isAttribute, namespace, isText, isCData));
            // 修改key值
            BeanPropertyWriter newWriter = bpw.rename(nameTransformer);
            if (TypeUtil.isIndexedType(newWriter.getType())) {
                PropertyName wrappedName = PropertyName.construct(newWriter.getName(), namespace);
                PropertyName wrapperName = newWriter.getWrapperName();

                // first things first: no wrapping?
                if (wrapperName != null && wrapperName != PropertyName.NO_NAME) {
                    // no local name? Just double the wrapped name for wrapper
                    String localName = wrapperName.getSimpleName();
                    if (localName == null || localName.length() == 0) {
                        wrapperName = wrappedName;
                    }
                    resultList.add(new XmlBeanPropertyWriter(newWriter, wrapperName, wrappedName));
                } else {
                    resultList.add(newWriter);
                }
            } else {
                resultList.add(newWriter);
            }
        });
        return resultList;
    }

    private JsonSerializer<Object> createCoreCodecSerializer(FieldInfo fieldInfo) {
        if (fieldInfo == null) {
            return null;
        }
        // java8时间类型需设置format
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

        JacksonCoreCodecSerializer coreCodecSerializer = UniversalCodecAdapterCreator.create(this.generate, beanClass,
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
