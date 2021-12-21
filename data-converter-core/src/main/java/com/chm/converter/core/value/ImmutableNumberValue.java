package com.chm.converter.core.value;

/**
 * {@link ImmutableIntegerValue} 和 {@link ImmutableFloatValue} 接口的不可变父接口
 * 要提取原始类型值，请调用 toXXX 方法，该方法可能会因舍入或截断而丢失一些信息。
 *
 * @author caihongming
 * @version v1.0
 * @see NumberValue
 * @see ImmutableValue
 * @see ImmutableIntegerValue
 * @see ImmutableFloatValue
 * @since 2021-12-04
 */
public interface ImmutableNumberValue extends NumberValue, ImmutableValue {
}
