package com.chm.converter.core.value;

/**
 * Boolean 类型
 *
 * @author caihongming
 * @version v1.0
 * @see Value
 * @since 2021-12-04
 */
public interface BooleanValue extends Value {

    /**
     * 将值作为 {@code boolean} 返回
     *
     * @return
     */
    boolean getBoolean();
}
