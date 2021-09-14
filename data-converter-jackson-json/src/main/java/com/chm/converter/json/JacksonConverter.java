package com.chm.converter.json;

import cn.hutool.core.collection.ListUtil;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.json.jackson.JacksonModule;
import com.chm.converter.json.jackson.deserializer.JacksonBeanDeserializerModifier;
import com.chm.converter.json.jackson.serializer.JacksonBeanSerializerModifier;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
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
        } catch (IOException e) {
            throw new ConvertException("json", e);
        }
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return mapper.readValue(source, mapper.getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ConvertException("json", e);
        }

    }

    public <T> T convertToJavaObject(String source, Class<?> parametrized, Class<?>... parameterClasses) {
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return mapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new ConvertException("json", e);
        }
    }

    public <T> T convertToJavaObject(String source, JavaType javaType) {
        try {
            return mapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new ConvertException("json", e);
        }
    }

    @Override
    public String encode(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Throwable e) {
            throw new ConvertException("json", e);
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

    /**
     * 检测Jackson相关类型
     *
     * @return Jackson相关类型
     */
    public static Class<?> checkJacksonClass() throws Throwable {
        return Class.forName(JACKSON_NAME);
    }

    @Override
    public boolean loadConverter() {
        try {
            checkJacksonClass();
            ConverterSelector.put(JacksonConverter.class, this);
        } catch (Throwable ignored) {
            return false;
        }
        return true;
    }

    public static boolean checkExistJacksonAnnotation(Class<?> cls) {
        return JavaBeanInfo.checkExistAnnotation(cls, JACKSON_ANNOTATION_LIST);
    }
}
