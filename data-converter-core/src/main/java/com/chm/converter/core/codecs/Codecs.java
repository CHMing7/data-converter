package com.chm.converter.core.codecs;

import com.chm.converter.core.exception.CodecException;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.StringUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Period;
import java.util.Arrays;
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
 * 常用编解码器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-03
 **/
public interface Codecs {

    IdentityCodec<Boolean> BOOLEAN = IdentityCodec.create(
            TypeToken.get(Boolean.class),
            (b, dw) -> dw.writeBoolean(b),
            DataReader::readBoolean
    );

    IdentityCodec<Character> CHARACTER = IdentityCodec.create(
            TypeToken.get(Character.class),
            (c, dw) -> dw.writeString(String.valueOf(c)),
            DataReader::readChar
    );

    IdentityCodec<Byte> BYTE = IdentityCodec.create(
            TypeToken.get(Byte.class),
            (b, dw) -> dw.writeByte(b),
            DataReader::readByte
    );

    IdentityCodec<Short> SHORT = IdentityCodec.create(
            TypeToken.get(Short.class),
            (s, dw) -> dw.writeShort(s),
            DataReader::readShort
    );

    IdentityCodec<Integer> INTEGER = IdentityCodec.create(
            TypeToken.get(Integer.class),
            (i, dw) -> dw.writeInt(i),
            DataReader::readInt
    );

    IdentityCodec<Float> FLOAT = IdentityCodec.create(
            TypeToken.get(Float.class),
            (f, dw) -> dw.writeFloat(f),
            DataReader::readFloat
    );

    IdentityCodec<Double> DOUBLE = IdentityCodec.create(
            TypeToken.get(Double.class),
            (d, dw) -> dw.writeDouble(d),
            DataReader::readDouble
    );

    IdentityCodec<Long> LONG = IdentityCodec.create(
            TypeToken.get(Long.class),
            (l, dw) -> dw.writeLong(l),
            DataReader::readLong
    );

    SimpleToStringCodec<BigDecimal> BIG_DECIMAL = SimpleToStringCodec.create(
            TypeToken.get(BigDecimal.class),
            BigDecimal::new
    );

    SimpleCodec<BigInteger, byte[]> BIG_INTEGER = SimpleCodec.create(
            TypeToken.get(byte[].class),
            TypeToken.get(BigInteger.class),
            BigInteger::toByteArray,
            BigInteger::new,
            (bytes, dw) -> dw.writeByteArray(bytes),
            DataReader::readByteArray
    );

    StringCodec STRING = StringCodec.INSTANCE;

    IdentityCodec<byte[]> BYTE_ARRAY = IdentityCodec.create(
            TypeToken.get(byte[].class),
            (bytes, dw) -> dw.writeByteArray(bytes),
            DataReader::readByteArray
    );

    SimpleCodec<ByteBuffer, byte[]> BYTE_BUFFER = SimpleCodec.create(
            TypeToken.get(byte[].class),
            TypeToken.get(ByteBuffer.class),
            ByteBuffer::array,
            ByteBuffer::wrap,
            (bytes, dw) -> dw.writeByteArray(bytes),
            DataReader::readByteArray
    );


    SimpleCodec<Class<?>, String> CLASS = SimpleCodec.create(
            TypeToken.get(String.class),
            new TypeToken<Class<?>>() {
            },
            Class::getName,
            className -> {
                try {
                    return Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new CodecException("not found class: " + className);
                }
            },
            (s, dw) -> dw.writeString(s),
            DataReader::readString
    );

    SimpleToStringCodec<SimpleDateFormat> SIMPLE_DATE_FORMAT = SimpleToStringCodec.create(
            TypeToken.get(SimpleDateFormat.class),
            SimpleDateFormat::new
    );

    SimpleToStringCodec<Currency> CURRENCY = SimpleToStringCodec.create(
            TypeToken.get(Currency.class),
            Currency::getInstance
    );

    SimpleToStringCodec<TimeZone> TIME_ZONE = SimpleToStringCodec.create(
            TypeToken.get(TimeZone.class),
            TimeZone::getTimeZone
    );

    SimpleToStringCodec<InetAddress> INET_ADDRESS = SimpleToStringCodec.create(
            TypeToken.get(InetAddress.class),
            host -> {
                try {
                    return InetAddress.getByName(host);
                } catch (UnknownHostException e) {
                    throw new CodecException("unknown host: " + host);
                }
            }
    );

