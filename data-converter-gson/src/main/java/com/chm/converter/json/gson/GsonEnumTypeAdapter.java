package com.chm.converter.json.gson;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.EnumCodec;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-26
 **/
public class GsonEnumTypeAdapter<E extends Enum<E>> extends TypeAdapter<E> {

    private final EnumCodec<E> enumCodec;

    public GsonEnumTypeAdapter(Class<E> classOfT, Converter<?> converter) {
        this.enumCodec = new EnumCodec<>(classOfT, converter);
    }

    @Override
    public void write(JsonWriter out, E value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        this.enumCodec.write(value, out::value);
    }

    @Override
    public E read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return this.enumCodec.read(in::nextString);
    }
}
