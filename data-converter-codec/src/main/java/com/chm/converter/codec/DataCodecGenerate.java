package com.chm.converter.codec;

import com.chm.converter.core.Converter;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.regex.Pattern;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public class DataCodecGenerate extends UniversalGenerate<Codec> {

    private final Converter<?> converter;

    public DataCodecGenerate() {
        this(null, null, false);
    }

    public DataCodecGenerate(List<UniversalFactory<Codec>> factories, Converter<?> converter, boolean isInitCodecs) {
        super(factories);
        this.converter = converter;
        if (isInitCodecs) {
            initCodec();
        }
    }

    private void initCodec() {
        put(Boolean.class, Codecs.BOOLEAN);
        put(boolean.class, Codecs.BOOLEAN);
        put(Character.class, Codecs.CHARACTER);
        put(char.class, Codecs.CHARACTER);
        put(Byte.class, Codecs.BYTE);
        put(byte.class, Codecs.BYTE);
        put(Short.class, Codecs.SHORT);
        put(short.class, Codecs.SHORT);
        put(Integer.class, Codecs.INTEGER);
        put(int.class, Codecs.INTEGER);
        put(Long.class, Codecs.LONG);
        put(long.class, Codecs.LONG);
        put(Float.class, Codecs.FLOAT);
        put(float.class, Codecs.FLOAT);
        put(Double.class, Codecs.DOUBLE);
        put(double.class, Codecs.DOUBLE);
        put(BigDecimal.class, Codecs.BIG_DECIMAL);
        put(BigInteger.class, Codecs.BIG_INTEGER);
        put(String.class, Codecs.STRING);

        put(Class.class, Codecs.CLASS);

        put(SimpleDateFormat.class, SimpleToStringCodec.create(SimpleDateFormat::new));
        put(Currency.class, SimpleToStringCodec.create(Currency::getInstance));
        put(TimeZone.class, SimpleToStringCodec.create(TimeZone::getTimeZone));
        put(InetAddress.class, Codecs.INET_ADDRESS);
        put(Inet4Address.class, Codecs.INET_ADDRESS);
        put(Inet6Address.class, Codecs.INET_ADDRESS);
        // put(InetSocketAddress.class, MiscCodec.instance);
        put(File.class, SimpleToStringCodec.create(File::new));
        put(Appendable.class, SimpleToStringCodec.create(StringBuffer::new));
        put(StringBuffer.class, SimpleToStringCodec.create(StringBuffer::new));
        put(StringBuilder.class, SimpleToStringCodec.create(StringBuilder::new));
        put(Charset.class, SimpleToStringCodec.create(Charset::forName));
        put(Pattern.class, SimpleToStringCodec.create(Pattern::compile));
        put(Locale.class, SimpleToStringCodec.create(Locale::new));
        put(URI.class, SimpleToStringCodec.create(URI::create));
        put(URL.class, SimpleToStringCodec.create(str -> {
            try {
                return new URL(str);
            } catch (MalformedURLException e) {
                throw new CodecException(e.getMessage());
            }
        }));
        put(UUID.class, SimpleToStringCodec.create(UUID::fromString));

        // atomic
        put(AtomicBoolean.class, Codecs.ATOMIC_BOOLEAN);
        put(AtomicInteger.class, Codecs.ATOMIC_INTEGER);
        put(AtomicLong.class, Codecs.ATOMIC_LONG);
     /*   put(AtomicReference.class, SimpleToStringCodec.create(str ->
                new AtomicReference<>(Long.decode(str))
        ));*/
        put(AtomicIntegerArray.class, Codecs.ATOMIC_INTEGER_ARRAY);
        put(AtomicLongArray.class, Codecs.ATOMIC_LONG_ARRAY);

        /*put(WeakReference.class, SimpleToStringCodec.create(WeakReference::new));
        put(SoftReference.class, SimpleToStringCodec.create(SoftReference::new));*/

        // Java8 Time Codec
        put(Instant.class, new Java8TimeCodec<>(Instant.class, converter));
        put(LocalDate.class, new Java8TimeCodec<>(LocalDate.class, converter));
        put(LocalDateTime.class, new Java8TimeCodec<>(LocalDateTime.class, converter));
        put(LocalTime.class, new Java8TimeCodec<>(LocalTime.class, converter));
        put(OffsetDateTime.class, new Java8TimeCodec<>(OffsetDateTime.class, converter));
        put(OffsetTime.class, new Java8TimeCodec<>(OffsetTime.class, converter));
        put(ZonedDateTime.class, new Java8TimeCodec<>(ZonedDateTime.class, converter));
        put(MonthDay.class, new Java8TimeCodec<>(MonthDay.class, converter));
        put(YearMonth.class, new Java8TimeCodec<>(YearMonth.class, converter));
        put(Year.class, new Java8TimeCodec<>(Year.class, converter));
        put(ZoneOffset.class, new Java8TimeCodec<>(ZoneOffset.class, converter));

        // Default Date Codec
        put(java.sql.Date.class, new DefaultDateCodec<>(java.sql.Date.class, converter));
        put(Timestamp.class, new DefaultDateCodec<>(Timestamp.class, converter));
        put(Date.class, new DefaultDateCodec<>(Date.class, converter));

    }

    public static final class DataCodecGenerateBuilder {

        private List<UniversalFactory<Codec>> factories;

        private Converter<?> converter;

        private boolean isInitCodecs;

        private DataCodecGenerateBuilder() {
        }

        public static DataCodecGenerateBuilder dataCodecGenerate() {
            return new DataCodecGenerateBuilder();
        }

        public DataCodecGenerateBuilder withFactories(List<UniversalFactory<Codec>> factories) {
            this.factories = factories;
            return this;
        }

        public DataCodecGenerateBuilder withConverter(Converter<?> converter) {
            this.converter = converter;
            return this;
        }

        public DataCodecGenerateBuilder withIsInitCodecs(boolean isInitCodecs) {
            this.isInitCodecs = isInitCodecs;
            return this;
        }

        public DataCodecGenerate build() {
            return new DataCodecGenerate(factories, converter, isInitCodecs);
        }
    }
}