    SimpleToStringCodec<InetSocketAddress> INET_SOCKET_ADDRESS = SimpleToStringCodec.create(
            TypeToken.get(InetSocketAddress.class),
            str -> {
                List<String> splitTrim = StringUtil.splitTrim(str, ":");
                String host = splitTrim.get(0);
                String portStr = splitTrim.get(1);
                InetAddress inetAddress;
                try {
                    inetAddress = InetAddress.getByName(host);
                } catch (UnknownHostException e) {
                    throw new CodecException("unknown host: " + host);
                }
                Integer port = Integer.parseInt(portStr);
                return new InetSocketAddress(inetAddress, port);
            }
    );

    SimpleToStringCodec<StringBuffer> STRING_BUFFER = SimpleToStringCodec.create(
            TypeToken.get(StringBuffer.class),
            StringBuffer::new
    );

    SimpleToStringCodec<StringBuilder> STRING_BUILDER = SimpleToStringCodec.create(
            TypeToken.get(StringBuilder.class),
            StringBuilder::new
    );

    SimpleToStringCodec<Charset> CHARSET = SimpleToStringCodec.create(
            TypeToken.get(Charset.class),
            Charset::forName
    );

    SimpleToStringCodec<Pattern> PATTERN = SimpleToStringCodec.create(
            TypeToken.get(Pattern.class),
            Pattern::compile
    );

    SimpleToStringCodec<Locale> LOCALE = SimpleToStringCodec.create(
            TypeToken.get(Locale.class),
            Locale::new
    );

    SimpleToStringCodec<URI> URI_ = SimpleToStringCodec.create(
            TypeToken.get(URI.class),
            URI::create
    );

    SimpleToStringCodec<URL> URL = SimpleToStringCodec.create(TypeToken.get(URL.class),
            str -> {
                try {
                    return new URL(str);
                } catch (MalformedURLException e) {
                    throw new CodecException(e.getMessage());
                }
            });

    SimpleToStringCodec<UUID> UUID_ = SimpleToStringCodec.create(
            TypeToken.get(UUID.class),
            UUID::fromString
    );

    SimpleToStringCodec<AtomicBoolean> ATOMIC_BOOLEAN = SimpleToStringCodec.create(
            TypeToken.get(AtomicBoolean.class),
            str -> {
                AtomicBoolean atomicBoolean = new AtomicBoolean();
                if (str == null) {
                    atomicBoolean.set(false);
                }
                if (StringUtil.isNotBlank(str)) {
                    str = str.trim().toLowerCase();
                    atomicBoolean.set("true".equals(str));
                }
                return atomicBoolean;
            });

    SimpleToStringCodec<AtomicInteger> ATOMIC_INTEGER = SimpleToStringCodec.create(
            TypeToken.get(AtomicInteger.class),
            str -> new AtomicInteger(Integer.decode(str)));

    SimpleToStringCodec<AtomicLong> ATOMIC_LONG = SimpleToStringCodec.create(
            TypeToken.get(AtomicLong.class),
            str -> new AtomicLong(Long.decode(str))
    );

    /*  SimpleToStringCodec<AtomicReference<?>> ATOMIC_REFERENCE = SimpleToStringCodec.create();*/

    SimpleToStringCodec<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY = SimpleToStringCodec.create(
            TypeToken.get(AtomicIntegerArray.class),
            str -> {
                String s = StringUtil.removeAny("[", "]");
                String[] strArray = StringUtil.splitToArray(s, ',');
                int[] ints = Arrays.stream(strArray).mapToInt(Integer::decode).toArray();
                return new AtomicIntegerArray(ints);
            }
    );

    SimpleToStringCodec<AtomicLongArray> ATOMIC_LONG_ARRAY = SimpleToStringCodec.create(
            TypeToken.get(AtomicLongArray.class),
            str -> {
                String s = StringUtil.removeAny("[", "]");
                String[] strArray = StringUtil.splitToArray(s, ',');
                long[] longs = Arrays.stream(strArray).mapToLong(Long::decode).toArray();
                return new AtomicLongArray(longs);
            }
    );

    SimpleToStringCodec<Duration> DURATION = SimpleToStringCodec.create(
            TypeToken.get(Duration.class),
            Duration::parse,
            true
    );

    SimpleToStringCodec<Period> PERIOD = SimpleToStringCodec.create(
            TypeToken.get(Period.class),
            Period::parse,
            true
    );
}
