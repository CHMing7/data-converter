package com.chm.converter.core.codecs;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;

import java.io.IOException;

/**
 * 无编解码器
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-01-06
 **/
public class IdentityCodec<T> implements Codec<T, T> {

    private final TypeToken<T> codecType;

    private final WriteData<T> simpleCodecWriteData;

    private final ReadData<T> simpleCodecReadData;

    private IdentityCodec(TypeToken<T> codecType, WriteData<T> writeData, ReadData<T> readData) {
        this.codecType = codecType;
        this.simpleCodecReadData = readData;
        this.simpleCodecWriteData = writeData;
    }

    public static <T> IdentityCodec<T> create(TypeToken<T> codecType, WriteData<T> writeData, ReadData<T> readData) {
        return new IdentityCodec<>(codecType, writeData, readData);
    }


    @Override
    public T encode(T t) {
        return t;
    }

    @Override
    public TypeToken getEncodeType() {
        return codecType;
    }

    @Override
    public void writeData(T t, DataWriter dw) throws IOException {
        if (t == null) {
            dw.writeNull();
            return;
        }
        simpleCodecWriteData.writeData(t, dw);
    }

    @Override
    public T decode(T t) {
        return t;
    }

    @Override
    public TypeToken<T> getDecodeType() {
        return codecType;
    }


    @Override
    public T readData(DataReader dr) throws IOException {
        return simpleCodecReadData.readData(dr);
    }
}
