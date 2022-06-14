package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-06-10
 **/
public class BigDecimalCast implements TypeCast<BigDecimal> {

    public final static BigDecimalCast INSTANCE = new BigDecimalCast();

    /**
     * 将指定对象转化为{@link BigDecimal}
     *
     * @param value 指定对象
     * @return {@link BigDecimal} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link BigDecimal}
     * @throws TypeCastException     类型不支持转化为{@link BigDecimal}
     */
    @Override
    public BigDecimal cast(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            }

            if (value instanceof BigInteger) {
                return new BigDecimal((BigInteger) value);
            }

            if (value instanceof Float
                    || value instanceof Double) {
                // Floating point number have no cached BigDecimal
                return new BigDecimal(value.toString());
            }

            long longValue = ((Number) value).longValue();
            return BigDecimal.valueOf(longValue);
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }

            return new BigDecimal(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to BigDecimal");
    }
}
