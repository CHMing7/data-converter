package com.chm.converter.core.codecs;

import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * toString简易编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-03
 **/
public class SimpleToStringCodec<T> extends ToStringCodec<T> {

    private final TypeToken<T> decodeType;

    private final WriteData<String> simpleCodecWriteData;

    private final Decode<T, String> simpleCodecDecode;

    private final ReadData<String> simpleCodecReadData;

    private SimpleToStringCodec(TypeToken<T> decodeType, WriteData<String> writeData, Decode<T, String> decode, ReadData<String> readData) {
        this.decodeType = decodeType;
        this.simpleCodecDecode = decode;
        this.simpleCodecReadData = readData;
        this.simpleCodecWriteData = writeData;
    }

    public static <T> SimpleToStringCodec<T> create(TypeToken<T> decodeType, WriteData<String> writeData, Decode<T, String> decode, ReadData<String> readData) {
        return new SimpleToStringCodec<>(decodeType, writeData, decode, readData);
    }

    @Override
    public TypeToken<String> getEncodeType() {
        return TypeToken.get(String.class);
    }

    @Override
    public void writeData(String s, DataWriter dw) throws IOException {
        if (s == null) {
            dw.writeNull();
            return;
        }
        simpleCodecWriteData.writeData(s, dw);
    }

    @Override
    public T decode(String s) {
        return s != null ? simpleCodecDecode.decode(s) : null;
    }

    @Override
    public TypeToken<T> getDecodeType() {
        return decodeType;
    }

    @Override
    public String readData(DataReader dr) throws IOException {
        return simpleCodecReadData.readData(dr);
    }
}
