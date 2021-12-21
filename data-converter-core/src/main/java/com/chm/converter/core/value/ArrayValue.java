package com.chm.converter.core.value;

import java.util.Iterator;
import java.util.List;

/**
 * 数组类型
 *
 * @author caihongming
 * @version v1.0
 * @see Value
 * @see Iterable<Value>
 * @since 2021-12-04
 **/
public interface ArrayValue extends Value, Iterable<Value> {

    /**
     * 返回数组大小
     *
     * @return
     */
    int size();

    /**
     * 返回数组中指定位置元素
     *
     * @param index
     * @return
     */
    Value get(int index);

    /**
     * 返回此数组中指定位置的元素。如果索引超出范围，此方法将返回一个 ImmutableNullValue。
     *
     * @param index
     * @return
     */
    Value getOrNullValue(int index);

    /**
     * 返回该数组的迭代器
     *
     * @return
     */
    @Override
    Iterator<Value> iterator();

    /**
     * 将该数组转为List返回
     *
     * @return
     */
    List<Value> list();
}
