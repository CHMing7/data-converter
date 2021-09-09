package com.chm.converter.codec;

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
    public String decode(String s) {
        return s;
    }
}
