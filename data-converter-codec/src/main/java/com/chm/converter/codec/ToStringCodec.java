package com.chm.converter.codec;

/**
 * toString编解码器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public abstract class ToStringCodec<T> implements Codec<T, String> {

    @Override
    public String encode(T t) {
        return t != null ? t.toString() : null;
    }

}
