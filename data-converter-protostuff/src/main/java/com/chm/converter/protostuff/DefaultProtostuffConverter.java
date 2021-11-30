package com.chm.converter.protostuff;

import com.chm.converter.core.exception.ConvertException;

import java.lang.reflect.Type;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-15
 **/
public class DefaultProtostuffConverter implements ProtostuffConverter {

    public static final String PROTOSTUFF_NAME = "io.protostuff.ProtostuffIOUtil";

    protected Protostuff protostuff = new Protostuff(this);

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        if (source == null) {
            return null;
        }

        try {
            return protostuff.deserialize(source, targetType);
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
            return protostuff.deserialize(source, targetType);
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
            return protostuff.serialize(source);
        } catch (Throwable e) {
            throw new ConvertException(getConverterName(), source.getClass().getName(), byte[].class.getName(), e);
        }
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Protostuff相关类型是否存在
            Class.forName(PROTOSTUFF_NAME);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

}
