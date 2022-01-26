package com.chm.converter.kryo.serializers;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-10-22
 **/
public class KryoJava8TimeSerializer<T extends TemporalAccessor> extends Serializer<T> {

    private final Java8TimeCodec<T> java8TimeCodec;

    public KryoJava8TimeSerializer(Class<T> clazz, String datePattern, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, datePattern, converter);
    }

    public KryoJava8TimeSerializer(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, dateFormatter, converter);
    }

    public KryoJava8TimeSerializer<T> withClass(Class<T> clazz) {
        return new KryoJava8TimeSerializer<>(clazz, this.java8TimeCodec.getDateFormatter(), this.java8TimeCodec.getConverter());
    }

    public KryoJava8TimeSerializer<T> withDatePattern(String datePattern) {
        return new KryoJava8TimeSerializer<>(this.java8TimeCodec.getClazz(), datePattern, this.java8TimeCodec.getConverter());
    }

    public KryoJava8TimeSerializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new KryoJava8TimeSerializer<>(this.java8TimeCodec.getClazz(), dateFormatter, this.java8TimeCodec.getConverter());
    }

    @Override
    public void write(Kryo kryo, Output output, T object) {
        String encode = this.java8TimeCodec.encode(object);
        output.writeString(encode);
    }

    @Override
    public T read(Kryo kryo, Input input, Class<T> type) {
        String s = input.readString();
        return this.java8TimeCodec.decode(s);
    }
}
