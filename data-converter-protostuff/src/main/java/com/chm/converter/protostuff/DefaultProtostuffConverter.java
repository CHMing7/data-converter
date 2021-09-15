package com.chm.converter.protostuff;

import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.protostuff.utils.WrapperUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-15
 **/
public class DefaultProtostuffConverter implements ProtostuffConverter {

    public static final String PROTOSTUFF_NAME = "io.protostuff.ProtostuffIOUtil";

    /**
     * 缓存Schema
     */
    private final static Map<Class<?>, Schema<?>> SCHEMA_CACHE = new ConcurrentHashMap<>();

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        Object result;
        if (WrapperUtil.needWrapper(targetType)) {
            Schema<Wrapper> schema = getSchema(Wrapper.class);
            Wrapper wrapper = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(source, wrapper, schema);
            return (T) wrapper.getData();
        } else {
            Schema schema = getSchema(targetType);
            result = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(source, result, schema);
        }
        return (T) result;
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        if (source == null) {
            return null;
        }
        Class<?> classByType = ClassUtil.getClassByType(targetType);
        return (T) convertToJavaObject(source, classByType);
    }

    @Override
    public byte[] encode(Object obj) {
        if (obj == null || WrapperUtil.needWrapper(obj)) {
            Schema<Wrapper> schema = getSchema(Wrapper.class);
            Wrapper wrapper = new Wrapper(obj);
            return ProtostuffIOUtil.toByteArray(wrapper, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        } else {
            Schema schema = getSchema(obj.getClass());
            return ProtostuffIOUtil.toByteArray(obj, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        }
    }

    private static Schema getSchema(Class<?> cls) {
        return SCHEMA_CACHE.computeIfAbsent(cls, RuntimeSchema::createFrom);
    }

    /**
     * 检测Protobuf相关类型
     *
     * @return Jackson相关类型
     */
    public static Class<?> checkProtostuffClass() throws Throwable {
        return Class.forName(PROTOSTUFF_NAME);
    }

    @Override
    public boolean loadConverter() {
        try {
            checkProtostuffClass();
            ConverterSelector.put(DefaultProtostuffConverter.class, this);
        } catch (Throwable ignored) {
            return false;
        }
        return true;
    }
}
