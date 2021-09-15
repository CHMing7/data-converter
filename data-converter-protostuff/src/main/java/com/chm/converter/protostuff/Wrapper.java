package com.chm.converter.protostuff;

/**
 * 包装类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-13
 **/
public class Wrapper<T> {

    /**
     * 包装数据
     */
    private final T data;

    Wrapper(T data) {
        this.data = data;
    }

    Object getData() {
        return data;
    }
}
