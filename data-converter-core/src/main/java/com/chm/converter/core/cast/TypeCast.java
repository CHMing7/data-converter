package com.chm.converter.core.cast;

import com.chm.converter.core.utils.ArrayUtil;
import com.chm.converter.core.utils.CharUtil;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-06-09
 **/
@FunctionalInterface
public interface TypeCast<T> {

    String NULL = "null";

    String TRUE = "true";

    String NUMBER_1 = "1";

    /**
     * 将指定对象转化为{@link T}
     *
     * @param value 指定对象
     * @return {@link T} or null
     */
    T cast(Object value);


    /**
     * 值转为String，用于内部转换中需要使用String中转的情况<br>
     * 转换规则为：
     *
     * <pre>
     * 1、字符串类型将被强转
     * 2、数组将被转换为逗号分隔的字符串
     * 3、其它类型将调用默认的toString()方法
     * </pre>
     *
     * @param value 值
     * @return String
     */
    default String convertToStr(Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof CharSequence) {
            return value.toString();
        } else if (ArrayUtil.isArray(value)) {
            return ArrayUtil.toString(value);
        } else if (CharUtil.isChar(value)) {
            //对于ASCII字符使用缓存加速转换，减少空间创建
            return CharUtil.toString((char) value);
        }
        return value.toString();
    }
}
