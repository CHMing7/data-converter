package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-06-09
 **/
public class FloatCast implements TypeCast<Float> {

    public final static FloatCast INSTANCE = new FloatCast();

    /**
     * 将指定对象转化为{@link Float}
     *
     * @param value 指定对象
     * @return {@link Float} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Float}
     * @throws TypeCastException     类型不支持转化为{@link Float}
     */
    @Override
    public Float cast(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Float) {
            return (Float) value;
        }

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }

            return Float.parseFloat(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to Float");
    }
}
