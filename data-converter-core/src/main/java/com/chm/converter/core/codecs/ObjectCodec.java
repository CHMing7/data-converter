package com.chm.converter.core.codecs;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalGenerate;

import java.io.IOException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-02-18
 **/
public class ObjectCodec implements Codec<Object, Object> {

    private final UniversalGenerate<Codec> generate;

    public ObjectCodec(UniversalGenerate<Codec> generate) {
        this.generate = generate;
    }

    @Override
    public Object encode(Object o) {
        return o;
    }

    @Override
    public TypeToken<Object> getEncodeType() {
        return TypeToken.get(Object.class);
    }

    @Override
    public void writeData(Object o, DataWriter dw) throws IOException {
        if (o == null) {
            dw.writeNull();
            return;
        }

        Codec codec = generate.get(o.getClass());
        if (codec instanceof ObjectCodec) {
            dw.writeBeanBegin(o);
            dw.writeBeanEnd(o);
            return;
        }
        dw.writeClass(o.getClass());
        codec.writeData(o, dw);
    }

    @Override
    public Object decode(Object o) {
        return o;
    }

    @Override
    public TypeToken<Object> getDecodeType() {
        return TypeToken.get(Object.class);
    }

    @Override
    public Object readData(DataReader dr) throws IOException {
        Class<Object> cls = dr.readClass();
        Codec codec = generate.get(cls);
        return codec.readData(dr);
    }
}
