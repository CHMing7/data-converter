package com.chm.converter.json;

import cn.hutool.core.util.StrUtil;
import com.chm.converter.Converter;
import com.chm.converter.exceptions.ConvertException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 使用Jackson实现的消息转折实现类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
public class JacksonConverter implements JsonConverter {

    /**
     * 日期格式
     */
    private String dateFormat;

    protected ObjectMapper mapper = new ObjectMapper();

    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
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
    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public Converter setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        if (StrUtil.isNotBlank(dateFormat)) {
            DateFormat format = new SimpleDateFormat(dateFormat);
            mapper.setDateFormat(format);
        }
        return this;
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
    public String encodeToString(Object obj) {
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

}
