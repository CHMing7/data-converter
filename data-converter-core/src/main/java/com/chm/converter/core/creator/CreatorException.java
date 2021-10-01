package com.chm.converter.core.creator;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-10-01
 **/
public class CreatorException extends RuntimeException {

    public CreatorException(String message) {
        super(message);
    }

    public CreatorException(String message, Throwable th) {
        super(message, th);
    }
}
