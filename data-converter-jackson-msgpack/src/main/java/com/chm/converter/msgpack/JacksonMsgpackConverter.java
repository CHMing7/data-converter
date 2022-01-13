package com.chm.converter.msgpack;

import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.jackson.deserializer.JacksonBeanDeserializerModifier;
import com.chm.converter.jackson.serializer.JacksonBeanSerializerModifier;
import com.chm.converter.msgpack.jackson.JacksonMsgpackModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Jackson msgpack数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-29
 **/
public class JacksonMsgpackConverter implements MsgpackConverter {

    public static final List<Class<? extends Annotation>> JACKSON_MSGPACK_ANNOTATION_LIST = ListUtil.of(JsonProperty.class);

    public static final String[] MSGPACK_NAME_ARRAY = new String[]{"org.msgpack.jackson.dataformat.MessagePackFactory",
            "com.fasterxml.jackson.databind.ObjectMapper"};

    protected ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());

    {
        SimpleModule module = new JacksonMsgpackModule(this);
        module.setSerializerModifier(new JacksonBeanSerializerModifier(this, JacksonMsgpackConverter::checkExistJacksonMsgpackAnnotation));
        module.setDeserializerModifier(new JacksonBeanDeserializerModifier(this, JacksonMsgpackConverter::checkExistJacksonMsgpackAnnotation));
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
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        try {
            return mapper.readValue(source, targetType);
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), byte[].class.getName(), targetType.getName(), e);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        try {
            return mapper.readValue(source, mapper.getTypeFactory().constructType(targetType));
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), byte[].class.getName(), targetType.getTypeName(), e);
        }
    }

    public <T> T convertToJavaObject(byte[] source, Class<?> parametrized, Class<?>... parameterClasses) {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
        try {
            return mapper.readValue(source, javaType);
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), byte[].class.getName(), javaType.getTypeName(), e);
        }
    }

    public <T> T convertToJavaObject(byte[] source, JavaType javaType) {
        try {
            return mapper.readValue(source, javaType);
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), byte[].class.getName(), javaType.getTypeName(), e);
        }
    }

    @Override
    public byte[] encode(Object source) {
        if (source == null) {
            return new byte[0];
        }
        try {
            return mapper.writeValueAsBytes(source);
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), source.getClass().getName(), byte[].class.getName(), e);
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
            // 检测Jackson-Msgpack相关类型是否存在
            for (String msgpackName : MSGPACK_NAME_ARRAY) {
                Class.forName(msgpackName);
            }
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean checkExistJacksonMsgpackAnnotation(Class<?> cls) {
        return JavaBeanInfo.checkExistAnnotation(cls, JACKSON_MSGPACK_ANNOTATION_LIST);
    }
}
