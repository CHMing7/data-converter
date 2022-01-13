package com.chm.converter.thrift;

import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.thrift.utils.Thrift;

import java.lang.reflect.Type;

/**
 * 默认thrift数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-30
 **/
public class DefaultThriftConverter implements ThriftConverter {

    public static final String THRIFT_NAME = "org.apache.thrift.TBase";

    protected Thrift thrift = new Thrift(this);

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        return convertToJavaObject(source, (Type) targetType);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        if (source == null) {
            return null;
        }
        try {
            return thrift.deserialize(source, targetType);
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
            return thrift.serialize(source);
        } catch (Throwable e) {
            throw new ConvertException(getConverterName(), source.getClass().getName(), byte[].class.getName(), e);
        }
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Thrift相关类型是否存在
            Class.forName(THRIFT_NAME);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
