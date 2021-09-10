package com.chm.converter.core.creator;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-07
 **/
public interface ObjectConstructor<T> {

    /**
     * 返回一个新实例
     *
     * @return
     */
    T construct();
}
