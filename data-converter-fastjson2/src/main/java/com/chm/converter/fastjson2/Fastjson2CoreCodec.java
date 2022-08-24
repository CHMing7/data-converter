package com.chm.converter.fastjson2;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.WithFormat;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-21
 **/
public class Fastjson2CoreCodec<T> implements ObjectWriter<T>, ObjectReader<T>, WithFormat {

    private final Codec codec;

    public Fastjson2CoreCodec(Codec codec) {
        this.codec = codec;
    }

    @Override
    public long getFeatures() {
        return ObjectWriter.super.getFeatures();
    }

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }

        Object encode = codec.encode(object);
        if (encode != null && encode.getClass() != object.getClass()) {
            jsonWriter.writeAny(encode);
        }
    }

    @Override
    public T readJSONBObject(JSONReader jsonReader, long features) {
        Object o = jsonReader.readAny();
        if (o == null) {
            return null;
        }
        return (T) this.codec.decode(o);
    }

    @Override
    public T readObject(JSONReader jsonReader, long features) {
        Object o = jsonReader.readAny();
        if (o == null) {
            return null;
        }
        return (T) this.codec.decode(o);
    }

    @Override
    public Fastjson2CoreCodec<T> withDatePattern(String datePattern) {
        if (codec instanceof WithFormat) {
            Codec withCodec = (Codec) ((WithFormat) codec).withDatePattern(datePattern);
            return new Fastjson2CoreCodec<>(withCodec);
        }
        return new Fastjson2CoreCodec<>(this.codec);
    }

    @Override
    public Fastjson2CoreCodec<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        if (codec instanceof WithFormat) {
            Codec withCodec = (Codec) ((WithFormat) codec).withDateFormatter(dateFormatter);
            return new Fastjson2CoreCodec<>(withCodec);
        }
        return new Fastjson2CoreCodec<>(this.codec);
    }
}
