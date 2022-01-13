package com.chm.converter.kryo.serializers;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-10-22
 **/
public class KryoDefaultDateSerializer<T extends Date> extends Serializer<T> {

    private final DefaultDateCodec<T> defaultDateCodec;

    public KryoDefaultDateSerializer(Class<T> clazz, String datePattern, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(clazz, datePattern, converter);
    }

    public KryoDefaultDateSerializer(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(clazz, dateFormatter, converter);
    }

    public KryoDefaultDateSerializer<T> withDateType(Class<T> dateType) {
        return new KryoDefaultDateSerializer<>(dateType, this.defaultDateCodec.getDateFormatter(), this.defaultDateCodec.getConverter());
    }

    public KryoDefaultDateSerializer<T> withDatePattern(String datePattern) {
        return new KryoDefaultDateSerializer<>(this.defaultDateCodec.getDateType(), datePattern, this.defaultDateCodec.getConverter());
    }

    public KryoDefaultDateSerializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new KryoDefaultDateSerializer<>(this.defaultDateCodec.getDateType(), dateFormatter, this.defaultDateCodec.getConverter());
    }

    @Override
    public void write(Kryo kryo, Output output, T object) {
        String encode = defaultDateCodec.encode(object);
        output.writeString(encode);
    }

    @Override
    public T read(Kryo kryo, Input input, Class<T> type) {
        String s = input.readString();
        return defaultDateCodec.decode(s);
    }
}
