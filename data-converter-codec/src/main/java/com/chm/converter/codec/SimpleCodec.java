package com.chm.converter.codec;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public class SimpleCodec<D, E> implements Codec<D, E> {

    private final Decode<D, E> simpleCodecDecode;

    private final Encode<D, E> simpleCodecEncode;

    private SimpleCodec(Decode<D, E> decode, Encode<D, E> encode) {
        this.simpleCodecDecode = decode;
        this.simpleCodecEncode = encode;
    }

    public static <D, E> SimpleCodec<D, E> create(Decode<D, E> decode, Encode<D, E> encode) {
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
}
