package com.chm.converter.core.value;

/**
 * Enum 类型
 *
 * @author caihongming
 * @version v1.0
 * @see Value
 * @since 2021-12-04
 */
public interface EnumValue extends Value {

    /**
     * 以Enum形式返回
     *
     * @return
     */
    Enum<?> toEnum();
}
