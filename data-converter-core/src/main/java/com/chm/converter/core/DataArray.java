package com.chm.converter.core;

import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.exception.TypeCastException;
import com.chm.converter.core.reflect.ConverterPreconditions;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.ListUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-05-24
 **/
public class DataArray extends ArrayList<Object> {

    private final Converter<?> converter;

    private final DataCodecGenerate codecGenerate;

    public DataArray(Converter<?> converter) {
        ConverterPreconditions.checkNotNull(converter, "param converter cannot be null");
        this.converter = converter;
        this.codecGenerate = DataCodecGenerate.getDataCodecGenerate(converter);
    }

    public DataArray(Converter<?> converter, Object... items) {
        super(items.length);
        ConverterPreconditions.checkNotNull(converter, "param converter cannot be null");
        this.converter = converter;
        for (Object item : items) {
            add(item);
        }
        this.codecGenerate = DataCodecGenerate.getDataCodecGenerate(converter);
    }

    public DataArray(Converter<?> converter, Collection<?> collection) {
        super(collection);
        ConverterPreconditions.checkNotNull(converter, "param converter cannot be null");
        this.converter = converter;
        this.codecGenerate = DataCodecGenerate.getDataCodecGenerate(converter);
    }

    public Converter<?> getConverter() {
        return converter;
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link DataArray}
     *
     * @param index 元素下标
     * @return {@link DataArray} or null
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    @SuppressWarnings("unchecked")
    public DataArray getArray(int index) {
        Object value = get(index);

        return DataCast.castArray(value, this.converter);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link DataMapper}
     *
     * @param index 元素下标
     * @return {@link DataMapper} or null
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public DataMapper getMapper(int index) {
        Object value = get(index);

        return DataCast.castMapper(value, this.converter);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link String}
     *
     * @param index 元素下标
     * @return {@link String} or null
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public String getString(int index) {
        Object value = get(index);

        return DataCast.castString(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link Double}
     *
     * @param index 元素下标
     * @return {@link Double} or null
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为{@link Double}
     * @throws TypeCastException         类型不支持转化为{@link Double}
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public Double getDouble(int index) {
        Object value = get(index);

        return DataCast.castDouble(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的 double
     *
     * @param index 元素下标
     * @return double
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为 double
     * @throws TypeCastException         类型不支持转化为 double
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public double getDoubleValue(int index) {
        Object value = get(index);

        return DataCast.castDoubleValue(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link Float}
     *
     * @param index 元素下标
     * @return {@link Float} or null
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为{@link Float}
     * @throws TypeCastException         类型不支持转化为{@link Float}
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public Float getFloat(int index) {
        Object value = get(index);

        return DataCast.castFloat(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的 float
     *
     * @param index 元素下标
     * @return float
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为 float
     * @throws TypeCastException         类型不支持转化为 float
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public float getFloatValue(int index) {
        Object value = get(index);

        return DataCast.castFloatValue(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link Long}
     *
     * @param index 元素下标
     * @return {@link Long} or null
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为{@link Long}
     * @throws TypeCastException         类型不支持转化为{@link Long}
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public Long getLong(int index) {
        Object value = get(index);

        return DataCast.castLong(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的 long
     *
     * @param index 元素下标
     * @return long
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为 long
     * @throws TypeCastException         类型不支持转化为 long
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public long getLongValue(int index) {
        Object value = get(index);

        return DataCast.castLongValue(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link Integer}
     *
     * @param index 元素下标
     * @return {@link Integer} or null
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为{@link Integer}
     * @throws TypeCastException         类型不支持转化为{@link Integer}
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public Integer getInteger(int index) {
        Object value = get(index);

        return DataCast.castInteger(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的 int
     *
     * @param index 元素下标
     * @return int
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为 int
     * @throws TypeCastException         类型不支持转化为 int
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public int getIntValue(int index) {
        Object value = get(index);

        return DataCast.castIntValue(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link Short}
     *
     * @param index 元素下标
     * @return {@link Short} or null
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为{@link Short}
     * @throws TypeCastException         类型不支持转化为{@link Short}
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public Short getShort(int index) {
        Object value = get(index);

        return DataCast.castShort(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的 short
     *
     * @param index 元素下标
     * @return short
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为 short
     * @throws TypeCastException         类型不支持转化为 short
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public short getShortValue(int index) {
        Object value = get(index);

        return DataCast.castShortValue(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link Byte}
     *
     * @param index 元素下标
     * @return {@link Byte} or null
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为{@link Byte}
     * @throws TypeCastException         类型不支持转化为{@link Byte}
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public Byte getByte(int index) {
        Object value = get(index);

        return DataCast.castByte(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的 byte
     *
     * @param index 元素下标
     * @return byte
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为 byte
     * @throws TypeCastException         类型不支持转化为 byte
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public byte getByteValue(int index) {
        Object value = get(index);

        return DataCast.castByteValue(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link Boolean}
     *
     * @param index 元素下标
     * @return {@link Boolean} or null
     * @throws TypeCastException         类型不支持转化为{@link Boolean}
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public Boolean getBoolean(int index) {
        Object value = get(index);

        return DataCast.castBoolean(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的 boolean
     *
     * @param index 元素下标
     * @return boolean
     * @throws TypeCastException         类型不支持转化为 boolean
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public boolean getBooleanValue(int index) {
        Object value = get(index);

        return DataCast.castBooleanValue(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link BigInteger}
     *
     * @param index 元素下标
     * @return {@link BigInteger} or null
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为{@link BigInteger}
     * @throws TypeCastException         类型不支持转化为{@link BigInteger}
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public BigInteger getBigInteger(int index) {
        Object value = get(index);

        return DataCast.castBigInteger(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link BigDecimal}
     *
     * @param index 元素下标
     * @return {@link BigDecimal} or null
     * @throws NumberFormatException     如果映射的值为{@link String}并且它不可解析为{@link BigDecimal}
     * @throws TypeCastException         类型不支持转化为{@link BigDecimal}
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     */
    public BigDecimal getBigDecimal(int index) {
        Object value = get(index);

        return DataCast.castBigDecimal(value);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link Date}
     *
     * @param index 元素下标
     * @return {@link Date} or null
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     * @throws TypeCastException         类型不支持转化为{@link Date}
     */
    public Date getDate(int index) {
        Object value = get(index);

        return DataCast.castDate(value, this.codecGenerate);
    }

    /**
     * 返回此{@link DataArray}中指定位置的{@link Instant}
     *
     * @param index 元素下标
     * @return {@link Instant} or null
     * @throws IndexOutOfBoundsException 如果索引超出范围{@code (index < 0 || index >= size())}
     * @throws TypeCastException         类型不支持转化为{@link Instant}
     */
    public Instant getInstant(int index) {
        Object value = get(index);

        return DataCast.castInstant(value, this.codecGenerate);
    }

    /**
     * 将此 {@link DataArray} 的所有成员转换为指定的{@link Type}
     * <p>
     * {@code List<User> users = array.toJavaList(User.class);}
     *
     * @param typeToken 指定要转换的 {@link TypeToken}
     * @return List<T>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> List<T> toJavaList(TypeToken<T> typeToken) {
        return toJavaList(typeToken.getType());
    }

    /**
     * 将此 {@link DataArray} 的所有成员转换为指定的{@link Type}
     * <p>
     * {@code List<User> users = array.toJavaList(User.class);}
     *
     * @param type 指定要转换的 {@link Type}
     * @return List<T>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> List<T> toJavaList(Type type) {
        List<T> list = ListUtil.toList();
        for (int i = 0; i < this.size(); i++) {
            T classItem = (T) getValue(i, type);
            list.add(classItem);
        }
        return list;
    }

    /**
     * 返回此{@link DataMapper}中指定位置的元素的{@link Type}
     * <p>
     * {@code User user = array.getObject(0, User.class);}
     *
     * @param index 元素下标
     * @param type  指定要转换的{@link Type}
     * @return {@code <T>} or null
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T getObject(int index, Type type) {
        return (T) getValue(index, type);
    }

    private Object getValue(int index, Type type) {
        Object value = get(index);

        return DataCast.castType(value, TypeToken.get(type), this.converter, this.codecGenerate);
    }

    /**
     * 元素的链式添加
     *
     * <pre>
     * DataArray array = new DataArray(converter).fluentAdd(1).fluentAdd(2).fluentAdd(3);
     * </pre>
     *
     * @param element 要附加到此列表的元素
     */
    public DataArray fluentAdd(Object element) {
        add(element);
        return this;
    }

    /**
     * 将多个元素打包为{@link DataArray}
     *
     * <pre>
     * DataArray array = DataArray.of(converter, 1, 2, "3", 4F, 5L, 6D, true);
     * </pre>
     *
     * @param items 元素集
     */
    public static DataArray of(Converter<?> converter, Object... items) {
        return new DataArray(converter, items);
    }

    /**
     * 将元素打包为{@link DataArray}
     *
     * <pre>
     * DataArray array = DataArray.of(converter, "dataArray");
     * </pre>
     *
     * @param item 目标元素
     */
    public static DataArray of(Converter<?> converter, Object item) {
        DataArray dataArray = new DataArray(converter);
        dataArray.add(item);
        return dataArray;
    }

    /**
     * 将两个元素打包为{@link DataArray}
     *
     * <pre>
     * DataArray array = DataArray.of(converter, "dataArray", 2);
     * </pre>
     *
     * @param first  第一个元素
     * @param second 第二个元素
     */
    public static DataArray of(Converter<?> converter, Object first, Object second) {
        DataArray dataArray = new DataArray(converter);
        dataArray.add(first);
        dataArray.add(second);
        return dataArray;
    }

    /**
     * 将三个元素打包为{@link DataArray}
     *
     * <pre>
     * DataArray array = DataArray.of(converter, "dataArray", 2, true);
     * </pre>
     *
     * @param first  第一个元素
     * @param second 第二个元素
     * @param third  第三个元素
     */
    public static DataArray of(Converter<?> converter, Object first, Object second, Object third) {
        DataArray dataArray = new DataArray(converter);
        dataArray.add(first);
        dataArray.add(second);
        dataArray.add(third);
        return dataArray;
    }

    /**
     * 将元素集打包为{@link DataArray}
     *
     * <pre>
     * DataArray array = DataArray.of(converter, collection);
     * </pre>
     *
     * @param collection 元素集
     */
    public static DataArray of(Converter<?> converter, Collection<?> collection) {
        return new DataArray(converter, collection);
    }
}
