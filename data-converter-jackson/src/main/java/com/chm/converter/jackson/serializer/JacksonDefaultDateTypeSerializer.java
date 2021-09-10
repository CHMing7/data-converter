package com.chm.converter.jackson.serializer;

import cn.hutool.core.util.StrUtil;
import com.chm.converter.core.Converter;
import com.chm.converter.core.utils.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-14
 **/
public class JacksonDefaultDateTypeSerializer<T extends Date> extends JsonSerializer<T> {

    private final DateTimeFormatter dateFormatter;

    private final Converter<?> converter;

    private static final String DEFAULT_DATE_PATTERN_STR = "yyyy-MM-dd HH:mm:ss.SSSS";

    private static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN_STR);

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
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        } else {
            this.dateFormatter = null;
        }
        this.converter = converter;
    }

    public JacksonDefaultDateTypeSerializer(DateTimeFormatter dateFormat, Converter<?> converter) {
        this.dateFormatter = dateFormat;
        this.converter = converter;
    }


    public JacksonDefaultDateTypeSerializer<T> withDatePattern(String datePattern) {
        return new JacksonDefaultDateTypeSerializer<>(datePattern, this.converter);
    }

    public JacksonDefaultDateTypeSerializer<T> withDateFormat(DateTimeFormatter dateFormatter) {
        return new JacksonDefaultDateTypeSerializer<>(dateFormatter, this.converter);
    }

    protected SerializationFeature getTimestampsFeature() {
        return SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
    }

    protected boolean useTimestamp(SerializerProvider provider) {
        // assume that explicit formatter definition implies use of textual format
        return (dateFormatter == null) && (provider != null)
                && provider.isEnabled(getTimestampsFeature());
    }

    protected long timestamp(Date value) {
        return (value == null) ? 0L : value.getTime();
    }


    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (useTimestamp(serializers)) {
            gen.writeNumber(timestamp(value));
        } else {

            String dateFormatAsString;
            DateTimeFormatter dtf = this.dateFormatter;
            if (converter != null && dtf == null) {
                dtf = converter.getDateFormat();
            }

            if (dtf != null) {
                dateFormatAsString = DateUtil.format(value, dtf);
            } else {
                dateFormatAsString = DateUtil.format(value, DEFAULT_DATE_FORMAT);
            }
            gen.writeString(dateFormatAsString);
        }
    }

}
