package com.chm.converter.codec;

import java.lang.reflect.Type;

/**
 * 编解码器工厂
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public interface CodecFactory {

    /**
     * 创建编解码器
     *
     * @param type
     * @param <E>
     * @param <D>
     * @return
     */
    <E, D> Codec<E, D> createCodec(Type type);
}
