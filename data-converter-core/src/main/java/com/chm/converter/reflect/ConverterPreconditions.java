package com.chm.converter.reflect;

/**
 * 此类参考Gson的$Gson$Preconditions实现
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-07
 **/
public class ConverterPreconditions {

    private ConverterPreconditions() {
        throw new UnsupportedOperationException();
    }

    public static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static void checkArgument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }
}
