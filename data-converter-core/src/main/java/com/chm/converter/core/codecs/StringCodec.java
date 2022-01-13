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
public class StringCodec implements Codec<String, String> {

    public final static StringCodec INSTANCE = new StringCodec();

    @Override
    public String encode(String s) {
        return s;
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
        dw.writeString(s);
    }

    @Override
    public String decode(String s) {
        return s;
    }

    @Override
    public TypeToken<String> getDecodeType() {
        return TypeToken.get(String.class);
    }

    @Override
    public String readData(DataReader dr) throws IOException {
        return dr.readString();
    }
}
