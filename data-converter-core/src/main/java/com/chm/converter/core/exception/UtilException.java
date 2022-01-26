package com.chm.converter.core.exception;

/**
 * 工具类异常
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-01-25
 **/
public class UtilException extends AbstractRuntimeException {

    public UtilException(Throwable e) {
        super(e);
    }

    public UtilException(String message) {
        super(message);
    }

    public UtilException(String messageTemplate, Object... params) {
        super(messageTemplate, params);
    }

    public UtilException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public UtilException(Throwable throwable, String messageTemplate, Object... params) {
        super(messageTemplate, params, throwable);
    }

}
