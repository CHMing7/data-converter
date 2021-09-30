package com.chm.converter.codec;

import cn.hutool.core.collection.ListUtil;
import com.chm.converter.core.Converter;

import java.io.File;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.*;
import java.util.regex.Pattern;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public class DataCodec {

    public final static DataCodec INSTANCE = new DataCodec();

    private final Map<Type, Codec<?, ?>> typeCache = new ConcurrentHashMap<>();

    private final List<CodecFactory> factories;

    private final Converter<?> converter;

    public DataCodec() {
        this(null, null, false);
    }

    public DataCodec(List<CodecFactory> factories, Converter<?> converter, boolean isInitCodecs) {
        this.factories = Collections.unmodifiableList(factories != null ? factories : ListUtil.empty());
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

    /**
     * 获取编解码器
     *
     * @param type
     * @return
     */
    public Codec<?, ?> getCodec(Type type) {
        if (type == null) {
            return null;
        }
        Codec<?, ?> cached = typeCache.get(type);
        if (cached != null) {
            return cached;
        }
        for (CodecFactory factory : factories) {
            Codec<?, ?> candidate = factory.createCodec(type);
            if (candidate != null) {
                put(type, candidate);
                return candidate;
            }
        }
        return null;
    }

    /**
     * 获取编解码器
     *
     * @param skipPast 跳过工厂类
     * @param type
     * @return
     */
    public Codec<?, ?> getDelegateCodec(CodecFactory skipPast, Type type) {
        boolean skipPastFound = false;
        for (CodecFactory factory : factories) {
            if (!skipPastFound) {
                if (factory == skipPast) {
                    skipPastFound = true;
                }
                continue;
            }

            Codec<?, ?> candidate = factory.createCodec(type);
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * 获取编解码器
     *
     * @param type
     * @param <T>
     * @return
     */
    public <T> Codec<?, ?> getCodec(Class<T> type) {
        return getCodec((Type) type);
    }

    /**
     * 新增编解码器
     *
     * @param type
     * @param codec
     * @return
     */
    public boolean put(Type type, Codec<?, ?> codec) {
        return this.typeCache.put(type, codec) != null;
    }

    public boolean containsByType(Type type) {
        if (this.typeCache.containsKey(type)) {
            return true;
        }
        return getCodec(type) != null;
    }

    public static final class DataCodecBuilder {
        private List<CodecFactory> factories;
        private Converter<?> converter;
        private boolean isInitCodecs;

        private DataCodecBuilder() {
        }

        public static DataCodecBuilder aDataCodec() {
            return new DataCodecBuilder();
        }

        public DataCodecBuilder withFactories(List<CodecFactory> factories) {
            this.factories = factories;
            return this;
        }

        public DataCodecBuilder withConverter(Converter<?> converter) {
            this.converter = converter;
            return this;
        }

        public DataCodecBuilder withIsInitCodecs(boolean isInitCodecs) {
            this.isInitCodecs = isInitCodecs;
            return this;
        }

        public DataCodec build() {
            return new DataCodec(factories, converter, isInitCodecs);
        }
    }
}
