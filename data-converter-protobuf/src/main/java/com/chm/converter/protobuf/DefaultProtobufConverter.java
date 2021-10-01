package com.chm.converter.protobuf;

import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.protobuf.utils.ProtobufUtil;

import java.lang.reflect.Type;

/**
 * 默认Protobuf数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-13
 **/
public class DefaultProtobufConverter implements ProtobufConverter {

    public static final String PROTOBUF_NAME = "com.google.protobuf.Parser";

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        try {
            return ProtobufUtil.deserialize(source, targetType);
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), byte[].class.getName(), targetType.getName(), e);
        }
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
    public byte[] encode(Object source) {
        if (source == null) {
            return new byte[0];
        }
        return ProtobufUtil.serialize(source);
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Protobuf相关类型是否存在
            Class.forName(PROTOBUF_NAME);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
