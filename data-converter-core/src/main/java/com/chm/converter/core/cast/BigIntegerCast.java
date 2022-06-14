package com.chm.converter.core.cast;

import com.chm.converter.core.exception.TypeCastException;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-06-10
 **/
public class BigIntegerCast implements TypeCast<BigInteger> {

    public final static BigIntegerCast INSTANCE = new BigIntegerCast();

    /**
     * 将指定对象转化为{@link BigInteger}
     *
     * @param value 指定对象
     * @return {@link BigInteger} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link BigInteger}
     * @throws TypeCastException     类型不支持转化为{@link BigInteger}
     */
    @Override
    public BigInteger cast(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            if (value instanceof BigInteger) {
                return (BigInteger) value;
            }

            if (value instanceof BigDecimal) {
                return ((BigDecimal) value).toBigInteger();
            }

            long longValue = ((Number) value).longValue();
            return BigInteger.valueOf(longValue);
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }

            return new BigInteger(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to BigInteger");
    }
}
