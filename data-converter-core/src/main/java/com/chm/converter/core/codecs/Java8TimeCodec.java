package com.chm.converter.core.codecs;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.DateUtil;
import com.chm.converter.core.utils.StringUtil;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Objects;
import java.util.TimeZone;

/**
 * jdk8时间类编解码器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public class Java8TimeCodec<T extends TemporalAccessor> implements Codec<T, String>, WithFormat {

    public static final Java8TimeCodec<Instant> INSTANT_CODEC = new Java8TimeCodec<>(Instant.class);

    public static final Java8TimeCodec<LocalDate> LOCAL_DATE_CODEC = new Java8TimeCodec<>(LocalDate.class);

    public static final Java8TimeCodec<LocalDateTime> LOCAL_DATE_TIME_CODEC = new Java8TimeCodec<>(LocalDateTime.class);

    public static final Java8TimeCodec<LocalTime> LOCAL_TIME_CODEC = new Java8TimeCodec<>(LocalTime.class);

    public static final Java8TimeCodec<OffsetDateTime> OFFSET_DATE_TIME_CODEC = new Java8TimeCodec<>(OffsetDateTime.class);

    public static final Java8TimeCodec<OffsetTime> OFFSET_TIME_CODEC = new Java8TimeCodec<>(OffsetTime.class);

    public static final Java8TimeCodec<ZonedDateTime> ZONED_DATE_TIME_CODEC = new Java8TimeCodec<>(ZonedDateTime.class);

    public static final Java8TimeCodec<MonthDay> MONTH_DAY_CODEC = new Java8TimeCodec<>(MonthDay.class);

    public static final Java8TimeCodec<YearMonth> YEAR_MONTH_CODEC = new Java8TimeCodec<>(YearMonth.class);

    public static final Java8TimeCodec<Year> YEAR_CODEC = new Java8TimeCodec<>(Year.class);

    public static final Java8TimeCodec<ZoneOffset> ZONE_OFFSET_CODEC = new Java8TimeCodec<>(ZoneOffset.class);

    private final Class<T> clazz;

    private final DateTimeFormatter dateFormatter;

    private final Converter<?> converter;

    private final DateTimeFormatter defaultDateTimeFormatter;

    private final TemporalQuery<T> temporalQuery;

    public Java8TimeCodec(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public Java8TimeCodec(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public Java8TimeCodec(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public Java8TimeCodec(Class<T> clazz, Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter);
    }

    public Java8TimeCodec(Class<T> clazz, String datePattern, Converter<?> converter) {
        Objects.requireNonNull(clazz, "clazz must not be null");
        this.clazz = clazz;
        if (StringUtil.isNotBlank(datePattern)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
            if (clazz == Instant.class && dateFormatter.getZone() == null) {
                TimeZone timeZone = converter != null ? converter.getTimeZone() : TimeZone.getDefault();
                dateFormatter = dateFormatter.withZone(timeZone.toZoneId());
            }
            this.dateFormatter = dateFormatter;
        } else {
            this.dateFormatter = null;
        }
        this.converter = converter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public Java8TimeCodec(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
        Objects.requireNonNull(clazz, "clazz must not be null");
        this.clazz = clazz;
        if (dateFormatter != null && clazz == Instant.class && dateFormatter.getZone() == null) {
            TimeZone timeZone = converter != null ? converter.getTimeZone() : TimeZone.getDefault();
            dateFormatter = dateFormatter.withZone(timeZone.toZoneId());
        }
        this.dateFormatter = dateFormatter;
        this.converter = converter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public Java8TimeCodec<T> withClass(Class<T> clazz) {
        return new Java8TimeCodec<>(clazz, this.dateFormatter, this.converter);
    }

    @Override
    public Java8TimeCodec<T> withDatePattern(String datePattern) {
        return new Java8TimeCodec<>(this.clazz, datePattern, this.converter);
    }

    @Override
    public Java8TimeCodec<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new Java8TimeCodec<>(this.clazz, dateFormatter, this.converter);
    }

    public Java8TimeCodec<T> withConverter(Converter<?> converter) {
        return new Java8TimeCodec<>(this.clazz, this.dateFormatter, converter);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public Converter<?> getConverter() {
        return converter;
    }

    public DateTimeFormatter getDefaultDateTimeFormatter() {
        return defaultDateTimeFormatter;
    }

    public TemporalQuery<T> getTemporalQuery() {
        return temporalQuery;
    }

    /**
     * 将jdk8时间类序列化成字符串
     *
     * @param t
     * @return
     */
    @Override
    public String encode(T t) {
        return encode(t, null);
    }

    @Override
    public TypeToken<String> getEncodeType() {
        return TypeToken.get(String.class);
    }

    @Override
    public void writeData(String s, DataWriter dw) throws IOException {
        if (s == null) {
            dw.writeNull();
            return;
        }
        dw.writeString(s);
    }

    /**
     * 将jdk8时间类序列化成字符串
     *
     * @param t
     * @param format
     * @return
     */
    public String encode(T t, String format) {
        if (t == null) {
            return null;
        }

        DateTimeFormatter dtf = getCodecDateFormatter(format);
        return DateUtil.format(t, dtf);
    }

    @Override
    public T decode(String timeStr) {
        return decode(timeStr, null);
    }

    @Override
    public TypeToken<T> getDecodeType() {
        return TypeToken.get(clazz);
    }

    @Override
    public String readData(DataReader dr) throws IOException {
        return dr.readString();
    }

    /**
     * 将字符串反序列化成jdk8时间类
     *
     * @param timeStr
     * @param format
     * @return
     */
    public T decode(String timeStr, String format) {
        if (StringUtil.isBlank(timeStr)) {
            return null;
        }

        DateTimeFormatter dtf = getCodecDateFormatter(format);
        try {
            return dtf.parse(timeStr, temporalQuery);
        } catch (Throwable cause) {
            return DateUtil.parse(timeStr, temporalQuery);
        }
    }

    private DateTimeFormatter getCodecDateFormatter(String otherFormat) {
        DateTimeFormatter dtf = null;
        if (StringUtil.isNotBlank(otherFormat)) {
            dtf = DateTimeFormatter.ofPattern(otherFormat);
        }
        if (dtf == null) {
            dtf = this.dateFormatter;
        }
        if (dtf == null && this.converter != null) {
            dtf = this.converter.getDateFormat();
        }
        if (dtf == null) {
            dtf = defaultDateTimeFormatter;
        }

        if (clazz == Instant.class && dtf != null && dtf.getZone() == null) {
            // Instant类需设置时区
            TimeZone timeZone = this.converter != null ? this.converter.getTimeZone() : TimeZone.getDefault();
            dtf = dtf.withZone(timeZone.toZoneId());
        }
        return dtf;
    }

    @Override
    public boolean isPriorityUse() {
        return true;
    }
}
