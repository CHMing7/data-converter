package com.chm.converter.thrift;

import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.thrift.utils.ThriftUtil;

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

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        try {
            return ThriftUtil.deserialize(source, targetType);
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
        try {
            return ThriftUtil.serialize(source);
        } catch (Exception e) {
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
