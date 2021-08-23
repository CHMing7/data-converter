package com.chm.converter.json.jackson.serializer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-14
 **/
public class JacksonDefaultDateTypeSerializer<T extends Date> extends JsonSerializer<T> {

    private DateFormat dateFormat;

    private static final String DEFAULT_DATE_PATTERN_STR = "yyyy-MM-dd HH:mm:ss";

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them is used for serialization as well.
     */
    private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_PATTERN_STR);

    public JacksonDefaultDateTypeSerializer() {
    }

    public JacksonDefaultDateTypeSerializer(String datePattern) {
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormat = new SimpleDateFormat(datePattern);
        }
    }

    public JacksonDefaultDateTypeSerializer(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public JacksonDefaultDateTypeSerializer<T> withDatePattern(String datePattern) {
        return new JacksonDefaultDateTypeSerializer<>(datePattern);
    }

    public JacksonDefaultDateTypeSerializer<T> withDateFormat(DateFormat dateFormat) {
        return new JacksonDefaultDateTypeSerializer<>(dateFormat);
    }

    public void setDateFormat(String datePattern) {
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormat = new SimpleDateFormat(datePattern);
        }
    }

    public void setDateFormatter(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    protected SerializationFeature getTimestampsFeature() {
        return SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
    }

    protected boolean useTimestamp(SerializerProvider provider) {
        // assume that explicit formatter definition implies use of textual format
        return (dateFormat == null) && (provider != null)
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
            DateFormat dateFormat = this.dateFormat;
            if (dateFormat == null) {
                dateFormat = serializers.getConfig().getDateFormat();
            }
            if (dateFormat != null) {
                dateFormatAsString = DateUtil.format(value, dateFormat);
            } else {
                dateFormatAsString = DateUtil.format(value, JacksonDefaultDateTypeSerializer.DEFAULT_DATE_FORMAT);
            }
            gen.writeString(dateFormatAsString);
        }
    }
}
