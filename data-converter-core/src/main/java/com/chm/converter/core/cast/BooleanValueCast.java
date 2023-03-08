package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-06-10
 **/
public class BooleanValueCast implements TypeCast<Boolean> {

    public final static BooleanValueCast INSTANCE = new BooleanValueCast();

    /**
     * 将指定对象转化为 boolean
     *
     * @param value 指定对象
     * @return boolean
     * @throws TypeCastException 类型不支持转化为 boolean
     */
    @Override
    public Boolean cast(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }

        if (value instanceof CharSequence) {
            String str = value.toString();
            return TRUE.equalsIgnoreCase(str) || NUMBER_1.equals(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to boolean value");
    }
}
