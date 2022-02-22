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
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
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
        this(null, null);
    }

    public DataCodecGenerate(List<UniversalFactory<Codec>> factories, Converter<?> converter) {
        super(factories);
        this.converter = converter;
    }

    public static DataCodecGenerate newDefault(Converter<?> converter) {
        List<UniversalFactory<Codec>> factories = new ArrayList<>();
        factories.add(new JavaBeanCodecFactory(converter));
        factories.add(new EnumCodecFactory(converter));
        factories.add(new MapCodecFactory());
        factories.add(new CollectionCodecFactory());
        factories.add(new ArrayCodecFactory());
        factories.add(new DefaultDateCodecFactory(converter));
        factories.add(new Java8TimeCodecFactory(converter));
        factories.add(new ObjectCodecFactory());
        DataCodecGenerate generate = new DataCodecGenerate(factories, converter);
        generate.put(Boolean.class, Codecs.BOOLEAN);
        generate.put(boolean.class, Codecs.BOOLEAN);
        generate.put(Character.class, Codecs.CHARACTER);
        generate.put(char.class, Codecs.CHARACTER);
        generate.put(Byte.class, Codecs.BYTE);
        generate.put(byte.class, Codecs.BYTE);
        generate.put(Short.class, Codecs.SHORT);
        generate.put(short.class, Codecs.SHORT);
        generate.put(Integer.class, Codecs.INTEGER);
        generate.put(int.class, Codecs.INTEGER);
        generate.put(Long.class, Codecs.LONG);
        generate.put(long.class, Codecs.LONG);
        generate.put(Float.class, Codecs.FLOAT);
        generate.put(float.class, Codecs.FLOAT);
        generate.put(Double.class, Codecs.DOUBLE);
        generate.put(double.class, Codecs.DOUBLE);
        generate.put(BigDecimal.class, Codecs.BIG_DECIMAL);
        generate.put(BigInteger.class, Codecs.BIG_INTEGER);
        generate.put(CharSequence.class, Codecs.STRING);
        generate.put(String.class, Codecs.STRING);

        generate.put(byte[].class, Codecs.BYTE_ARRAY);
        generate.put(Byte[].class, Codecs.BYTE_ARRAY);

        generate.put(ByteBuffer.class, Codecs.BYTE_BUFFER);

        generate.put(Class.class, Codecs.CLASS);

        generate.put(SimpleDateFormat.class, Codecs.SIMPLE_DATE_FORMAT);
        generate.put(Currency.class, Codecs.CURRENCY);
        generate.put(TimeZone.class, Codecs.TIME_ZONE);
        generate.put(InetAddress.class, Codecs.INET_ADDRESS);
        generate.put(Inet4Address.class, Codecs.INET_ADDRESS);
        generate.put(Inet6Address.class, Codecs.INET_ADDRESS);
        // put(InetSocketAddress.class, MiscCodec.instance);
        generate.put(Appendable.class, Codecs.STRING_BUFFER);
        generate.put(StringBuffer.class, Codecs.STRING_BUFFER);
        generate.put(StringBuilder.class, Codecs.STRING_BUILDER);
        generate.put(Charset.class, Codecs.CHARSET);
        generate.put(Pattern.class, Codecs.PATTERN);
        generate.put(Locale.class, Codecs.LOCALE);
        generate.put(URI.class, Codecs.URI_);
        generate.put(URL.class, Codecs.URL);
        generate.put(UUID.class, Codecs.UUID_);

        // atomic
        generate.put(AtomicBoolean.class, Codecs.ATOMIC_BOOLEAN);
        generate.put(AtomicInteger.class, Codecs.ATOMIC_INTEGER);
        generate.put(AtomicLong.class, Codecs.ATOMIC_LONG);
     /*   put(AtomicReference.class, SimpleToStringCodec.create(str ->
                new AtomicReference<>(Long.decode(str))
        ));*/
        generate.put(AtomicIntegerArray.class, Codecs.ATOMIC_INTEGER_ARRAY);
        generate.put(AtomicLongArray.class, Codecs.ATOMIC_LONG_ARRAY);

        /*put(WeakReference.class, SimpleToStringCodec.create(WeakReference::new));
        put(SoftReference.class, SimpleToStringCodec.create(SoftReference::new));*/

        return generate;
    }

    public Converter<?> getConverter() {
        return converter;
    }

    public static final class DataCodecGenerateBuilder {

        private List<UniversalFactory<Codec>> factories;

        private Converter<?> converter;

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

        public DataCodecGenerate build() {
            return new DataCodecGenerate(factories, converter);
        }
    }
}
