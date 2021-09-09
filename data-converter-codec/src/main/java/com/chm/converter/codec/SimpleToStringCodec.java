package com.chm.converter.codec;

/**
 * toString简易编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-03
 **/
public class SimpleToStringCodec<T> extends ToStringCodec<T> {

    private final SimpleCodecDecode<T> simpleCodecDecode;

    private SimpleToStringCodec(SimpleCodecDecode<T> simpleCodecDecode) {
        this.simpleCodecDecode = simpleCodecDecode;
    }

    public static <T> SimpleToStringCodec<T> create(SimpleCodecDecode<T> simpleCodecDecode) {
        return new SimpleToStringCodec<>(simpleCodecDecode);
    }

    @Override
    public T decode(String s) {
        return s != null ? simpleCodecDecode.decode(s) : null;
    }

    /**
     * 解码类
     *
     * @param <T>
     */
    @FunctionalInterface
    public interface SimpleCodecDecode<T> {

        /**
         * 解码
         *
         * @param str
         * @return
         */
        T decode(String str);
    }
}
