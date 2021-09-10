package com.chm.converter.text;

import com.chm.converter.core.ConverterSelector;

import java.lang.reflect.Type;

/**
 * 默认文本数据转换接口实现
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-10
 **/
public class DefaultTextConverter implements TextConverter {

    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        return (T) source;
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        return (T) source;
    }

    @Override
    public String encodeToString(Object obj) {
        return null != obj ? obj.toString() : null;
    }

    @Override
    public boolean loadConverter() {
        ConverterSelector.put(DefaultTextConverter.class, new DefaultTextConverter());
        return true;
    }
}
