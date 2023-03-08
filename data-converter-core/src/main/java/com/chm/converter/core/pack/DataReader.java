package com.chm.converter.core.pack;

import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.reflect.TypeToken;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

/**
 * 数据读取器
 *
 * @author caihongming
 * @version v1.0
 * @date 2021-12-04
 **/
public interface DataReader extends Closeable {

    /**
     * 如果此解包器具有更多元素，则返回 true
     *
     * @return 是否含有下一个
     * @throws IOException
     */
    boolean hasNext() throws IOException;

    /**
     * 跳过下一个数据
     *
     * @throws IOException
     */
    void skipAny() throws IOException;

    /**
     * 读取字段信息
     *
     * @param type 字段所属类型
     * @return
     * @throws IOException
     */
    FieldInfo readFieldBegin(Type type) throws IOException;

    /**
     * 读取字段信息
     *
     * @param javaBeanInfo 字段所属类对象信息
     * @return
     * @throws IOException
     */
    FieldInfo readFieldBegin(JavaBeanInfo<?> javaBeanInfo) throws IOException;

    /**
     * 读取字段信息结束
     *
     * @param type 字段所属类型
     * @return
     * @throws IOException
     */
    void readFieldEnd(Type type) throws IOException;

    /**
     * 读取字段信息结束
     *
     * @param javaBeanInfo 字段所属类对象信息
     * @return
     * @throws IOException
     */
    void readFieldEnd(JavaBeanInfo<?> javaBeanInfo) throws IOException;

    /**
     * 读取{@code boolean}值
     *
     * @return {@code boolean}值
     * @throws IOException
     */
    boolean readBoolean() throws IOException;

    /**
     * 读取一个字节
     *
     * @return
     * @throws IOException
     */
    byte readByte() throws IOException;

    /**
     * 读取short
     *
     * @return
     * @throws IOException
     */
    short readShort() throws IOException;

    /**
     * 读取{@code int}值
     *
     * @return
     * @throws IOException
     */
    int readInt() throws IOException;

    /**
     * 读取{@code long}值
     *
     * @return
     * @throws IOException
     */
    long readLong() throws IOException;

    /**
     * 读取{@code BigInteger}值
     *
     * @return
     * @throws IOException
     */
    BigInteger readBigInteger() throws IOException;

    /**
     * 读取{@code float}值
     *
     * @return
     * @throws IOException
     */
    float readFloat() throws IOException;

    /**
     * 读取{@code double}值
     *
     * @return
     * @throws IOException
     */
    double readDouble() throws IOException;

    /**
     * 读取{@code BigDecimal}值
     *
     * @return
     * @throws IOException
     */
    BigDecimal readBigDecimal() throws IOException;

    /**
     * 读取{@code char}值
     *
     * @return
     * @throws IOException
     */
    char readChar() throws IOException;

    /**
     * 读取{@code String}值
     *
     * @return
     * @throws IOException
     */
    String readString() throws IOException;

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
     * 读取一个数组值
     *
     * @return
     * @throws IOException
     */
    <T> T[] readArray() throws IOException;

    /**
     * 读取一个数组值
     *
     * @param targetType 数组类型
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T[] readArray(TypeToken<T[]> targetType) throws IOException;

    /**
     * 读取集合
     *
     * @param targetType 集合类型
     * @param <E>
     * @return 集合
     * @throws IOException
     */
    <E> Collection<E> readCollection(TypeToken<Collection<E>> targetType) throws IOException;

    /**
     * 读取{@code Map}值
     *
     * @param targetType {@code Map}类型
     * @param <K>
     * @param <V>
     * @return {@code Map}值
     * @throws IOException
     */
    <K, V> Map<K, V> readMap(TypeToken<Map<K, V>> targetType) throws IOException;

    /**
     * 读取{@code Enum}值
     *
     * @param targetType {@code Enum}类型
     * @param <E>
     * @return {@code Enum}值
     * @throws IOException
     */
    <E extends Enum<E>> Enum<E> readEnum(TypeToken<Enum<E>> targetType) throws IOException;

    /**
     * 读取{@code Class}值
     *
     * @param <T>
     * @return {@code Class}值
     * @throws IOException
     */
    <T> Class<T> readClass() throws IOException;

    /**
     * 读取Bean值
     *
     * @param targetType {@code Bean}类型
     * @param <T>
     * @return {@code Bean}值
     * @throws IOException
     */
    <T> T readBean(TypeToken<T> targetType) throws IOException;

    /**
     * 读取下一个数据
     *
     * @param targetType 数据类型
     * @return 字段数据
     * @throws IOException
     */
    default Object readAny(TypeToken targetType) throws IOException {
        Class<?> cls = targetType.getRawType();
        int token = DataToken.getToken(cls);
        switch (token) {
            case DataToken.BOOLEAN:
                return readBoolean();

            case DataToken.BYTE:
                return readByte();

            case DataToken.SHORT:
                return readShort();

            case DataToken.INTEGER:
                return readInt();

            case DataToken.LONG:
                return readLong();

            case DataToken.BIG_INTEGER:
                return readBigInteger();

            case DataToken.FLOAT:
                return readFloat();

            case DataToken.DOUBLE:
                return readDouble();

            case DataToken.BIG_DECIMAL:
                return readBigDecimal();

            case DataToken.CHAR:
                return readChar();

            case DataToken.STRING:
                return readString();

            case DataToken.BYTE_ARRAY:
                return readByteArray();

            case DataToken.ARRAY:
                return readArray(targetType);

            case DataToken.COLLECTION:
                return readCollection(targetType);

            case DataToken.MAP:
                return readMap(targetType);

            case DataToken.ENUM:
                return readEnum(targetType);

            case DataToken.CLASS:
                return readClass();

            default:
                return readBean(targetType);
        }
    }
}
