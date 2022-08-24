package com.chm.converter.core.exception;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public class CodecException extends AbstractRuntimeException {

    public CodecException() {
        super();
    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(String messageTemplate, Object... params) {
        super(messageTemplate, params);
    }

    public CodecException(Throwable cause) {
        super(cause);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodecException(Throwable throwable, String messageTemplate, Object... params) {
        super(throwable, messageTemplate, params);
    }
}
