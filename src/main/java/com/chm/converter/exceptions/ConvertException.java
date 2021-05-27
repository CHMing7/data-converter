package com.chm.converter.exceptions;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
public class ConvertException extends RuntimeException {

    public ConvertException(String converterName, Throwable th) {
        super("Converter '" + converterName + "' Error: " + th.getMessage(), th);
    }
}
