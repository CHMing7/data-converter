package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-06-10
 **/
public class ByteValueCast implements TypeCast<Byte> {

    public final static ByteValueCast INSTANCE = new ByteValueCast();

    /**
     * 将指定对象转化为 byte
     *
     * @param value 指定对象
     * @return byte
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 byte
     * @throws TypeCastException     类型不支持转化为 byte
     */
    @Override
    public Byte cast(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return 0;
            }

            return Byte.parseByte(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to byte value");
    }
}
