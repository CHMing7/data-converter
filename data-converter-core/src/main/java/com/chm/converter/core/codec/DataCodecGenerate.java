package com.chm.converter.core.codec;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.Codecs;
import com.chm.converter.core.codecs.factory.ArrayCodecFactory;
import com.chm.converter.core.codecs.factory.CollectionCodecFactory;
import com.chm.converter.core.codecs.factory.DefaultDateCodecFactory;
import com.chm.converter.core.codecs.factory.EnumCodecFactory;
import com.chm.converter.core.codecs.factory.Java8TimeCodecFactory;
import com.chm.converter.core.codecs.factory.JavaBeanCodecFactory;
import com.chm.converter.core.codecs.factory.MapCodecFactory;
import com.chm.converter.core.codecs.factory.ObjectCodecFactory;
import com.chm.converter.core.codecs.factory.OptionalCodecFactory;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.core.utils.MapUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Period;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    public static final Map<Converter<?>, DataCodecGenerate> CONVERTER_DATA_CODEC_GENERATE_MAP = MapUtil.newConcurrentHashMap();

    private final Converter<?> converter;

    private DataCodecGenerate(List<UniversalFactory<Codec>> factories, Converter<?> converter) {
        super(factories);
        this.converter = converter;
    }

    public Converter<?> getConverter() {
        return this.converter;
    }

    public static DataCodecGenerate newDefault(Converter<?> converter) {
        List<UniversalFactory<Codec>> factories = ListUtil.list(true);
        factories.add(new ObjectCodecFactory());
        factories.add(new OptionalCodecFactory());
        factories.add(new Java8TimeCodecFactory(converter));
        factories.add(new DefaultDateCodecFactory(converter));
        factories.add(new ArrayCodecFactory());
        factories.add(new CollectionCodecFactory());
        factories.add(new MapCodecFactory());
        factories.add(new EnumCodecFactory(converter));
        factories.add(new JavaBeanCodecFactory(converter));
        DataCodecGenerate codecGenerate = new DataCodecGenerate(factories, converter);
        // init codecs
        initCodecs(codecGenerate);

        return codecGenerate;
    }

    public static void newDefault(List<UniversalFactory<Codec>> factories, Converter<?> converter) {
        DataCodecGenerate codecGenerate = new DataCodecGenerate(factories, converter);
        initCodecs(codecGenerate);
        CONVERTER_DATA_CODEC_GENERATE_MAP.put(converter, codecGenerate);
    }

    private static void initCodecs(DataCodecGenerate codecGenerate) {
        codecGenerate.put(Boolean.class, Codecs.BOOLEAN);
        codecGenerate.put(boolean.class, Codecs.BOOLEAN);
        codecGenerate.put(Character.class, Codecs.CHARACTER);
        codecGenerate.put(char.class, Codecs.CHARACTER);
        codecGenerate.put(Byte.class, Codecs.BYTE);
        codecGenerate.put(byte.class, Codecs.BYTE);
        codecGenerate.put(Short.class, Codecs.SHORT);
        codecGenerate.put(short.class, Codecs.SHORT);
        codecGenerate.put(Integer.class, Codecs.INTEGER);
        codecGenerate.put(int.class, Codecs.INTEGER);
        codecGenerate.put(Long.class, Codecs.LONG);
        codecGenerate.put(long.class, Codecs.LONG);
        codecGenerate.put(Float.class, Codecs.FLOAT);
        codecGenerate.put(float.class, Codecs.FLOAT);
        codecGenerate.put(Double.class, Codecs.DOUBLE);
        codecGenerate.put(double.class, Codecs.DOUBLE);
        codecGenerate.put(BigDecimal.class, Codecs.BIG_DECIMAL);
        codecGenerate.put(BigInteger.class, Codecs.BIG_INTEGER);
        codecGenerate.put(CharSequence.class, Codecs.STRING);
        codecGenerate.put(String.class, Codecs.STRING);

        codecGenerate.put(byte[].class, Codecs.BYTE_ARRAY);
        codecGenerate.put(Byte[].class, Codecs.BYTE_ARRAY);

        codecGenerate.put(ByteBuffer.class, Codecs.BYTE_BUFFER);

        codecGenerate.put(Class.class, Codecs.CLASS);

        codecGenerate.put(SimpleDateFormat.class, Codecs.SIMPLE_DATE_FORMAT);
        codecGenerate.put(Currency.class, Codecs.CURRENCY);
        codecGenerate.put(TimeZone.class, Codecs.TIME_ZONE);
        codecGenerate.put(InetAddress.class, Codecs.INET_ADDRESS);
        codecGenerate.put(Inet4Address.class, Codecs.INET_ADDRESS);
        codecGenerate.put(Inet6Address.class, Codecs.INET_ADDRESS);
        codecGenerate.put(InetSocketAddress.class, Codecs.INET_SOCKET_ADDRESS);
        codecGenerate.put(Appendable.class, Codecs.STRING_BUFFER);
        codecGenerate.put(StringBuffer.class, Codecs.STRING_BUFFER);
        codecGenerate.put(StringBuilder.class, Codecs.STRING_BUILDER);
        codecGenerate.put(Charset.class, Codecs.CHARSET);
        codecGenerate.put(Pattern.class, Codecs.PATTERN);
        codecGenerate.put(Locale.class, Codecs.LOCALE);
        codecGenerate.put(URI.class, Codecs.URI_);
        codecGenerate.put(URL.class, Codecs.URL);
        codecGenerate.put(UUID.class, Codecs.UUID_);

        // atomic
        codecGenerate.put(AtomicBoolean.class, Codecs.ATOMIC_BOOLEAN);
        codecGenerate.put(AtomicInteger.class, Codecs.ATOMIC_INTEGER);
        codecGenerate.put(AtomicLong.class, Codecs.ATOMIC_LONG);
        codecGenerate.put(AtomicIntegerArray.class, Codecs.ATOMIC_INTEGER_ARRAY);
        codecGenerate.put(AtomicLongArray.class, Codecs.ATOMIC_LONG_ARRAY);
        // other java8 time
        codecGenerate.put(Duration.class, Codecs.DURATION);
        codecGenerate.put(Period.class, Codecs.PERIOD);
    }

    public static void newDataCodecGenerate(List<UniversalFactory<Codec>> factories, Converter<?> converter) {
        DataCodecGenerate codecGenerate = new DataCodecGenerate(factories, converter);
        CONVERTER_DATA_CODEC_GENERATE_MAP.put(converter, codecGenerate);
    }

    public static DataCodecGenerate getDataCodecGenerate(Converter<?> converter) {
        return MapUtil.computeIfAbsent(CONVERTER_DATA_CODEC_GENERATE_MAP, converter, DataCodecGenerate::newDefault);
    }
}
