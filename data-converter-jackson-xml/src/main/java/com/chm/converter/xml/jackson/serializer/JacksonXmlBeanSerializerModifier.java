package com.chm.converter.xml.jackson.serializer;

import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.UseOriginalJudge;
import com.chm.converter.jackson.PropertyNameTransformer;
import com.chm.converter.jackson.serializer.JacksonDefaultDateTypeSerializer;
import com.chm.converter.jackson.serializer.JacksonJava8TimeSerializer;
import com.chm.converter.xml.XmlClassInfoStorage;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.dataformat.xml.ser.XmlBeanPropertyWriter;
import com.fasterxml.jackson.dataformat.xml.ser.XmlBeanSerializerBase;
import com.fasterxml.jackson.dataformat.xml.ser.XmlBeanSerializerModifier;
import com.fasterxml.jackson.dataformat.xml.util.TypeUtil;
import com.fasterxml.jackson.dataformat.xml.util.XmlInfo;

import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-08
 **/
public class JacksonXmlBeanSerializerModifier extends XmlBeanSerializerModifier {

    private final Converter<?> converter;

    private final UseOriginalJudge useOriginalJudge;

    public JacksonXmlBeanSerializerModifier(Converter<?> converter, UseOriginalJudge useOriginalJudge) {
        this.converter = converter;
        this.useOriginalJudge = useOriginalJudge;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        if (useOriginalJudge.useOriginalImpl(beanDesc.getBeanClass())) {
            return super.changeProperties(config, beanDesc, beanProperties);
        }
        List<BeanPropertyWriter> resultList = new LinkedList<>();
        Map<String, BeanPropertyWriter> propertyWriterMap = beanProperties.stream()
                .collect(Collectors.toMap(BeanPropertyWriter::getName, beanPropertyWriter -> beanPropertyWriter));
        NameTransformer nameTransformer = PropertyNameTransformer.get(beanDesc.getBeanClass());
        JavaBeanInfo javaBeanInfo = XmlClassInfoStorage.INSTANCE.getJavaBeanInfo(beanDesc.getBeanClass());
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        // 去除不序列化的属性
        sortedFieldList.stream().filter(FieldInfo::isSerialize).forEach(fieldInfo -> {
            BeanPropertyWriter bpw = propertyWriterMap.get(fieldInfo.getFieldName());
            // 修改时间类型序列化类
            JsonSerializer<Object> dateTimeSerializer = createDateTimeSerializer(bpw, fieldInfo);
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
            if (TypeUtil.isIndexedType(bpw.getType())) {
                PropertyName wrappedName = PropertyName.construct(bpw.getName(), namespace);
                PropertyName wrapperName = bpw.getWrapperName();

                // first things first: no wrapping?
                if (wrapperName != null && wrapperName != PropertyName.NO_NAME) {
                    // no local name? Just double the wrapped name for wrapper
                    String localName = wrapperName.getSimpleName();
                    if (localName == null || localName.length() == 0) {
                        wrapperName = wrappedName;
                    }
                    resultList.add(new XmlBeanPropertyWriter(bpw, wrapperName, wrappedName));
                } else {
                    resultList.add(newWriter);
                }
            } else {
                resultList.add(newWriter);
            }
        });
        return resultList;
    }

    private JsonSerializer<Object> createDateTimeSerializer(BeanPropertyWriter propertyWriter, FieldInfo fieldInfo) {
        // java8时间类型需设置format
        String format = fieldInfo != null ? fieldInfo.getFormat() : null;
        Class<?> cls = propertyWriter.getType().getRawClass();
        Optional<Class<? extends TemporalAccessor>> jdk8TimeFirst = TimeConstant.TEMPORAL_ACCESSOR_SET.stream()
                .filter(temporalAccessorClass -> temporalAccessorClass.isAssignableFrom(cls)).findFirst();
        JsonSerializer<Object> serializer = jdk8TimeFirst.map(clazz -> new JacksonJava8TimeSerializer(cls, format, converter)).orElse(null);
        if (serializer != null) {
            return serializer;
        }
        Optional<Class<? extends Date>> defaultDateFirst = TimeConstant.DEFAULT_DATE_SET.stream()
                .filter(dateClass -> dateClass.isAssignableFrom(cls)).findFirst();
        return defaultDateFirst.map(clazz -> new JacksonDefaultDateTypeSerializer(format, converter)).orElse(null);
    }

}
