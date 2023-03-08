package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-06-09
 **/
public class IntegerCast implements TypeCast<Integer> {

    public final static IntegerCast INSTANCE = new IntegerCast();

    /**
     * 将指定对象转化为{@link Integer}
     *
     * @param value 指定对象
     * @return {@link Integer} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Integer}
     * @throws TypeCastException     类型不支持转化为{@link Integer}
     */
    @Override
    public Integer cast(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return ((Integer) value);
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }

            return Integer.parseInt(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to Integer");
    }
}
