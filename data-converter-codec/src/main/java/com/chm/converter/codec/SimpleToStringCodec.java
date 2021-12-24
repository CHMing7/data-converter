package com.chm.converter.codec;

/**
 * toString简易编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-03
 **/
public class SimpleToStringCodec<T> extends ToStringCodec<T> {

    private final Decode<T, String> simpleCodecDecode;

    private SimpleToStringCodec(Decode<T, String> simpleCodecDecode) {
        this.simpleCodecDecode = simpleCodecDecode;
    }

    public static <T> SimpleToStringCodec<T> create(Decode<T, String> simpleCodecDecode) {
        return new SimpleToStringCodec<>(simpleCodecDecode);
    }

    @Override
    public T decode(String s) {
        return s != null ? simpleCodecDecode.decode(s) : null;
    }
}
