package com.chm.converter.codec;


import com.chm.converter.core.utils.StringUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * 常用编解码器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-03
 **/
public interface Codecs {

    SimpleToStringCodec<Boolean> BOOLEAN = SimpleToStringCodec.create(str -> {
        if (str == null) {
            return false;
        }
        if (StringUtil.isNotBlank(str)) {
            str = str.trim().toLowerCase();
            return "true".equals(str);
        }
        return false;
    });

    SimpleToStringCodec<Character> CHARACTER = SimpleToStringCodec.create(str -> {
        if (str == null || str.length() == 0) {
            return null;
        }
        if (str.length() != 1) {
            throw new CodecException("can not cast to char, value : " + str);
        }
        return str.charAt(0);
    });

    SimpleToStringCodec<Byte> BYTE = SimpleToStringCodec.create(Byte::decode);

    SimpleToStringCodec<Double> DOUBLE = SimpleToStringCodec.create(Double::valueOf);

    SimpleToStringCodec<Float> FLOAT = SimpleToStringCodec.create(Float::valueOf);

    SimpleToStringCodec<Integer> INTEGER = SimpleToStringCodec.create(Integer::decode);

    SimpleToStringCodec<Long> LONG = SimpleToStringCodec.create(Long::decode);

    SimpleToStringCodec<Short> SHORT = SimpleToStringCodec.create(Short::decode);

    SimpleToStringCodec<BigDecimal> BIG_DECIMAL = SimpleToStringCodec.create(BigDecimal::new);

    SimpleToStringCodec<BigInteger> BIG_INTEGER = SimpleToStringCodec.create(BigInteger::new);

    SimpleToStringCodec<String> STRING = SimpleToStringCodec.create(String::toString);

    SimpleToStringCodec<Class<?>> CLASS = SimpleToStringCodec.create(className -> {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new CodecException("not found class: " + className);
        }
    });

    SimpleToStringCodec<InetAddress> INET_ADDRESS = SimpleToStringCodec.create(host -> {
        try {
            return InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new CodecException("unknown host: " + host);
        }
    });


    SimpleToStringCodec<AtomicBoolean> ATOMIC_BOOLEAN = SimpleToStringCodec.create(str -> {
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

    SimpleToStringCodec<AtomicInteger> ATOMIC_INTEGER = SimpleToStringCodec.create(str ->
            new AtomicInteger(Integer.decode(str)));

    SimpleToStringCodec<AtomicLong> ATOMIC_LONG = SimpleToStringCodec.create(str ->
            new AtomicLong(Long.decode(str)));

    /*  SimpleToStringCodec<AtomicReference<?>> ATOMIC_REFERENCE = SimpleToStringCodec.create();*/

    SimpleToStringCodec<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY = SimpleToStringCodec.create(str -> {
        String s = StringUtil.removeAny("[", "]");
        String[] strArray = StringUtil.splitToArray(s, ',');
        int[] ints = Arrays.stream(strArray).mapToInt(Integer::decode).toArray();
        return new AtomicIntegerArray(ints);
    });

    SimpleToStringCodec<AtomicLongArray> ATOMIC_LONG_ARRAY = SimpleToStringCodec.create(str -> {
        String s = StringUtil.removeAny("[", "]");
        String[] strArray = StringUtil.splitToArray(s, ',');
        long[] longs = Arrays.stream(strArray).mapToLong(Long::decode).toArray();
        return new AtomicLongArray(longs);
    });


}
