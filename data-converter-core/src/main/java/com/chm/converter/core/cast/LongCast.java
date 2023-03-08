package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-06-09
 **/
public class LongCast implements TypeCast<Long> {

    public final static LongCast INSTANCE = new LongCast();

    /**
     * 将指定对象转化为{@link Long}
     *
     * @param value 指定对象
     * @return {@link Long} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Long}
     * @throws TypeCastException     类型不支持转化为{@link Long}
     */
    @Override
    public Long cast(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Long) {
            return ((Long) value);
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }

            return Long.parseLong(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to Long");
    }
}
