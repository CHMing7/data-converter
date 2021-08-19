package com.chm.converter.core;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-13
 **/
public interface Encoder {

    /**
     * 将java对象转化成字符串
     *
     * @param obj
     * @return
     */
    String encodeToString(Object obj);
}
