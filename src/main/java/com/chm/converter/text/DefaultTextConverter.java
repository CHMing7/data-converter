package com.chm.converter.text;

import com.chm.converter.Converter;

import java.lang.reflect.Type;

/**
 * 默认的文本数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
public class DefaultTextConverter implements Converter<String> {

    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        return (T) source;
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        return (T) source;
    }
}
