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

    IdentityCodec<Boolean> BOOLEAN = IdentityCodec.create(TypeToken.get(boolean.class),
            (b, dw) -> dw.writeBoolean(b), DataReader::readBoolean);

    IdentityCodec<Character> CHARACTER = IdentityCodec.create(TypeToken.get(char.class),
            (c, dw) -> dw.writeString(String.valueOf(c)), DataReader::readChar);

    IdentityCodec<Byte> BYTE = IdentityCodec.create(TypeToken.get(byte.class),
            (b, dw) -> dw.writeByte(b), DataReader::readByte);

    IdentityCodec<Short> SHORT = IdentityCodec.create(TypeToken.get(short.class),
            (s, dw) -> dw.writeShort(s), DataReader::readShort);

    IdentityCodec<Integer> INTEGER = IdentityCodec.create(TypeToken.get(int.class),
            (i, dw) -> dw.writeInt(i), DataReader::readInt);

    IdentityCodec<Float> FLOAT = IdentityCodec.create(TypeToken.get(float.class),
            (f, dw) -> dw.writeFloat(f), DataReader::readFloat);

    IdentityCodec<Double> DOUBLE = IdentityCodec.create(TypeToken.get(double.class),
            (d, dw) -> dw.writeDouble(d), DataReader::readDouble);

    IdentityCodec<Long> LONG = IdentityCodec.create(TypeToken.get(long.class),
            (l, dw) -> dw.writeLong(l), DataReader::readLong);

    SimpleToStringCodec<BigDecimal> BIG_DECIMAL = SimpleToStringCodec.create(TypeToken.get(BigDecimal.class),
            (s, dw) -> dw.writeString(s), BigDecimal::new, DataReader::readString);

    SimpleCodec<BigInteger, byte[]> BIG_INTEGER = SimpleCodec.create(TypeToken.get(byte[].class),
            TypeToken.get(BigInteger.class), BigInteger::toByteArray, BigInteger::new,
            (bytes, dw) -> dw.writeByteArray(bytes), DataReader::readByteArray);

    StringCodec STRING = StringCodec.INSTANCE;

    IdentityCodec<byte[]> BYTE_ARRAY = IdentityCodec.create(TypeToken.get(byte[].class),
            (bytes, dw) -> dw.writeByteArray(bytes), DataReader::readByteArray);

    SimpleCodec<ByteBuffer, byte[]> BYTE_BUFFER = SimpleCodec.create(TypeToken.get(byte[].class),
            TypeToken.get(ByteBuffer.class), ByteBuffer::array, ByteBuffer::wrap,
            (bytes, dw) -> dw.writeByteArray(bytes), DataReader::readByteArray);


    SimpleCodec<Class<?>, String> CLASS = SimpleCodec.create(TypeToken.get(String.class), new TypeToken<Class<?>>() {
            },
            Class::getName, className -> {
                try {
                    return Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new CodecException("not found class: " + className);
                }
            }, (s, dw) -> dw.writeString(s), DataReader::readString);

    SimpleToStringCodec<SimpleDateFormat> SIMPLE_DATE_FORMAT = SimpleToStringCodec.create(TypeToken.get(SimpleDateFormat.class),
            (s, dw) -> dw.writeString(s), SimpleDateFormat::new, DataReader::readString);

    SimpleToStringCodec<Currency> CURRENCY = SimpleToStringCodec.create(TypeToken.get(Currency.class),
            (s, dw) -> dw.writeString(s), Currency::getInstance, DataReader::readString);

    SimpleToStringCodec<TimeZone> TIME_ZONE = SimpleToStringCodec.create(TypeToken.get(TimeZone.class),
            (s, dw) -> dw.writeString(s), TimeZone::getTimeZone, DataReader::readString);

    SimpleToStringCodec<InetAddress> INET_ADDRESS = SimpleToStringCodec.create(TypeToken.get(InetAddress.class),
            (s, dw) -> dw.writeString(s), host -> {
                try {
                    return InetAddress.getByName(host);
                } catch (UnknownHostException e) {
                    throw new CodecException("unknown host: " + host);
                }
            }, DataReader::readString);

    SimpleToStringCodec<InetSocketAddress> INET_SOCKET_ADDRESS = SimpleToStringCodec.create(TypeToken.get(InetSocketAddress.class),
            (s, dw) -> dw.writeString(s), str -> {
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
            }, DataReader::readString);

    SimpleToStringCodec<StringBuffer> STRING_BUFFER = SimpleToStringCodec.create(TypeToken.get(StringBuffer.class),
            (s, dw) -> dw.writeString(s), StringBuffer::new, DataReader::readString);

    SimpleToStringCodec<StringBuilder> STRING_BUILDER = SimpleToStringCodec.create(TypeToken.get(StringBuilder.class),
            (s, dw) -> dw.writeString(s), StringBuilder::new, DataReader::readString);

    SimpleToStringCodec<Charset> CHARSET = SimpleToStringCodec.create(TypeToken.get(Charset.class),
            (s, dw) -> dw.writeString(s), Charset::forName, DataReader::readString);

    SimpleToStringCodec<Pattern> PATTERN = SimpleToStringCodec.create(TypeToken.get(Pattern.class),
            (s, dw) -> dw.writeString(s), Pattern::compile, DataReader::readString);

    SimpleToStringCodec<Locale> LOCALE = SimpleToStringCodec.create(TypeToken.get(Locale.class),
            (s, dw) -> dw.writeString(s), Locale::new, DataReader::readString);

    SimpleToStringCodec<URI> URI_ = SimpleToStringCodec.create(TypeToken.get(URI.class),
            (s, dw) -> dw.writeString(s), URI::create, DataReader::readString);

    SimpleToStringCodec<URL> URL = SimpleToStringCodec.create(TypeToken.get(URL.class),
            (s, dw) -> dw.writeString(s),
            str -> {
                try {
                    return new URL(str);
                } catch (MalformedURLException e) {
                    throw new CodecException(e.getMessage());
                }
            }, DataReader::readString);

    SimpleToStringCodec<UUID> UUID_ = SimpleToStringCodec.create(TypeToken.get(UUID.class),
            (s, dw) -> dw.writeString(s), UUID::fromString, DataReader::readString);

    SimpleToStringCodec<AtomicBoolean> ATOMIC_BOOLEAN = SimpleToStringCodec.create(TypeToken.get(AtomicBoolean.class),
            (s, dw) -> dw.writeString(s), str -> {
                AtomicBoolean atomicBoolean = new AtomicBoolean();
                if (str == null) {
                    atomicBoolean.set(false);
                }
                if (StringUtil.isNotBlank(str)) {
                    str = str.trim().toLowerCase();
                    atomicBoolean.set("true".equals(str));
                }
                return atomicBoolean;
            }, DataReader::readString);

    SimpleToStringCodec<AtomicInteger> ATOMIC_INTEGER = SimpleToStringCodec.create(TypeToken.get(AtomicInteger.class),
            (s, dw) -> dw.writeString(s), str -> new AtomicInteger(Integer.decode(str)), DataReader::readString);

    SimpleToStringCodec<AtomicLong> ATOMIC_LONG = SimpleToStringCodec.create(TypeToken.get(AtomicLong.class),
            (s, dw) -> dw.writeString(s), str -> new AtomicLong(Long.decode(str)), DataReader::readString);

    /*  SimpleToStringCodec<AtomicReference<?>> ATOMIC_REFERENCE = SimpleToStringCodec.create();*/

    SimpleToStringCodec<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY = SimpleToStringCodec.create(TypeToken.get(AtomicIntegerArray.class),
            (s, dw) -> dw.writeString(s), str -> {
                String s = StringUtil.removeAny("[", "]");
                String[] strArray = StringUtil.splitToArray(s, ',');
                int[] ints = Arrays.stream(strArray).mapToInt(Integer::decode).toArray();
                return new AtomicIntegerArray(ints);
            }, DataReader::readString);

    SimpleToStringCodec<AtomicLongArray> ATOMIC_LONG_ARRAY = SimpleToStringCodec.create(TypeToken.get(AtomicLongArray.class),
            (s, dw) -> dw.writeString(s), str -> {
                String s = StringUtil.removeAny("[", "]");
                String[] strArray = StringUtil.splitToArray(s, ',');
                long[] longs = Arrays.stream(strArray).mapToLong(Long::decode).toArray();
                return new AtomicLongArray(longs);
            }, DataReader::readString);

}
