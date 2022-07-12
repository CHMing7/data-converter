package com.chm.converter.fastjson2;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.chm.converter.core.utils.StringUtil;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-21
 **/
public class Fastjson2DefaultDateCodec<T extends Date> implements ObjectWriter<T>, ObjectReader<T>, WithFormat {

    private final DefaultDateCodec<T> defaultDateCodec;

    public Fastjson2DefaultDateCodec(Class<T> dateType) {
        this(dateType, (DateTimeFormatter) null, null);
    }

    public Fastjson2DefaultDateCodec(Class<T> dateType, String datePattern) {
        this(dateType, datePattern, null);
    }

    public Fastjson2DefaultDateCodec(Class<T> dateType, DateTimeFormatter dateFormatter) {
        this(dateType, dateFormatter, null);
    }

    public Fastjson2DefaultDateCodec(Class<T> dateType, Converter<?> converter) {
        this(dateType, (DateTimeFormatter) null, converter);
    }

    public Fastjson2DefaultDateCodec(Class<T> dateType, String datePattern, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, datePattern, converter);
    }

    public Fastjson2DefaultDateCodec(Class<T> dateType, DateTimeFormatter dateFormat, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, dateFormat, converter);
    }

    public Fastjson2DefaultDateCodec<T> withDateType(Class<T> dateType) {
        return new Fastjson2DefaultDateCodec<>(dateType, this.defaultDateCodec.getDateFormatter(), this.defaultDateCodec.getConverter());
    }

    @Override
    public Fastjson2DefaultDateCodec<T> withDatePattern(String datePattern) {
        return new Fastjson2DefaultDateCodec<>(this.defaultDateCodec.getDateType(), datePattern, this.defaultDateCodec.getConverter());
    }

    @Override
    public Fastjson2DefaultDateCodec<T> withDateFormatter(DateTimeFormatter dateFormat) {
        return new Fastjson2DefaultDateCodec<>(this.defaultDateCodec.getDateType(), dateFormat, this.defaultDateCodec.getConverter());
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
        jsonWriter.writeString(this.defaultDateCodec.encode((T) object));
    }

    @Override
    public T readJSONBObject(JSONReader jsonReader, long features) {
        String str = jsonReader.readString();
        if (StringUtil.isBlank(str)) {
            return null;
        }

        return this.defaultDateCodec.decode(str);
    }

    @Override
    public T readObject(JSONReader jsonReader, long features) {
        String str = jsonReader.readString();
        if (StringUtil.isBlank(str)) {
            return null;
        }

        return this.defaultDateCodec.decode(str);
    }
}
