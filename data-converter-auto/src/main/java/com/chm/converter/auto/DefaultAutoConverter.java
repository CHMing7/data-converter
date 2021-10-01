package com.chm.converter.auto;


import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ClassUtil;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * @author caihongming
 * @version v1.0
 * @since 2020-12-31
 **/
public class DefaultAutoConverter implements AutoConverter {

    @Override
    public <T> T convertToJavaObject(Object source, Class<T> targetType) {
        if (source instanceof InputStream
                || source instanceof byte[]
                || source instanceof File) {
            return tryConvert(source, targetType, DataType.BINARY);
        }
        T result = null;
        if (source instanceof CharSequence) {
            String str = source.toString();
            if (String.class.isAssignableFrom(targetType)) {
                return (T) str;
            }
            String trimmedStr = str.trim();
            char ch = trimmedStr.charAt(0);
            try {
                if (ch == '{' || ch == '[') {
                    result = tryConvert(trimmedStr, targetType, DataType.JSON);
                } else if (ch == '<') {
                    result = tryConvert(trimmedStr, targetType, DataType.XML);
                } else if (Character.isDigit(ch)) {
                    try {
                        result = tryConvert(trimmedStr, targetType, DataType.JSON);
                    } catch (Throwable th) {
                        result = tryConvert(source, targetType, DataType.TEXT);
                    }
                } else if ("true".equalsIgnoreCase(trimmedStr)) {
                    if (boolean.class.isAssignableFrom(targetType) || Boolean.class.isAssignableFrom(targetType)) {
                        result = (T) Boolean.TRUE;
                    } else {
                        result = tryConvert(trimmedStr, targetType, DataType.TEXT);
                    }
                } else if ("false".equalsIgnoreCase(trimmedStr)) {
                    if (boolean.class.isAssignableFrom(targetType) || Boolean.class.isAssignableFrom(targetType)) {
                        result = (T) Boolean.FALSE;
                    } else {
                        result = tryConvert(trimmedStr, targetType, DataType.TEXT);
                    }
                } else {
                    result = tryConvert(source, targetType, DataType.TEXT);
                }
            } catch (Throwable th) {
                try {
                    result = tryConvert(trimmedStr, targetType, DataType.TEXT);
                } catch (Throwable th2) {
                    throw new ConvertException(getConverterName(), source.getClass().getName(), targetType.getName(), th);
                }
            }
        }
        return result;
    }

    private <T> T tryConvert(Object source, Class<T> targetType, DataType dataType) {
        return (T) ConverterSelector.select(dataType).convertToJavaObject(source, (Class<T>) targetType);
    }

    private <T> T tryConvert(Object source, Type targetType, DataType dataType) {
        return (T) ConverterSelector.select(dataType).convertToJavaObject(source, targetType);
    }


    @Override
    public <T> T convertToJavaObject(Object source, Type targetType) {
        if (source instanceof InputStream
                || source instanceof byte[]
                || source instanceof File) {
            return tryConvert(source, targetType, DataType.BINARY);
        }
        T result = null;
        Class<?> clazz = ClassUtil.getClassByType(targetType);
        if (source instanceof CharSequence) {
            String str = source.toString();
            if (String.class.isAssignableFrom(clazz)) {
                return (T) str;
            }
            String trimmedStr = str.trim();
            char ch = trimmedStr.charAt(0);
            try {
                if (ch == '{' || ch == '[') {
                    result = tryConvert(trimmedStr, targetType, DataType.JSON);
                } else if (ch == '<') {
                    result = tryConvert(trimmedStr, targetType, DataType.XML);
                } else if (Character.isDigit(ch)) {
                    try {
                        result = tryConvert(trimmedStr, targetType, DataType.JSON);
                    } catch (Throwable th) {
                        result = tryConvert(source, targetType, DataType.TEXT);
                    }
                } else if ("true".equalsIgnoreCase(trimmedStr)) {
                    if (boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)) {
                        result = (T) Boolean.TRUE;
                    } else {
                        result = tryConvert(trimmedStr, targetType, DataType.TEXT);
                    }
                } else if ("false".equalsIgnoreCase(trimmedStr)) {
                    if (boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)) {
                        result = (T) Boolean.FALSE;
                    } else {
                        result = tryConvert(trimmedStr, targetType, DataType.TEXT);
                    }
                } else {
                    result = tryConvert(source, targetType, DataType.TEXT);
                }
            } catch (Throwable th) {
                try {
                    result = tryConvert(trimmedStr, targetType, DataType.TEXT);
                } catch (Throwable th2) {
                    throw new ConvertException(getConverterName(), source.getClass().getName(), targetType.getTypeName(), th);
                }
            }
        }
        return result;
    }

    @Override
    public Object encode(Object source) {
        return null;
    }

    @Override
    public boolean checkCanBeLoad() {
        return true;
    }

}
