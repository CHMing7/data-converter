package com.chm.converter.core.exception;

import com.chm.converter.core.utils.StringUtil;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-13
 **/
public class ConvertException extends RuntimeException {

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(Throwable th) {
        super(th);
    }

    public ConvertException(String message, Throwable th) {
        super(message, th);
    }

    public ConvertException(String converterName, String sourceClassName, String targetClassName, Throwable th) {
        super(StringUtil.format("Converter '{}' Conversion Type '{}' To '{}' Error: {}", converterName,
                sourceClassName, targetClassName, th.getMessage()), th);
    }

    public ConvertException(String converterName, Class<?> sourceClass, Class<?> targetClass, Throwable th) {
        super(StringUtil.format("Converter '{}' Conversion Type '{}' To '{}' Error: {}", converterName,
                sourceClass.getName(), targetClass.getName(), th.getMessage()), th);
    }
}
