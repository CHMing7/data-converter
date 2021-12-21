package com.chm.converter.core.value;

import com.chm.converter.core.exception.IntegerOverflowException;

import java.math.BigInteger;

/**
 * Integer 类型
 *
 * @author caihongming
 * @version v1.0
 * @see NumberValue
 * @since 2021-12-04
 */
public interface IntegerValue extends NumberValue {

    /**
     * 如果该值在 [-2<sup>7</sup> 到 2<sup>7</sup>-1] 的范围内，则返回 true。
     *
     * @return
     */
    boolean isInByteRange();

    /**
     * 如果该值在 [-2<sup>15</sup> 到 2<sup>15</sup>-1] 的范围内，则返回 true。
     *
     * @return
     */
    boolean isInShortRange();

    /**
     * 如果该值在 [-2<sup>31</sup> 到 2<sup>31</sup>-1] 的范围内，则返回 true。
     *
     * @return
     */
    boolean isInIntRange();

    /**
     * 如果该值在 [-2<sup>63</sup> 到 2<sup>63</sup>-1] 的范围内，则返回 true。
     *
     * @return
     */
    boolean isInLongRange();

    /**
     * 转为{@code byte}类型值返回
     *
     * @return
     * @throws IntegerOverflowException 如果该值不在 {@code byte} 类型的范围则抛出
     */
    byte asByte();

    /**
     * 转为{@code short}类型值返回
     *
     * @return
     * @throws IntegerOverflowException 如果该值不在 {@code short} 类型的范围则抛出
     */
    short asShort();

    /**
     * 转为{@code int}类型值返回
     *
     * @return
     * @throws IntegerOverflowException 如果该值不在 {@code int} 类型的范围则抛出
     */
    int asInt();

    /**
     * 转为{@code long}类型值返回
     *
     * @return
     * @throws IntegerOverflowException 如果该值不在 {@code long} 类型的范围则抛出
     */
    long asLong();

    /**
     * 转为{@code BigInteger}类型值返回
     *
     * @return
     */
    BigInteger asBigInteger();
}
