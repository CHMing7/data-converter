package com.chm.converter.yaml;

import com.chm.converter.core.Converter;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.jackson.deserializer.JacksonBeanDeserializerModifier;
import com.chm.converter.jackson.serializer.JacksonBeanSerializerModifier;
import com.chm.converter.yaml.jackson.JacksonYamlModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.auto.service.AutoService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Jackson yaml数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-01-13
 **/
@AutoService(Converter.class)
public class JacksonYamlConverter implements YamlConverter {

    public static final List<Class<? extends Annotation>> JACKSON_YAML_ANNOTATION_LIST = ListUtil.of(JsonProperty.class);

    public static final String[] YAML_NAME_ARRAY = new String[]{"com.fasterxml.jackson.dataformat.yaml.YAMLFactory",
            "com.fasterxml.jackson.databind.ObjectMapper"};

    protected ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    {
        SimpleModule module = new JacksonYamlModule(this);
        module.setSerializerModifier(new JacksonBeanSerializerModifier(this, JacksonYamlConverter::checkExistJacksonYamlAnnotation));
        module.setDeserializerModifier(new JacksonBeanDeserializerModifier(this, JacksonYamlConverter::checkExistJacksonYamlAnnotation));
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

    public Map<String, Object> convertObjectToMap(byte[] obj) {
        if (obj == null) {
            return null;
        }

        JavaType javaType = mapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class);
        return mapper.convertValue(obj, javaType);
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Jackson-Yaml相关类型是否存在
            for (String yamlName : YAML_NAME_ARRAY) {
                Class.forName(yamlName);
            }
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean checkExistJacksonYamlAnnotation(Class<?> cls) {
        return JavaBeanInfo.checkExistAnnotation(cls, JACKSON_YAML_ANNOTATION_LIST);
    }
}
