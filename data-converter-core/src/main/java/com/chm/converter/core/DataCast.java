package com.chm.converter.core;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.exception.TypeCastException;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.TypeUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-05-30
 **/
public interface DataCast {

    String NULL = "null";

    String TRUE = "true";

    String NUMBER_1 = "1";

    /**
     * 将指定对象转化为 {@link DataArray}
     *
     * @param value     指定对象
     * @param converter
     * @return {@link DataArray} or null
     */
    static DataArray castArray(Object value, Converter<?> converter) {
        if (value instanceof DataArray) {
            return (DataArray) value;
        }

        if (value instanceof Collection) {
            return new DataArray(converter, (Collection<?>) value);
        }

        return null;
    }

    /**
     * 将指定对象转化为 {@link DataMapper}
     *
     * @param value     指定对象
     * @param converter
     * @return {@link DataMapper} or null
     */
    static DataMapper castMapper(Object value, Converter<?> converter) {
        if (value instanceof DataMapper) {
            return (DataMapper) value;
        }

        if (value instanceof Map) {
            return new DataMapper(converter, (Map) value);
        }

        return null;
    }

    /**
     * 将指定对象转化为 {@link String}
     *
     * @param value 指定对象
     * @return {@link String} or null
     */
    static String castString(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof CharSequence) {
            return value.toString();
        }

