package com.chm.converter.core.exception;

import java.math.BigInteger;

/**
 * 当用户尝试使用较小的类型读取整数值时会引发此错误。
 * 例如，为大于 Integer.MAX_VALUE 的整数值调用 DataUnpacker.unpackInt() 将导致此异常。
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-12-06
 **/
public class IntegerOverflowException extends RuntimeException {

    private final BigInteger bigInteger;

    public IntegerOverflowException(BigInteger bigInteger) {
        super();
        this.bigInteger = bigInteger;
    }

    public IntegerOverflowException(long value) {
        this(BigInteger.valueOf(value));
    }

    public IntegerOverflowException(String message, BigInteger bigInteger) {
        super(message);
        this.bigInteger = bigInteger;
    }

    public BigInteger getBigInteger() {
        return bigInteger;
    }

    @Override
    public String getMessage() {
        return bigInteger.toString();
    }
}
