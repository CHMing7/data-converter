package com.chm.converter.core.value;

import com.chm.converter.core.exception.TypeCastException;
import com.chm.converter.core.pack.DataPacker;

import java.io.IOException;

/**
 * Value 在系统中存储一个值及其类型。
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-12-04
 **/
public interface Value {

    /**
     * 返回此值的类型
     *
     * @return {@link ValueType}
     */
    ValueType getValueType();

    /**
     * 返回此值的不可变副本
     *
     * @return {@link ImmutableValue}
     */
    ImmutableValue immutableValue();

    /**
     * 如果此值的类型为 null，则返回 true
     *
     * @return {@link Boolean}
     */
    boolean isNullValue();

    /**
     * 如果此值的类型为Boolean，则返回 true
     *
     * @return {@link Boolean}
     */
    boolean isBooleanValue();

    /**
     * 如果此值的类型为 Integer 或 Float，则返回 true
     *
     * @return {@link Boolean}
     */
    boolean isNumberValue();

    /**
     * 如果此值的类型为 Integer，则返回 true
     *
     * @return {@link Boolean}
     */
    boolean isIntegerValue();

    /**
     * 如果此值的类型为 Float，则返回 true
     *
     * @return {@link Boolean}
     */
    boolean isFloatValue();

    /**
     * 如果此值的类型为 Binary，则返回 true
     *
     * @return {@link Boolean}
     */
    boolean isBinaryValue();

    /**
     * 如果此值的类型为 String，则返回 true
     *
     * @return {@link Boolean}
     */
    boolean isStringValue();

    /**
     * 如果此值的类型为 Array，则返回 true
     *
     * @return {@link Boolean}
     */
    boolean isArrayValue();

    /**
     * 如果此值的类型为 Map，则返回 true
     *
     * @return
     */
    boolean isMapValue();

    /**
     * 如果此值的类型为 Collection，则返回 true
     *
     * @return
     */
    boolean isCollectionValue();

    /**
     * 如果此值的类型为 Enum，则返回 true
     *
     * @return
     */
    boolean isEnumValue();

    /**
     * 如果此值的类型为 Class，则返回 true
     *
     * @return
     */
    boolean isClassValue();

    /**
     * 如果类型为Extension，则返回 true
     *
     * @return {@link Boolean}
     */
    boolean isExtensionValue();

    /**
     * 转为{@code NullValue}类型返回，否则抛出 {@code TypeCastException}。
     *
     * @return
     * @throws TypeCastException 如果此值的类型不是{@code Null}，则抛出该异常
     */
    NullValue asNullValue();

    /**
     * 转为{@code BooleanValue}类型值返回，否则抛出 {@code TypeCastException}。
     *
     * @return
     * @throws TypeCastException 如果此值的类型不是{@code BooleanValue}，则抛出该异常
     */
    BooleanValue asBooleanValue();

    /**
     * 转为{@code NumberValue}类型值返回,否则抛出 {@code TypeCastException}。
     *
     * @return
     * @throws TypeCastException 如果此值的类型不是{@code NumberValue}，则抛出该异常
     */
    NumberValue asNumberValue();

    /**
     * 转为{@code IntegerValue}类型值返回,否则抛出 {@code TypeCastException}。
     *
     * @return
     * @throws TypeCastException 如果此值的类型不是{@code IntegerValue}，则抛出该异常
     */
    IntegerValue asIntegerValue();

    /**
     * 转为{@code FloatValue}类型值返回,否则抛出 {@code TypeCastException}。
     *
     * @return
     * @throws TypeCastException 如果此值的类型不是{@code FloatValue}，则抛出该异常
     */
    FloatValue asFloatValue();

    /**
     * 转为{@code BinaryValue}类型值返回,否则抛出 {@code TypeCastException}。
     *
     * @return
     * @throws TypeCastException 如果此值的类型不是{@code BinaryValue}，则抛出该异常
     */
    BinaryValue asBinaryValue();

    /**
     * 转为{@code StringValue}类型值返回,否则抛出 {@code TypeCastException}。
     *
     * @return
     * @throws TypeCastException 如果此值的类型不是{@code StringValue}，则抛出该异常
     */
    StringValue asStringValue();

    /**
     * 转为{@code ArrayValue}类型值返回,否则抛出 {@code TypeCastException}。
     *
     * @return
     * @throws TypeCastException 如果此值的类型不是{@code ArrayValue}，则抛出该异常
     */
    ArrayValue asArrayValue();

    /**
     * 转为{@code MapValue}类型值返回,否则抛出 {@code TypeCastException}。
     *
     * @return
     * @throws TypeCastException 如果此值的类型不是{@code MapValue}，则抛出该异常
     */
    MapValue asMapValue();

    /**
     * 转为{@code CollectionValue}类型值返回,否则抛出 {@code TypeCastException}。
     *
     * @return
     * @throws TypeCastException 如果此值的类型不是{@code CollectionValue}，则抛出该异常
     */
    CollectionValue asCollectionValue();

    /**
     * 转为{@code EnumValue}类型值返回,否则抛出 {@code TypeCastException}。
     *
     * @return
     * @throws TypeCastException 如果此值的类型不是{@code EnumValue}，则抛出该异常
     */
    EnumValue asEnumValue();

    /**
     * 转为{@code ClassValue}类型值返回,否则抛出 {@code TypeCastException}。
     *
     * @return
     * @throws TypeCastException 如果此值的类型不是{@code ClassValue}，则抛出该异常
     */
    ClassValue asClassValue();

    /**
     * 使用指定的 {@code DataPacker} 序列化值
     *
     * @param pk
     * @throws IOException
     * @see DataPacker
     */
    void writeTo(DataPacker pk) throws IOException;

    /**
     * 将此值与指定的对象进行比较。
     * 如果类型和值相等，则此方法返回 {@code true}
     *
     * @param obj
     * @return
     */
    @Override
    boolean equals(Object obj);

    /**
     * 返回此值的 json 表示
     *
     * @return
     */
    String toJson();
}
