package com.chm.converter.core.pack;

import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.reflect.ConverterPreconditions;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

/**
 * 数据写入器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-12-04
 **/
public interface DataWriter extends Closeable, Flushable {

    /**
     * 获取输出目标对象
     *
     * @param <T>
     * @return
     */
    <T> T getOutputTarget();

    /**
     * 写入字段信息开始
     *
     * @param fieldInfo 字段信息
     * @return 数据写入器
     * @throws IOException
     */
    default DataWriter writeFieldStart(FieldInfo fieldInfo) throws IOException {
        writeFieldStart(fieldInfo.getSortedNumber(), fieldInfo);
        return this;
    }

    /**
     * 写入字段信息开始
     *
     * @param fieldNumber 字段排序
     * @param fieldInfo   字段信息
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeFieldStart(int fieldNumber, FieldInfo fieldInfo) throws IOException;

    /**
     * 写入{@code null}值字段信息开始
     *
     * @param fieldInfo 字段信息
     * @return 数据写入器
     * @throws IOException
     */
    default DataWriter writeFieldNull(FieldInfo fieldInfo) throws IOException {
        writeFieldNull(fieldInfo.getSortedNumber(), fieldInfo);
        return this;
    }

    /**
     * 写入{@code null}值字段信息开始
     *
     * @param fieldNumber 字段排序
     * @param fieldInfo   字段信息
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeFieldNull(int fieldNumber, FieldInfo fieldInfo) throws IOException;

    /**
     * 写入字段信息结束
     *
     * @param fieldInfo 字段信息
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeFieldEnd(FieldInfo fieldInfo) throws IOException;

    /**
     * 写入字段信息结束
     *
     * @param fieldNumber 字段排序
     * @param fieldInfo   字段信息
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeFieldEnd(int fieldNumber, FieldInfo fieldInfo) throws IOException;

    /**
     * 写入{@code null}
     *
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeNull() throws IOException;

    /**
     * 写入一个{@code boolean}值
     *
     * @param value {@code boolean}值
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeBoolean(boolean value) throws IOException;

    /**
     * 写入一个{@code byte}
     *
     * @param value {@code byte}值
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeByte(byte value) throws IOException;

    /**
     * 写入一个{@code short}
     *
     * @param value {@code short}值
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeShort(short value) throws IOException;

    /**
     * 写入一个{@code int}
     *
     * @param value {@code int}值
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeInt(int value) throws IOException;

    /**
     * 写入一个{@code long}
     *
     * @param value {@code long}值
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeLong(long value) throws IOException;

    /**
     * 写入一个{@code BigInteger}
     *
     * @param value {@code BigInteger}值
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeBigInteger(BigInteger value) throws IOException;

    /**
     * 写入一个{@code float}
     *
     * @param value {@code float}值
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeFloat(float value) throws IOException;

    /**
     * 写入一个{@code double}
     *
     * @param value {@code double}值
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeDouble(double value) throws IOException;

    /**
     * 写入一个{@code BigDecimal}
     *
     * @param value {@code BigDecimal}值
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeBigDecimal(BigDecimal value) throws IOException;

    /**
     * 写入一个{@code char}
     *
     * @param value {@code char}值
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeChar(char value) throws IOException;

    /**
     * 写入一个{@code String}
     *
     * @param value {@code String}值
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeString(String value) throws IOException;

    /**
     * 写入字符串
     *
     * @param text   字节数组
     * @param offset 偏移量
     * @param len    长度
     * @return 数据写入器
     * @throws IOException
     */
    default DataWriter writeString(char[] text, int offset, int len) throws IOException {
        ConverterPreconditions.checkArgument(text != null, "null text");
        String str = new String(text, offset, len);
        writeString(str);
        return this;
    }

