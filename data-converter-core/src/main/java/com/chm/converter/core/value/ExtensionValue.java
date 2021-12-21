package com.chm.converter.core.value;

/**
 * 扩展类型
 *
 * @author caihongming
 * @version v1.0
 * @see Value
 * @since 2021-12-04
 */
public interface ExtensionValue extends Value {

    /**
     * 返回该值具体类型
     *
     * @return
     */
    Object getData();
}
