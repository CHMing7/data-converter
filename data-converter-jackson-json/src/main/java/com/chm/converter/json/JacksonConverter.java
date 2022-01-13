package com.chm.converter.json;

import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.jackson.deserializer.JacksonBeanDeserializerModifier;
import com.chm.converter.jackson.serializer.JacksonBeanSerializerModifier;
import com.chm.converter.json.jackson.JacksonModule;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用Jackson实现的消息转折实现类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
public class JacksonConverter implements JsonConverter {

    public static final List<Class<? extends Annotation>> JACKSON_ANNOTATION_LIST = ListUtil.of(JsonIgnore.class,
            JsonIgnoreProperties.class, JsonIgnoreType.class, JsonAutoDetect.class, JsonSetter.class,
            JsonAnySetter.class, JsonCreator.class, JacksonInject.class, JsonDeserialize.class, JsonInclude.class,
            JsonGetter.class, JsonAnyGetter.class, JsonPropertyOrder.class, JsonRawValue.class, JsonValue.class,
            JsonSerialize.class);

    public static final String JACKSON_NAME = "com.fasterxml.jackson.databind.ObjectMapper";

    protected ObjectMapper mapper = new ObjectMapper();

    {
        SimpleModule module = new JacksonModule(this);
        module.setSerializerModifier(new JacksonBeanSerializerModifier(this, JacksonConverter::checkExistJacksonAnnotation));
        module.setDeserializerModifier(new JacksonBeanDeserializerModifier(this, JacksonConverter::checkExistJacksonAnnotation));
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

    @Override
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
            // 检测Jackson相关类型是否存在
            Class.forName(JACKSON_NAME);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean checkExistJacksonAnnotation(Class<?> cls) {
        return JavaBeanInfo.checkExistAnnotation(cls, JACKSON_ANNOTATION_LIST);
    }
}
