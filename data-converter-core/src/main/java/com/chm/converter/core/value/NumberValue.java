package com.chm.converter.core.value;

import java.math.BigInteger;

/**
 * {@link IntegerValue} 和 {@link FloatValue} 接口的父接口
 * 要提取原始类型值，请调用 toXXX 方法，该方法可能会因舍入或截断而丢失一些信息。
 *
 * @author caihongming
 * @version v1.0
 * @see Value
 * @since 2021-12-04
 */
public interface NumberValue extends Value {

    /**
     * 将该值以{@code byte}形式表示返回
     *
     * @return
     */
    byte toByte();

    /**
     * 将该值以{@code short}形式表示返回
     *
     * @return
     */
    short toShort();

    /**
     * 将该值以{@code int}形式表示返回
     *
     * @return
     */
    int toInt();

    /**
     * 将该值以{@code long}形式表示返回
     *
     * @return
     */
    long toLong();

    /**
     * 将该值以{@code BigInteger}形式表示返回
     *
     * @return
     */
    BigInteger toBigInteger();

    /**
     * 将该值以{@code float}形式表示返回
     *
     * @return
     */
    float toFloat();

    /**
     * 将该值以{@code double}形式表示返回
     *
     * @return
     */
    double toDouble();
}
