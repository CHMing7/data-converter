package com.chm.converter.core.exception;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public class CodecException extends RuntimeException {

    public CodecException() {
        super();
    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(Throwable cause) {
        super(cause);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
