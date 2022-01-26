package com.chm.converter.core.codecs;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;

import java.io.IOException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public class SimpleCodec<D, E> implements Codec<D, E> {

    private final TypeToken<E> encodeType;

    private final TypeToken<D> decodeType;

    private final Encode<D, E> simpleCodecEncode;

    private final Decode<D, E> simpleCodecDecode;

    private final WriteData<E> simpleCodecWriteData;

    private final ReadData<E> simpleCodecReadData;


    private SimpleCodec(TypeToken<E> encodeType, TypeToken<D> decodeType, Encode<D, E> encode, Decode<D, E> decode, WriteData<E> writeData, ReadData<E> readData) {
        this.encodeType = encodeType;
        this.decodeType = decodeType;
        this.simpleCodecDecode = decode;
        this.simpleCodecEncode = encode;
        this.simpleCodecReadData = readData;
        this.simpleCodecWriteData = writeData;
    }

    public static <D, E> SimpleCodec<D, E> create(TypeToken<E> encodeType, TypeToken<D> decodeType, Encode<D, E> encode, Decode<D, E> decode, WriteData<E> writeData, ReadData<E> readData) {
        return new SimpleCodec<>(encodeType, decodeType, encode, decode, writeData, readData);
    }

    @Override
    public E encode(D d) {
        return d != null ? simpleCodecEncode.encode(d) : null;
    }

    @Override
    public TypeToken<E> getEncodeType() {
        return encodeType;
    }

    @Override
    public void writeData(E e, DataWriter dw) throws IOException {
        if (e == null) {
            dw.writeNull();
            return;
        }
        simpleCodecWriteData.writeData(e, dw);
    }

    @Override
    public D decode(E e) {
        return e != null ? simpleCodecDecode.decode(e) : null;
    }

    @Override
    public TypeToken<D> getDecodeType() {
        return decodeType;
    }

    @Override
    public E readData(DataReader dr) throws IOException {
        return simpleCodecReadData.readData(dr);
    }
}
