package com.chm.converter.jackson.serializer;

import cn.hutool.core.util.StrUtil;
import com.chm.converter.jackson.JacksonModule;
import com.chm.converter.json.JsonConverter;
import com.chm.converter.utils.DateUtil;
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

    private final JsonConverter jsonConverter;

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

    public JacksonDefaultDateTypeSerializer(JsonConverter jsonConverter) {
        this((DateTimeFormatter) null, jsonConverter);
    }

    public JacksonDefaultDateTypeSerializer(String datePattern, JsonConverter jsonConverter) {
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        } else {
            this.dateFormatter = null;
        }
        this.jsonConverter = jsonConverter;
    }

    public JacksonDefaultDateTypeSerializer(DateTimeFormatter dateFormat, JsonConverter jsonConverter) {
        this.dateFormatter = dateFormat;
        this.jsonConverter = jsonConverter;
    }


    public JacksonDefaultDateTypeSerializer<T> withDatePattern(String datePattern) {
        return new JacksonDefaultDateTypeSerializer<>(datePattern, this.jsonConverter);
    }

    public JacksonDefaultDateTypeSerializer<T> withDateFormat(DateTimeFormatter dateFormatter) {
        return new JacksonDefaultDateTypeSerializer<>(dateFormatter, this.jsonConverter);
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
            DateTimeFormatter dateFormatter = JacksonModule.getDateFormatter(this.dateFormatter, jsonConverter, serializers);

            if (dateFormatter != null) {
                dateFormatAsString = DateUtil.format(value, dateFormatter);
            } else {
                dateFormatAsString = DateUtil.format(value, JacksonDefaultDateTypeSerializer.DEFAULT_DATE_FORMAT);
            }
            gen.writeString(dateFormatAsString);
        }
    }

}