        return null;
    }

    /**
     * 将指定对象转化为{@link Double}
     *
     * @param value 指定对象
     * @return {@link Double} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Double}
     * @throws TypeCastException     类型不支持转化为{@link Double}
     */
    static Double castDouble(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Double) {
            return (Double) value;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }

            return Double.parseDouble(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to Double");
    }

    /**
     * 将指定对象转化为 double
     *
     * @param value 指定对象
     * @return double
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 double
     * @throws TypeCastException     类型不支持转化为 double
     */
    static double castDoubleValue(Object value) {
        if (value == null) {
            return 0D;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return 0D;
            }

            return Double.parseDouble(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to double value");
    }

    /**
     * 将指定对象转化为{@link Float}
     *
     * @param value 指定对象
     * @return {@link Float} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Float}
     * @throws TypeCastException     类型不支持转化为{@link Float}
     */
    static Float castFloat(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Float) {
            return (Float) value;
        }

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }

            return Float.parseFloat(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to Float");
    }

    /**
     * 将指定对象转化为 float
     *
     * @param value 指定对象
     * @return float
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 float
     * @throws TypeCastException     类型不支持转化为 float
     */
    static float castFloatValue(Object value) {
        if (value == null) {
            return 0F;
        }

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return 0F;
            }

            return Float.parseFloat(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to float value");
    }

    /**
     * 将指定对象转化为{@link Long}
     *
     * @param value 指定对象
     * @return {@link Long} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Long}
     * @throws TypeCastException     类型不支持转化为{@link Long}
     */
    static Long castLong(Object value) {
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

    /**
     * 将指定对象转化为 long
     *
     * @param value 指定对象
     * @return long
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 long
     * @throws TypeCastException     类型不支持转化为 long
     */
    static long castLongValue(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return 0;
            }

            return Long.parseLong(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to long value");
    }

    /**
     * 将指定对象转化为{@link Integer}
     *
     * @param value 指定对象
     * @return {@link Integer} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Integer}
     * @throws TypeCastException     类型不支持转化为{@link Integer}
     */
    static Integer castInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return ((Integer) value);
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }

            return Integer.parseInt(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to Integer");
    }

    /**
     * 将指定对象转化为 int
     *
     * @param value 指定对象
     * @return int
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 int
     * @throws TypeCastException     类型不支持转化为 int
     */
    static int castIntValue(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return 0;
            }

            return Integer.parseInt(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to int value");
    }

    /**
     * 将指定对象转化为{@link Short}
     *
     * @param value 指定对象
     * @return {@link Short} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Short}
     * @throws TypeCastException     类型不支持转化为{@link Short}
     */
    static Short castShort(Object value) {
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

    /**
     * 将指定对象转化为 short
     *
     * @param value 指定对象
     * @return short
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 short
     * @throws TypeCastException     类型不支持转化为 short
     */
    static short castShortValue(Object value) {
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

    /**
     * 将指定对象转化为{@link Byte}
     *
     * @param value 指定对象
     * @return {@link Byte} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Byte}
     * @throws TypeCastException     类型不支持转化为{@link Byte}
     */
    static Byte castByte(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }

            return Byte.parseByte(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to Byte");
    }

    /**
     * 将指定对象转化为 byte
     *
     * @param value 指定对象
     * @return byte
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 byte
     * @throws TypeCastException     类型不支持转化为 byte
     */
    static byte castByteValue(Object value) {
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

    /**
     * 将指定对象转化为{@link Boolean}
     *
     * @param value 指定对象
     * @return {@link Boolean} or null
     * @throws TypeCastException 类型不支持转化为{@link Boolean}
     */
    static Boolean castBoolean(Object value) {
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

    /**
     * 将指定对象转化为 boolean
     *
     * @param value 指定对象
     * @return boolean
     * @throws TypeCastException 类型不支持转化为 boolean
     */
    static boolean castBooleanValue(Object value) {
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

    /**
     * 将指定对象转化为{@link BigInteger}
     *
     * @param value 指定对象
     * @return {@link BigInteger} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link BigInteger}
     * @throws TypeCastException     类型不支持转化为{@link BigInteger}
     */
    static BigInteger castBigInteger(Object value) {
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

    /**
     * 将指定对象转化为{@link BigDecimal}
     *
     * @param value 指定对象
     * @return {@link BigDecimal} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link BigDecimal}
     * @throws TypeCastException     类型不支持转化为{@link BigDecimal}
     */
    static BigDecimal castBigDecimal(Object value) {
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

    /**
     * 将指定对象转化为{@link Date}
     *
     * @param value    指定对象
     * @param generate
     * @return {@link Date} or null
     * @throws TypeCastException 类型不支持转化为{@link Date}
     */
    static Date castDate(Object value, DataCodecGenerate generate) {
        if (value == null) {
            return null;
        }

        if (value instanceof Date) {
            return (Date) value;
        }

        if (value instanceof Number) {
            long millis = ((Number) value).longValue();
            if (millis == 0) {
                return null;
            }
            return new Date(millis);
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }
            Codec<Date, String> codec = generate.get(Date.class);
            return codec.decode(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to Date");
    }

    /**
     * 将指定对象转化为{@link Instant}
     *
     * @param value    指定对象
     * @param generate
     * @return {@link Instant} or null
     * @throws TypeCastException 类型不支持转化为{@link Instant}
     */
    static Instant castInstant(Object value, DataCodecGenerate generate) {
        if (value == null) {
            return null;
        }

        if (value instanceof Instant) {
            return (Instant) value;
        }

        if (value instanceof Number) {
            long millis = ((Number) value).longValue();
            if (millis == 0) {
                return null;
            }
            return Instant.ofEpochMilli(millis);
        }

        if (value instanceof CharSequence) {
            String str = value.toString();

            if (str.isEmpty() || NULL.equalsIgnoreCase(str)) {
                return null;
            }
            Codec<Instant, String> codec = generate.get(Instant.class);
            return codec.decode(str);
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to Instant");
    }

    /**
     * 将指定对象转化为指定的{@link TypeToken}
     *
     * @param value     指定对象
     * @param typeToken 指定{@link TypeToken}
     * @return T
     */
    static <T> T castType(Object value, TypeToken<T> typeToken, Converter<?> converter, DataCodecGenerate generate) {
        Type type = typeToken.getType();
        if (type == int.class || type == Integer.class) {
            return (T) castInteger(value);
        }
        if (type == long.class || type == Long.class) {
            return (T) castLong(value);
        }
        if (type == float.class || type == Float.class) {
            return (T) castFloat(value);
        }
        if (type == double.class || type == Double.class) {
            return (T) castDouble(value);
        }
        if (type == boolean.class || type == Boolean.class) {
            return (T) castBoolean(value);
        }
        if (type == String.class) {
            return (T) castString(value);
        }
        if (type == BigDecimal.class) {
            return (T) castBigDecimal(value);
        }
        if (type == BigInteger.class) {
            return (T) castBigInteger(value);
        }
        if (type == Date.class) {
            return (T) castDate(value, generate);
        }
        if (type == Instant.class) {
            return (T) castInstant(value, generate);
        }
        Class<?> clazz = TypeUtil.getClass(type);
        if (List.class.isAssignableFrom(clazz)) {
            DataArray dataArray = castArray(value, converter);
            if (dataArray != null) {
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < dataArray.size(); i++) {
                    DataMapper dataMapper = dataArray.getMapper(i);
                    list.add(dataMapper.toJavaBean(((ParameterizedType) type).getActualTypeArguments()[0]));
                }
                return (T) list;
            }
        } else {
            DataMapper dataMapper = castMapper(value, converter);
            if (dataMapper != null) {
                return dataMapper.toJavaBean(type);
            }
        }
        if (!typeToken.getRawType().isInstance(value)) {
            Codec codec = generate.get(typeToken.getRawType());
            if (codec != null) {
                return (T) codec.decode(value);
            }
        }
        return null;
    }
}
