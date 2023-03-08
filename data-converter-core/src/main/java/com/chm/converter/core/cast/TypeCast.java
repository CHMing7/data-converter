package com.chm.converter.core.cast;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-06-09
 **/
@FunctionalInterface
public interface TypeCast<T> {

    String NULL = "null";

    String TRUE = "true";

    String NUMBER_1 = "1";

    /**
     * 将指定对象转化为{@link T}
     *
     * @param value 指定对象
     * @return {@link T} or null
     */
    T cast(Object value);
}
