package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-06-09
 **/
public class IntValueCast implements TypeCast<Integer> {

    public final static IntValueCast INSTANCE = new IntValueCast();

    /**
     * 将指定对象转化为 int
     *
     * @param value 指定对象
     * @return int
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 int
     * @throws TypeCastException     类型不支持转化为 int
     */
    @Override
    public Integer cast(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return 0;
            }

            return Integer.parseInt(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to int value");
    }
}
