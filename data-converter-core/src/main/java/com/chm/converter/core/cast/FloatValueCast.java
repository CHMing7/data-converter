package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-06-09
 **/
public class FloatValueCast implements TypeCast<Float> {

    public final static FloatValueCast INSTANCE = new FloatValueCast();

    /**
     * 将指定对象转化为 float
     *
     * @param value 指定对象
     * @return float
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 float
     * @throws TypeCastException     类型不支持转化为 float
     */
    @Override
    public Float cast(Object value) {
        if (value == null) {
            return 0F;
        }

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return 0F;
            }

            return Float.parseFloat(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to float value");
    }
}
