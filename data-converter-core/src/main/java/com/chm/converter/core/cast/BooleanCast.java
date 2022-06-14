package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-06-10
 **/
public class BooleanCast implements TypeCast<Boolean> {

    public final static BooleanCast INSTANCE = new BooleanCast();

    /**
     * 将指定对象转化为{@link Boolean}
     *
     * @param value 指定对象
     * @return {@link Boolean} or null
     * @throws TypeCastException 类型不支持转化为{@link Boolean}
     */
    @Override
    public Boolean cast(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }

            return TRUE.equalsIgnoreCase(str) || NUMBER_1.equals(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to Boolean");
    }
}
