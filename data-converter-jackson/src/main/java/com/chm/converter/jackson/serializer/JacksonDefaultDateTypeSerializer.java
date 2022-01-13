package com.chm.converter.jackson.serializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-14
 **/
public class JacksonDefaultDateTypeSerializer<T extends Date> extends JsonSerializer<T> {

    private final DefaultDateCodec<T> defaultDateCodec;

    public JacksonDefaultDateTypeSerializer() {
        this((DateTimeFormatter) null, null);
    }

    public JacksonDefaultDateTypeSerializer(String datePattern) {
        this(datePattern, null);
    }

    public JacksonDefaultDateTypeSerializer(DateTimeFormatter dateFormat) {
        this(dateFormat, null);
    }

    public JacksonDefaultDateTypeSerializer(Converter<?> converter) {
        this((DateTimeFormatter) null, converter);
    }

    public JacksonDefaultDateTypeSerializer(String datePattern, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec(Date.class, datePattern, converter);
    }

    public JacksonDefaultDateTypeSerializer(DateTimeFormatter dateFormat, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec(Date.class, dateFormat, converter);
    }

    public JacksonDefaultDateTypeSerializer<T> withDatePattern(String datePattern) {
        return new JacksonDefaultDateTypeSerializer<>(datePattern, this.defaultDateCodec.getConverter());
    }

    public JacksonDefaultDateTypeSerializer<T> withDateFormat(DateTimeFormatter dateFormatter) {
        return new JacksonDefaultDateTypeSerializer<>(dateFormatter, this.defaultDateCodec.getConverter());
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
        visitor.expectStringFormat(type);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, T value) {
        return value == null;
    }

    protected SerializationFeature getTimestampsFeature() {
        return SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
    }

    protected boolean useTimestamp(SerializerProvider provider) {
        // assume that explicit formatter definition implies use of textual format
        return (this.defaultDateCodec.getDateFormatter() == null) && (provider != null)
                && provider.isEnabled(getTimestampsFeature());
    }

    protected String timestamp(Date value) {
        return (value == null) ? "" : String.valueOf(value.getTime());
    }


    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (useTimestamp(serializers)) {
            gen.writeString(timestamp(value));
        } else {
            String dateFormatAsString = this.defaultDateCodec.encode(value);
            gen.writeString(dateFormatAsString);
        }
    }

}
