package com.chm.converter.xml;

import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.xml.jackson.JacksonXmlModule;
import com.chm.converter.xml.jackson.deserializer.JacksonXmlBeanDeserializerModifier;
import com.chm.converter.xml.jackson.serializer.JacksonXmlBeanSerializerModifier;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-08
 **/

public class JacksonXmlConverter implements XmlConverter {

    public static final List<Class<? extends Annotation>> JACKSON_XML_ANNOTATION_LIST = ListUtil.of(JacksonXmlCData.class,
            JacksonXmlElementWrapper.class, JacksonXmlProperty.class, JacksonXmlRootElement.class, JacksonXmlText.class,
            JsonProperty.class);

    public static final String JACKSON_XML_NAME = "com.fasterxml.jackson.dataformat.xml.XmlMapper";

    protected ObjectMapper mapper;

    {
        JacksonXmlModule module = new JacksonXmlModule(this);
        module.setXMLTextElementName("jacksonXmlTextElementName@@@###");
        module.setSerializerModifier(new JacksonXmlBeanSerializerModifier(this, JacksonXmlConverter::checkExistJacksonXmlAnnotation));
        module.setDeserializerModifier(new JacksonXmlBeanDeserializerModifier("jacksonXmlTextElementName@@@###", this, JacksonXmlConverter::checkExistJacksonXmlAnnotation));
        mapper = new XmlMapper(module);
        mapper.registerModule(module);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 获取Jackson的Mapper对象
     *
     * @return Jackson的Mapper对象，{@link ObjectMapper}类实例
     */
    public ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        try {
            return mapper.readValue(source, targetType);
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), String.class.getName(), targetType.getName(), e);
        }
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return mapper.readValue(source, mapper.getTypeFactory().constructType(targetType));
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), String.class.getName(), targetType.getTypeName(), e);
        }

    }

    public <T> T convertToJavaObject(String source, Class<?> parametrized, Class<?>... parameterClasses) {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
        try {
            return mapper.readValue(source, javaType);
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), String.class.getName(), javaType.getTypeName(), e);
        }
    }

    public <T> T convertToJavaObject(String source, JavaType javaType) {
        try {
            return mapper.readValue(source, javaType);
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), String.class.getName(), javaType.getTypeName(), e);
        }
    }

    @Override
    public String encode(Object source) {
        try {
            return mapper.writeValueAsString(source);
        } catch (Throwable e) {
            throw new ConvertException(getConverterName(), source.getClass().getName(), String.class.getName(), e);
        }
    }

    public Map<String, Object> convertObjectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof CharSequence) {
            return convertToJavaObject(obj.toString(), LinkedHashMap.class);
        }

        JavaType javaType = mapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class);
        return mapper.convertValue(obj, javaType);
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Jackson-Xml相关类型是否存在
            Class.forName(JACKSON_XML_NAME);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean checkExistJacksonXmlAnnotation(Class<?> cls) {
        return JavaBeanInfo.checkExistAnnotation(cls, JACKSON_XML_ANNOTATION_LIST);
    }
}
