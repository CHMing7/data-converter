package com.chm.converter.core.value;

/**
 * Class 类型
 *
 * @author caihongming
 * @version v1.0
 * @see Value
 * @since 2021-12-04
 */
public interface ClassValue extends Value {

    /**
     * 以Class形式返回
     *
     * @return
     */
    Class<?> toClass();
}
