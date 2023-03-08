package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-06-10
 **/
public class ShortCast implements TypeCast<Short> {

    public final static ShortCast INSTANCE = new ShortCast();

    /**
     * 将指定对象转化为{@link Short}
     *
     * @param value 指定对象
     * @return {@link Short} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Short}
     * @throws TypeCastException     类型不支持转化为{@link Short}
     */
    @Override
    public Short cast(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Short) {
            return (Short) value;
        }

        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }

            return Short.parseShort(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to Short");
    }
}
