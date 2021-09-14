package com.chm.converter.protobuf;

import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.protobuf.utils.WrapperUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;

/**
 * 默认Protobuf数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-13
 **/
public class DefaultProtobufConverter implements ProtobufConverter {

    public static final String PROTOBUF_NAME = "io.protostuff.ProtobufIOUtil";

    @Override
    public <T> T convertToJavaObject(ByteBuffer source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        Object result;
        if (WrapperUtil.needWrapper(targetType)) {
            Schema<Wrapper> schema = RuntimeSchema.createFrom(Wrapper.class);
            Wrapper wrapper = schema.newMessage();
            ProtobufIOUtil.mergeFrom(source.array(), wrapper, schema);
            return (T) wrapper.getData();
        } else {
            Schema schema = RuntimeSchema.createFrom(targetType);
            result = schema.newMessage();
            ProtobufIOUtil.mergeFrom(source.array(), result, schema);
        }
        return (T) result;
    }

    @Override
    public <T> T convertToJavaObject(ByteBuffer source, Type targetType) {
        if (source == null) {
            return null;
        }
        Class<?> classByType = ClassUtil.getClassByType(targetType);
        return (T) convertToJavaObject(source, classByType);
    }

    @Override
    public ByteBuffer encode(Object obj) {
        if (obj == null || WrapperUtil.needWrapper(obj)) {
            Schema<Wrapper> schema = RuntimeSchema.getSchema(Wrapper.class);
            Wrapper wrapper = new Wrapper(obj);
            return ByteBuffer.wrap(ProtobufIOUtil.toByteArray(wrapper, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE)));
        } else {
            Schema schema = RuntimeSchema.getSchema(obj.getClass());
            return ByteBuffer.wrap(ProtobufIOUtil.toByteArray(obj, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE)));
        }
    }

    /**
     * 检测Protobuf相关类型
     *
     * @return Jackson相关类型
     */
    public static Class<?> checkProtobufClass() throws Throwable {
        return Class.forName(PROTOBUF_NAME);
    }

    @Override
    public boolean loadConverter() {
        try {
            checkProtobufClass();
            ConverterSelector.put(DefaultProtobufConverter.class, this);
        } catch (Throwable ignored) {
            return false;
        }
        return true;
    }
}
