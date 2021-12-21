package com.chm.converter.core.exception;

/**
 * 类型转换异常
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-12-04
 **/
public class TypeCastException extends RuntimeException {

    public TypeCastException() {
        super();
    }

    public TypeCastException(String message) {
        super(message);
    }

    public TypeCastException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeCastException(Throwable cause) {
        super(cause);
    }
}