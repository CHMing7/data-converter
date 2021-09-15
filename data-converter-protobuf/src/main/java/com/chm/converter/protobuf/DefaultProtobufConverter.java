package com.chm.converter.protobuf;

import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.protobuf.utils.ProtobufUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

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
        } catch (InvalidProtocolBufferException e) {
            throw new ConvertException("Found a protobuf message but " + targetType.getName() + " had no getDefaultInstance() method", e);
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
        return ProtobufUtil.serialize(source);
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
