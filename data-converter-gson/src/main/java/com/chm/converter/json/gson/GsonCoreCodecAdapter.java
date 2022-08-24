package com.chm.converter.json.gson;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.WithFormat;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-21
 **/
public class GsonCoreCodecAdapter<T> extends TypeAdapter<T> implements WithFormat {

    private final Codec codec;

    private final TypeAdapter encodeAdapter;

    public GsonCoreCodecAdapter(Codec codec, TypeAdapter encodeAdapter) {
        this.codec = codec;
        this.encodeAdapter = encodeAdapter;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        this.encodeAdapter.write(out, this.codec.encode(value));
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return (T) this.codec.read(() -> this.encodeAdapter.read(in));
    }

    @Override
    public GsonCoreCodecAdapter<T> withDatePattern(String datePattern) {
        if (this.codec instanceof WithFormat) {
            Codec withCodec = (Codec) ((WithFormat) this.codec).withDatePattern(datePattern);
            return new GsonCoreCodecAdapter<>(withCodec, this.encodeAdapter);
        }
        return new GsonCoreCodecAdapter<>(this.codec, this.encodeAdapter);
    }

    @Override
    public GsonCoreCodecAdapter<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        if (this.codec instanceof WithFormat) {
            Codec withCodec = (Codec) ((WithFormat) this.codec).withDateFormatter(dateFormatter);
            return new GsonCoreCodecAdapter<>(withCodec, this.encodeAdapter);
        }
        return new GsonCoreCodecAdapter<>(this.codec, this.encodeAdapter);
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
