package com.chm.converter;

/**
 * @author caihongming
 * @version v1.0
 * @since 2020-12-31
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
