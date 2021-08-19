package com.chm.converter.exception;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-13
 **/
public class ConvertException extends RuntimeException {

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(String converterName, Throwable th) {
        super("Converter '" + converterName + "' Error: " + th.getMessage(), th);
    }
}
