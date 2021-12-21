package com.chm.converter.core.pack;

import com.chm.converter.core.value.Value;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

/**
 * 数据解包器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-12-04
 **/
public interface DataUnpacker extends Closeable {

    /**
     * 如果此解包器具有更多元素，则返回 true
     *
     * @return
     * @throws IOException
     */
    boolean hasNext() throws IOException;

    /**
     * 读取{@code null}
     *
     * @return
     * @throws IOException
     */
    void unpackNull() throws IOException;

    /**
     * 读取{@code boolean}值
     *
     * @return
     * @throws IOException
     */
    boolean unpackBoolean() throws IOException;

    /**
     * 读取一个字节
     *
     * @return
     * @throws IOException
     */
    byte unpackByte() throws IOException;

    /**
     * 读取一个short
     *
     * @return
     * @throws IOException
     */
    short unpackShort() throws IOException;

    /**
     * 读取{@code int}值
     *
     * @return
     * @throws IOException
     */
    int unpackInt() throws IOException;

    /**
     * 读取{@code long}值
     *
     * @return
     * @throws IOException
     */
    long unpackLong() throws IOException;

    /**
     * 读取{@code BigInteger}值
     *
     * @return
     * @throws IOException
     */
    BigInteger unpackBigInteger() throws IOException;


    /**
     * 读取{@code BigDecimal}值
     *
     * @return
     * @throws IOException
     */
    BigDecimal unpackBigDecimal() throws IOException;

    /**
     * 读取{@code float}值
     *
     * @return
     * @throws IOException
     */
    float unpackFloat() throws IOException;

    /**
     * 读取{@code double}值
     *
     * @return
     * @throws IOException
     */
    double unpackDouble() throws IOException;

    /**
     * 读取{@code char}值
     *
     * @return
     * @throws IOException
     */
    char unpackChar() throws IOException;

    /**
     * 读取{@code String}值
     *
     * @return
     * @throws IOException
     */
    String unpackString() throws IOException;

    /**
     * 读取字节数组
     *
     * @return
     * @throws IOException
     */
    byte[] unpackByteArray() throws IOException;

    /**
     * 读取字节数组
     *
     * @param src
     * @return
     * @throws IOException
     */
    void unpackByteArray(byte[] src) throws IOException;

    /**
     * 读取字节数组
     *
     * @param len
     * @return
     * @throws IOException
     */
    byte[] unpackByteArray(int len) throws IOException;

    /**
     * 读取字节数组
     *
     * @param len
     * @return
     * @throws IOException
     */
    void unpackByteArray(byte[] src, int off, int len) throws IOException;

    /**
     * 读取字节数组
     *
     * @return
     * @throws IOException
     */
    byte[] readByteArray() throws IOException;

    /**
     * 读取字节数组
     *
     * @param src
     * @return
     * @throws IOException
     */
    void readByteArray(byte[] src) throws IOException;

    /**
     * 读取字节数组
     *
     * @param len
     * @return
     * @throws IOException
     */
    byte[] readByteArray(int len) throws IOException;

    /**
     * 读取字节数组
     *
     * @param src
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    void readByteArray(byte[] src, int off, int len) throws IOException;

    /**
     * 读取{@code Array}值
     *
     * @return
     * @throws IOException
     */
    Object unpackArray() throws IOException;

    /**
     * 读取{@code Collection}值
     *
     * @return
     * @throws IOException
     */
    <E> Collection<E> unpackCollection() throws IOException;

    /**
     * 读取{@code Map}值
     *
     * @return
     * @throws IOException
     */
    <K, V> Map<K, V> unpackMap() throws IOException;

    /**
     * 读取{@code Enum}值
     *
     * @return
     * @throws IOException
     */
    <E extends Enum<E>> Enum<E> unpackEnum() throws IOException;

    /**
     * 读取{@code Class}值
     *
     * @return
     * @throws IOException
     */
    <C> Class<C> unpackClass() throws IOException;

    /**
     * 读取数值大小
     *
     * @return
     * @throws IOException
     */
    int unpackValueSize() throws IOException;

    /**
     * 读取动态类型的值
     *
     * @return
     * @throws IOException
     */
    Value unpackValue() throws IOException;

    /**
     * 读取动态类型的值
     *
     * @return
     * @throws IOException
     */
    Object unpackObject() throws IOException;
}
