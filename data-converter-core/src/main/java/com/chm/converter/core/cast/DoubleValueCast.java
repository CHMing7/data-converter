package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-06-09
 **/
public class DoubleValueCast implements TypeCast<Double> {

    public final static DoubleValueCast INSTANCE = new DoubleValueCast();

    /**
     * 将指定对象转化为 double
     *
     * @param value 指定对象
     * @return double
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 double
     * @throws TypeCastException     类型不支持转化为 double
     */
    @Override
    public Double cast(Object value) {
        if (value == null) {
            return 0D;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return 0D;
            }

            return Double.parseDouble(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to double value");
    }
}
