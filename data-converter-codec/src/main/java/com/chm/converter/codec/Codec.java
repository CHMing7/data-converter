package com.chm.converter.codec;

import com.chm.converter.core.universal.UniversalInterface;

/**
 * 编解码器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public interface Codec<D, E> extends UniversalInterface {

    /**
     * 编码
     *
     * @param d
     * @return
     */
    E encode(D d);

    /**
     * 解码
     *
     * @param e
     * @return
     */
    D decode(E e);
}
