package com.chm.converter.core.pack;

import com.chm.converter.core.value.Value;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

/**
 * 数据打包器
 * @author caihongming
 * @version v1.0
 * @since 2021-12-04
 **/
public interface DataPacker extends Closeable, Flushable {

    /**
     * 写入{@code null}
     *
     * @return
     * @throws IOException
     */
    DataPacker packNull() throws IOException;

    /**
     * 写入{@code boolean}值
     *
     * @param b
     * @return
     * @throws IOException
     */
    DataPacker packBoolean(boolean b) throws IOException;

    /**
     * 写入{@code byte}值
     *
     * @param b
     * @return
     * @throws IOException
     */
    DataPacker packByte(byte b) throws IOException;

    /**
     * 写入{@code short}值
     *
     * @param v
     * @return
     * @throws IOException
     */
    DataPacker packShort(short v) throws IOException;

    /**
     * 写入{@code int}值
     *
     * @param r
     * @return
     * @throws IOException
     */
    DataPacker packInt(int r) throws IOException;

    /**
     * 写入{@code long}值
     *
     * @param v
     * @return
     * @throws IOException
     */
    DataPacker packLong(long v) throws IOException;

    /**
     * 写入{@code BigInteger}值
     *
     * @param bi
     * @return
     * @throws IOException
     */
    DataPacker packBigInteger(BigInteger bi) throws IOException;


    /**
     * 写入{@code BigDecimal}值
     *
     * @param bi
     * @return
     * @throws IOException
     */
    DataPacker packBigDecimal(BigDecimal bi) throws IOException;

    /**
     * 写入{@code float}值
     *
     * @param v
     * @return
     * @throws IOException
     */
    DataPacker packFloat(float v) throws IOException;

    /**
     * 写入{@code double}值
     *
     * @param v
     * @return
     * @throws IOException
     */
    DataPacker packDouble(double v) throws IOException;

    /**
     * 写入{@code char}值
     *
     * @param c
     * @return
     * @throws IOException
     */
    DataPacker packChar(char c) throws IOException;

    /**
     * 写入{@code String}值
     *
     * @param s
     * @return
     * @throws IOException
     */
    DataPacker packString(String s) throws IOException;

    /**
     * 写入字节数组
     *
     * @param src
     * @return
     * @throws IOException
     */
    DataPacker packByteArray(byte[] src) throws IOException;

    /**
     * 写入字节数组
     *
     * @param src
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    DataPacker packByteArray(byte[] src, int off, int len) throws IOException;

    /**
     * 写入字节数组
     *
     * @param src
     * @return
     * @throws IOException
     */
    DataPacker writeByteArray(byte[] src) throws IOException;

    /**
     * 写入字节数组
     *
     * @param src
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    DataPacker writeByteArray(byte[] src, int off, int len) throws IOException;

    /**
     * 写入字节数组
     *
     * @param src
     * @return
     * @throws IOException
     */
    DataPacker addByteArray(byte[] src) throws IOException;

    /**
     * 写入字节数组
     *
     * @param src
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    DataPacker addByteArray(byte[] src, int off, int len) throws IOException;

    /**
     * 写入{@code Array}值
     *
     * @param value
     * @return
     * @throws IOException
     */
    DataPacker packArray(Object value) throws IOException;

    /**
     * 写入{@code Collection}值
     *
     * @param coll
     * @return
     * @throws IOException
     */
    <E> DataPacker packCollection(Collection<E> coll) throws IOException;

    /**
     * 写入{@code Map}值
     *
     * @param map
     * @return
     * @throws IOException
     */
    <K, V> DataPacker packMap(Map<K, V> map) throws IOException;

    /**
     * 写入{@code Enum}值
     *
     * @param value
     * @return
     * @throws IOException
     */
    <E extends Enum<E>> DataPacker packEnum(Enum<E> value) throws IOException;

    /**
     * 写入{@code Class}值
     *
     * @param clazz
     * @return
     * @throws IOException
     */
    <C> DataPacker packClass(Class<C> clazz) throws IOException;

    /**
     * 写入数值大小
     *
     * @param valueSize
     * @return
     * @throws IOException
     */
    DataPacker packValueSize(int valueSize) throws IOException;

    /**
     * 写入动态类型的值
     *
     * @param v
     * @return
     * @throws IOException
     */
    DataPacker packValue(Value v) throws IOException;

    /**
     * 写入动态类型的值
     *
     * @param o
     * @return
     * @throws IOException
     */
    DataPacker packValue(Object o) throws IOException;
}
