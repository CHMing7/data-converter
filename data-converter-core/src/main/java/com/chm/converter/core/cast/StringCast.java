package com.chm.converter.core.cast;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-06-09
 **/
public class StringCast implements TypeCast<String> {

    public final static StringCast INSTANCE = new StringCast();

    /**
     * 将指定对象转化为 {@link String}
     *
     * @param value 指定对象
     * @return {@link String} or null
     */
    @Override
    public String cast(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof CharSequence) {
            return value.toString();
        }

        return null;
    }
}
