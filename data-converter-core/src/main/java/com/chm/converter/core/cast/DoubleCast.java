package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-06-09
 **/
public class DoubleCast implements TypeCast<Double> {

    public final static DoubleCast INSTANCE = new DoubleCast();

    /**
     * 将指定对象转化为{@link Double}
     *
     * @param value 指定对象
     * @return {@link Double} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Double}
     * @throws TypeCastException     类型不支持转化为{@link Double}
     */
    @Override
    public Double cast(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Double) {
            return (Double) value;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }

            return Double.parseDouble(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to Double");
    }
}
