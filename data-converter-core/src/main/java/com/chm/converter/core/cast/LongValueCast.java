package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-06-09
 **/
public class LongValueCast implements TypeCast<Long> {

    public final static LongValueCast INSTANCE = new LongValueCast();

    /**
     * 将指定对象转化为 long
     *
     * @param value 指定对象
     * @return long
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 long
     * @throws TypeCastException     类型不支持转化为 long
     */
    @Override
    public Long cast(Object value) {
        if (value == null) {
            return 0L;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return 0L;
            }

            return Long.parseLong(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to long value");
    }
}
