package com.chm.converter.core.codecs;

import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;

import java.io.IOException;

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

    private final boolean isPriorityUse;

    private SimpleToStringCodec(TypeToken<T> decodeType,
                                WriteData<String> writeData,
                                Decode<T, String> decode,
                                ReadData<String> readData,
                                boolean isPriorityUse) {
        this.decodeType = decodeType;
        this.simpleCodecWriteData = writeData;
        this.simpleCodecDecode = decode;
        this.simpleCodecReadData = readData;
        this.isPriorityUse = isPriorityUse;
    }

    public static <T> SimpleToStringCodec<T> create(TypeToken<T> decodeType,
                                                    Decode<T, String> decode) {
        return new SimpleToStringCodec<>(decodeType, (s, dw) -> dw.writeString(s), decode, DataReader::readString, false);
    }

    public static <T> SimpleToStringCodec<T> create(TypeToken<T> decodeType,
                                                    Decode<T, String> decode,
                                                    boolean isPriorityUse) {
        return new SimpleToStringCodec<>(decodeType, (s, dw) -> dw.writeString(s), decode, DataReader::readString, isPriorityUse);
    }

    public static <T> SimpleToStringCodec<T> create(TypeToken<T> decodeType,
                                                    Decode<T, String> decode,
                                                    ReadData<String> readData) {
        return new SimpleToStringCodec<>(decodeType, (s, dw) -> dw.writeString(s), decode, readData, false);
    }

    public static <T> SimpleToStringCodec<T> create(TypeToken<T> decodeType,
                                                    Decode<T, String> decode,
                                                    ReadData<String> readData,
                                                    boolean isPriorityUse) {
        return new SimpleToStringCodec<>(decodeType, (s, dw) -> dw.writeString(s), decode, readData, isPriorityUse);
    }

    public static <T> SimpleToStringCodec<T> create(TypeToken<T> decodeType,
                                                    WriteData<String> writeData,
                                                    Decode<T, String> decode) {
        return new SimpleToStringCodec<>(decodeType, writeData, decode, DataReader::readString, false);
    }

    public static <T> SimpleToStringCodec<T> create(TypeToken<T> decodeType,
                                                    WriteData<String> writeData,
                                                    Decode<T, String> decode,
                                                    boolean isPriorityUse) {
        return new SimpleToStringCodec<>(decodeType, writeData, decode, DataReader::readString, isPriorityUse);
    }

    public static <T> SimpleToStringCodec<T> create(TypeToken<T> decodeType,
                                                    WriteData<String> writeData,
                                                    Decode<T, String> decode,
                                                    ReadData<String> readData) {
        return new SimpleToStringCodec<>(decodeType, writeData, decode, readData, false);
    }

    public static <T> SimpleToStringCodec<T> create(TypeToken<T> decodeType,
                                                    WriteData<String> writeData,
                                                    Decode<T, String> decode,
                                                    ReadData<String> readData,
                                                    boolean isPriorityUse) {
        return new SimpleToStringCodec<>(decodeType, writeData, decode, readData, isPriorityUse);
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

    @Override
    public boolean isPriorityUse() {
        return isPriorityUse;
    }
}
