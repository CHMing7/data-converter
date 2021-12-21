package com.chm.converter.core.value;

import java.util.Iterator;
import java.util.List;

/**
 * 数组类型的不可变表示
 *
 * @author caihongming
 * @version v1.0
 * @see ArrayValue
 * @see ImmutableValue
 * @since 2021-12-04
 **/
public interface ImmutableArrayValue extends ArrayValue, ImmutableValue {

    /**
     * 返回元素上的迭代器。返回的迭代器不支持 {@link java.util.List#remove(Object)} 方法，因为该值是不可变的。
     *
     * @return
     */
    @Override
    Iterator<Value> iterator();

    /**
     * 以 {@link List} 形式返回值。返回的列表是不可变的。它不支持 {@link java.util.List#add(Object)}、{@link List#clear()} 或其他修改值的方法。
     *
     * @return
     */
    @Override
    List<Value> list();
}
