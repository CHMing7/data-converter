package com.chm.converter.core.value;

/**
 * 不可变类型
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-12-04
 **/
public interface ImmutableValue extends Value {

    /**
     * 转为{@code ImmutableNullValue}类型值返回
     *
     * @return
     */
    @Override
    ImmutableNullValue asNullValue();

    /**
     * 转为{@code ImmutableBooleanValue}类型值返回
     *
     * @return
     */
    @Override
    ImmutableBooleanValue asBooleanValue();

    /**
     * 转为{@code ImmutableNumberValue}类型值返回
     *
     * @return
     */
    @Override
    ImmutableNumberValue asNumberValue();

    /**
     * 转为{@code ImmutableIntegerValue}类型值返回
     *
     * @return
     */
    @Override
    ImmutableIntegerValue asIntegerValue();

    /**
     * 转为{@code ImmutableFloatValue}类型值返回
     *
     * @return
     */
    @Override
    ImmutableFloatValue asFloatValue();

    /**
     * 转为{@code ImmutableBinaryValue}类型值返回
     *
     * @return
     */
    @Override
    ImmutableBinaryValue asBinaryValue();

    /**
     * 转为{@code ImmutableStringValue}类型值返回
     *
     * @return
     */
    @Override
    ImmutableStringValue asStringValue();

    /**
     * 转为{@code ImmutableArrayValue}类型值返回
     *
     * @return
     */
    @Override
    ImmutableArrayValue asArrayValue();

    /**
     * 转为{@code ImmutableMapValue}类型值返回
     *
     * @return
     */
    @Override
    ImmutableMapValue asMapValue();

    /**
     * 转为{@code ImmutableCollectionValue}类型值返回
     *
     * @return
     */
    @Override
    ImmutableCollectionValue asCollectionValue();

    /**
     * 转为{@code ImmutableEnumValue}类型值返回
     *
     * @return
     */
    @Override
    ImmutableEnumValue asEnumValue();

    /**
     * 转为{@code ImmutableClassValue}类型值返回
     *
     * @return
     */
    @Override
    ImmutableClassValue asClassValue();
}
