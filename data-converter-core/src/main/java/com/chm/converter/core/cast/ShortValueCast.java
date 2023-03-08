package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-06-10
 **/
public class ShortValueCast implements TypeCast<Short> {

    public final static ShortValueCast INSTANCE = new ShortValueCast();

    /**
     * 将指定对象转化为 short
     *
     * @param value 指定对象
     * @return short
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 short
     * @throws TypeCastException     类型不支持转化为 short
     */
    @Override
    public Short cast(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return 0;
            }

            return Short.parseShort(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to short value");
    }
}
