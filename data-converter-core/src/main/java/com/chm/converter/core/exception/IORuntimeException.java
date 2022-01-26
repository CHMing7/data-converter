package com.chm.converter.core.exception;

/**
 * IO运行时异常，常用于对IOException的包装
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-01-25
 **/
public class IORuntimeException extends AbstractRuntimeException {

    public IORuntimeException(Throwable e) {
        super(e);
    }

    public IORuntimeException(String message) {
        super(message);
    }

    public IORuntimeException(String messageTemplate, Object... params) {
        super(messageTemplate, params);
    }

    public IORuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public IORuntimeException(Throwable throwable, String messageTemplate, Object... params) {
        super(messageTemplate, params, params);
    }

}
