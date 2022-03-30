package com.chm.converter.protobuf;

import com.chm.converter.core.Converter;
import com.chm.converter.core.exception.ConvertException;
import com.google.auto.service.AutoService;

import java.lang.reflect.Type;

/**
 * 默认Protobuf数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-13
 **/
@AutoService(Converter.class)
public class DefaultProtobufConverter implements ProtobufConverter {

    public static final String PROTOBUF_NAME = "com.google.protobuf.Parser";

    protected Protobuf protobuf = new Protobuf(this);

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        if (source == null) {
            return null;
        }

        try {
            return protobuf.deserialize(source, targetType);
        } catch (Throwable e) {
            throw new ConvertException(getConverterName(), byte[].class.getName(), targetType.getName(), e);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        if (source == null) {
            return null;
        }

        try {
            return protobuf.deserialize(source, targetType);
        } catch (Throwable e) {
            throw new ConvertException(getConverterName(), byte[].class.getName(), targetType.getTypeName(), e);
        }
    }

    @Override
    public byte[] encode(Object source) {
        if (source == null) {
            return new byte[0];
        }

        try {
            return protobuf.serialize(source);
        } catch (Throwable e) {
            throw new ConvertException(getConverterName(), source.getClass().getName(), byte[].class.getName(), e);
        }
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
