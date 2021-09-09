package com.chm.converter.codec;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public class SimpleCodec<D, E> implements Codec<D, E> {

    private final SimpleCodecDecode<D, E> simpleCodecDecode;

    private final SimpleCodecEncode<D, E> simpleCodecEncode;

    private SimpleCodec(SimpleCodecDecode<D, E> decode, SimpleCodecEncode<D, E> encode) {
        this.simpleCodecDecode = decode;
        this.simpleCodecEncode = encode;
    }

    public static <D, E> SimpleCodec<D, E> create(SimpleCodecDecode<D, E> decode, SimpleCodecEncode<D, E> encode) {
        return new SimpleCodec<>(decode, encode);
    }

    @Override
    public E encode(D d) {
        return d != null ? simpleCodecEncode.encode(d) : null;
    }

    @Override
    public D decode(E e) {
        return e != null ? simpleCodecDecode.decode(e) : null;
    }

    /**
     * 编码类
     * @param <D>
     * @param <E>
     */
    @FunctionalInterface
    public interface SimpleCodecEncode<D, E> {

        /**
         * 编码
         *
         * @param d
         * @return
         */
        E encode(D d);
    }

    /**
     * 解码类
     * @param <D>
     * @param <E>
     */
    @FunctionalInterface
    public interface SimpleCodecDecode<D, E> {

        /**
         * 解码
         *
         * @param e
         * @return
         */
        D decode(E e);
    }
}