    /**
     * 写入UTF-8 编码的字符串
     *
     * @param text
     * @param offset
     * @param length
     * @return 数据写入器
     * @throws IOException
     */
    default DataWriter writeUtf8String(byte[] text, int offset, int length) throws IOException {
        ConverterPreconditions.checkArgument(text != null, "null text");
        String str = new String(text, offset, length, StandardCharsets.UTF_8);
        writeString(str);
        return this;
    }

    /**
     * 写入{@code byte[]}
     *
     * @param value {@code byte[]}值
     * @return 数据写入器
     * @throws IOException
     */
    DataWriter writeByteArray(byte[] value) throws IOException;


    /**
     * 写入字节
     *
     * @param value  字节数组
     * @param offset 偏移量
     * @param len    长度
     * @return
     * @throws IOException
     */
    DataWriter writeByteArray(byte[] value, int offset, int len) throws IOException;

    /**
     * 写入字节
     *
     * @param value       字节输入流
     * @param valueLength 字节长度
     * @return
     * @throws IOException
     */
    DataWriter writeByteArray(InputStream value, int valueLength) throws IOException;

    /**
     * 写入一个数组
     *
     * @param value 数组值
     * @param <T>
     * @return 数据写入器
     * @throws IOException
     */
    <T> DataWriter writeArray(T[] value) throws IOException;

    /**
     * 写入一个集合
     *
     * @param value 集合值
     * @return 数据写入器
     * @throws IOException
     */
    <E> DataWriter writeCollection(Collection<E> value) throws IOException;

    /**
     * 写入一个{@code Map}
     *
     * @param value Map
     * @return 数据写入器
     * @throws IOException
     */
    <K, V> DataWriter writeMap(Map<K, V> value) throws IOException;

    /**
     * 写入一个枚举
     *
     * @param value
     * @param <E>
     * @return 数据写入器
     * @throws IOException
     */
    <E extends Enum<E>> DataWriter writeEnum(Enum<E> value) throws IOException;

    /**
     * 写入一个{@code Class}
     *
     * @param value {@code Class}值
     * @param <T>
     * @return 数据写入器
     * @throws IOException
     */
    <T> DataWriter writeClass(Class<T> value) throws IOException;

    /**
     * 写入一个Java Bean
     *
     * @param value Java Bean值
     * @param <T>
     * @return 数据写入器
     * @throws IOException
     */
    <T> DataWriter writeBean(T value) throws IOException;

    /**
     * 写入任意数据
     *
     * @param value 任意数据对象
     * @return 数据写入器
     * @throws IOException
     */
    default DataWriter writeAny(Object value) throws IOException {
        if (value == null) {
            return writeNull();
        }
        Class<?> cls = value.getClass();
        if (cls == boolean.class || cls == Boolean.class) {
            return writeBoolean((boolean) value);
        }
        if (cls == byte.class || cls == Byte.class) {
            return writeByte((byte) value);
        }
        if (cls == short.class || cls == Short.class) {
            return writeShort((short) value);
        }
        if (cls == int.class || cls == Integer.class) {
            return writeInt((int) value);
        }
        if (cls == long.class || cls == Long.class) {
            return writeLong((long) value);
        }
        if (cls == float.class || cls == Float.class) {
            return writeFloat((float) value);
        }
        if (cls == double.class || cls == Double.class) {
            return writeDouble((double) value);
        }
        if (cls == BigInteger.class) {
            return writeBigInteger((BigInteger) value);
        }
        if (cls == BigDecimal.class) {
            return writeBigDecimal((BigDecimal) value);
        }
        if (cls == byte[].class) {
            return writeByteArray((byte[]) value);
        }

        if (value instanceof CharSequence) {
            return writeString(value.toString());
        }
        if (cls.isArray()) {
            return writeArray((Object[]) value);
        }
        if (value instanceof Map) {
            return writeMap((Map) value);
        }
        if (value instanceof Collection) {
            return writeCollection((Collection) value);
        }
        if (cls.isEnum()) {
            return writeEnum((Enum) value);
        }
        if (cls == Class.class) {
            return writeClass((Class) value);
        }
        return writeBean(value);
    }
}
