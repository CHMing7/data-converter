package com.chm.converter.core.value;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Map 类型
 *
 * @author caihongming
 * @version v1.0
 * @see Value
 * @since 2021-12-04
 */
public interface MapValue extends Value {

    /**
     * 返回{@code Map}大小
     *
     * @return
     */
    int size();

    /**
     * 返回{@code key}集合
     *
     * @return
     */
    Set<Value> keySet();

    /**
     * 返回Entry集合
     *
     * @return
     */
    Set<Map.Entry<Value, Value>> entrySet();

    /**
     * 返回{@code value}值集合
     *
     * @return
     */
    Collection<Value> values();

    /**
     * 以 {@code Map} 的形式返回
     *
     * @return
     */
    Map<Value, Value> map();

    /**
     * 以 {@code Value} 数组的形式返回键值对
     *
     * @return
     */
    Value[] getKeyValueArray();
}
