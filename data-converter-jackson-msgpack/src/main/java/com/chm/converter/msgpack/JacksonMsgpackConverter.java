package com.chm.converter.msgpack;

import cn.hutool.core.collection.ListUtil;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.StringUtil;
import com.chm.converter.jackson.deserializer.JacksonBeanDeserializerModifier;
import com.chm.converter.jackson.serializer.JacksonBeanSerializerModifier;
import com.chm.converter.msgpack.jackson.JacksonMsgpackModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

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

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        try {
            return mapper.readValue(source, targetType);
        } catch (IOException e) {
            throw new ConvertException(StringUtil.format("bytes data cannot be msgpack deserialized to type: {}", targetType.getName()), e);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        try {
            return mapper.readValue(source, mapper.getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ConvertException(StringUtil.format("bytes data cannot be msgpack deserialized to type: {}", targetType.getTypeName()), e);
        }
    }

    @Override
    public byte[] encode(Object source) {
        if (source == null) {
            return new byte[0];
        }
        try {
            return mapper.writeValueAsBytes(source);
        } catch (JsonProcessingException e) {
            throw new ConvertException(StringUtil.format("data cannot be serialized to msgpack bytes, data type: {}", source.getClass()), e);
        }
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Hessian相关类型是否存在
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
