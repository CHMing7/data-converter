package com.chm.converter.kryo.serializers;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.EnumCodec;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-26
 **/
public class KryoEnumSerializer<E extends Enum<E>> extends Serializer<E> implements CustomizeSerializer {

    private final EnumCodec<E> enumCodec;

    public KryoEnumSerializer(Class<E> classOfT, Converter<?> converter) {
        this.enumCodec = new EnumCodec<>(classOfT, converter);
    }

    @Override
    public void write(Kryo kryo, Output output, E object) {
        String encode = this.enumCodec.encode(object);
        output.writeString(encode);
    }

    @Override
    public E read(Kryo kryo, Input input, Class<E> type) {
        String s = input.readString();
        return this.enumCodec.decode(s);
    }
}
