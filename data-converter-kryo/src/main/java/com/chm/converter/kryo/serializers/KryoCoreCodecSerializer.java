package com.chm.converter.kryo.serializers;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.WithFormat;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.time.format.DateTimeFormatter;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-08-11
 **/
public class KryoCoreCodecSerializer<T> extends Serializer<T> implements CustomizeSerializer, WithFormat {

    private final Codec codec;

    private final Serializer encodeSerializer;

    public KryoCoreCodecSerializer(Codec codec, Serializer encodeSerializer) {
        this.codec = codec;
        this.encodeSerializer = encodeSerializer;
    }

    @Override
    public KryoCoreCodecSerializer<T> withDatePattern(String datePattern) {
        if (codec instanceof WithFormat) {
            Codec withCodec = (Codec) ((WithFormat) codec).withDatePattern(datePattern);
            return new KryoCoreCodecSerializer<>(withCodec, this.encodeSerializer);
        }
        return new KryoCoreCodecSerializer<>(this.codec, this.encodeSerializer);
    }

    @Override
    public KryoCoreCodecSerializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        if (codec instanceof WithFormat) {
            Codec withCodec = (Codec) ((WithFormat) codec).withDateFormatter(dateFormatter);
            return new KryoCoreCodecSerializer<>(withCodec, this.encodeSerializer);
        }
        return new KryoCoreCodecSerializer<>(this.codec, this.encodeSerializer);
    }

    @Override
    public void write(Kryo kryo, Output output, T object) {
        Object encode = this.codec.encode(object);
        kryo.writeObjectOrNull(output, encode, encodeSerializer);
    }

    @Override
    public T read(Kryo kryo, Input input, Class<T> type) {
        Object o = kryo.readObjectOrNull(input, this.codec.getEncodeType().getRawType(), encodeSerializer);
        if (o == null) {
            return null;
        }
        return (T) this.codec.decode(o);
    }

    /**
     * 优先使用此codec
     *
     * @return
     */
    public boolean isPriorityUse() {
        return this.codec.isPriorityUse();
    }
}
