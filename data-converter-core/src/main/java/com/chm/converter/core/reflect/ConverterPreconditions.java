package com.chm.converter.core.reflect;

import com.chm.converter.core.utils.StringUtil;

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

    public static <T> T checkNotNull(T reference, String errorMessage, Object... params) {
        if (reference == null) {
            throw new NullPointerException(StringUtil.format(errorMessage, params));
        }
        return reference;
    }

    public static void checkArgument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean condition, String errorMessage, Object... params) {
        if (!condition) {
            throw new IllegalArgumentException(StringUtil.format(errorMessage, params));
        }
    }
}
