package com.chm.converter.core;

import com.chm.converter.core.cast.BigDecimalCast;
import com.chm.converter.core.cast.BigIntegerCast;
import com.chm.converter.core.cast.BooleanCast;
import com.chm.converter.core.cast.BooleanValueCast;
import com.chm.converter.core.cast.ByteCast;
import com.chm.converter.core.cast.ByteValueCast;
import com.chm.converter.core.cast.FloatCast;
import com.chm.converter.core.cast.FloatValueCast;
import com.chm.converter.core.cast.IntValueCast;
import com.chm.converter.core.cast.IntegerCast;
import com.chm.converter.core.cast.LongCast;
import com.chm.converter.core.cast.LongValueCast;
import com.chm.converter.core.cast.ShortCast;
import com.chm.converter.core.cast.ShortValueCast;
import com.chm.converter.core.cast.StringCast;
import com.chm.converter.core.cast.TypeCast;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.exception.TypeCastException;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.MapUtil;

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
public abstract class DataCast {

    private static final Map<Type, TypeCast<?>> CLASS_TYPE_CAST_MAP = MapUtil.newConcurrentHashMap();

    private static final String NULL = "null";

    static {
        register(Object.class, value -> value);
        register(Boolean.class, BooleanCast.INSTANCE);
        register(boolean.class, BooleanValueCast.INSTANCE);
        register(Byte.class, ByteCast.INSTANCE);
        register(byte.class, ByteValueCast.INSTANCE);
        register(Short.class, ShortCast.INSTANCE);
        register(short.class, ShortValueCast.INSTANCE);
        register(Integer.class, IntegerCast.INSTANCE);
        register(int.class, IntValueCast.INSTANCE);
        register(Float.class, FloatCast.INSTANCE);
        register(float.class, FloatValueCast.INSTANCE);
        register(Double.class, ByteValueCast.INSTANCE);
        register(double.class, ByteValueCast.INSTANCE);
        register(Long.class, LongCast.INSTANCE);
        register(long.class, LongValueCast.INSTANCE);
        register(BigInteger.class, BigIntegerCast.INSTANCE);
        register(BigDecimal.class, BigDecimalCast.INSTANCE);
        register(String.class, StringCast.INSTANCE);
    }

    public static <T> void register(Type type, TypeCast<T> typeCast) {
        CLASS_TYPE_CAST_MAP.put(type, typeCast);
    }

    /**
     * ???????????????????????? {@link DataArray}
     *
     * @param value     ????????????
     * @param converter
     * @return {@link DataArray} or null
     */
    public static DataArray castArray(Object value, Converter<?> converter) {
        if (value instanceof DataArray) {
            return (DataArray) value;
        }

        if (value instanceof Collection) {
            return DataArray.of(converter, (Collection<?>) value);
        }

        if (value.getClass().isArray()) {
            return DataArray.of(converter, (Object[]) value);
        }

        return null;
    }

    /**
     * ???????????????????????? {@link DataMapper}
     *
     * @param value     ????????????
     * @param converter
     * @return {@link DataMapper} or null
     */
    public static DataMapper castMapper(Object value, Converter<?> converter) {
        if (value instanceof DataMapper) {
            return (DataMapper) value;
        }

        if (value instanceof Map) {
            return DataMapper.of(converter, (Map) value);
        }

        return null;
    }

    /**
     * ???????????????????????? {@link String}
     *
     * @param value ????????????
     * @return {@link String} or null
     */
    public static String castString(Object value) {
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(String.class);
        if (typeCast != null) {
            return (String) typeCast.cast(value);
        }
        return null;
    }

    /**
     * ????????????????????????{@link Double}
     *
     * @param value ????????????
     * @return {@link Double} or null
     * @throws NumberFormatException ?????????????????????{@link String}????????????????????????{@link Double}
     * @throws TypeCastException     ????????????????????????{@link Double}
     */
    public static Double castDouble(Object value) {
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Double.class);
        if (typeCast != null) {
            return (Double) typeCast.cast(value);
        }
        return null;
    }

    /**
     * ???????????????????????? double
     *
     * @param value ????????????
     * @return double
     * @throws NumberFormatException ?????????????????????{@link String}???????????????????????? double
     * @throws TypeCastException     ???????????????????????? double
     */
    public static double castDoubleValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(double.class);
        if (typeCast != null) {
            return (double) typeCast.cast(value);
        }
        return 0D;
    }

    /**
     * ????????????????????????{@link Float}
     *
     * @param value ????????????
     * @return {@link Float} or null
     * @throws NumberFormatException ?????????????????????{@link String}????????????????????????{@link Float}
     * @throws TypeCastException     ????????????????????????{@link Float}
     */
    public static Float castFloat(Object value) {
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Float.class);
        if (typeCast != null) {
            return (Float) typeCast.cast(value);
        }
        return null;
    }

    /**
     * ???????????????????????? float
     *
     * @param value ????????????
     * @return float
     * @throws NumberFormatException ?????????????????????{@link String}???????????????????????? float
     * @throws TypeCastException     ???????????????????????? float
     */
    public static float castFloatValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(float.class);
        if (typeCast != null) {
            return (float) typeCast.cast(value);
        }
        return 0F;
    }

    /**
     * ????????????????????????{@link Long}
     *
     * @param value ????????????
     * @return {@link Long} or null
     * @throws NumberFormatException ?????????????????????{@link String}????????????????????????{@link Long}
     * @throws TypeCastException     ????????????????????????{@link Long}
     */
    public static Long castLong(Object value) {
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Long.class);
        if (typeCast != null) {
            return (Long) typeCast.cast(value);
        }
        return null;
    }

    /**
     * ???????????????????????? long
     *
     * @param value ????????????
     * @return long
     * @throws NumberFormatException ?????????????????????{@link String}???????????????????????? long
     * @throws TypeCastException     ???????????????????????? long
     */
    public static long castLongValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(long.class);
        if (typeCast != null) {
            return (long) typeCast.cast(value);
        }
        return 0L;
    }

    /**
     * ????????????????????????{@link Integer}
     *
     * @param value ????????????
     * @return {@link Integer} or null
     * @throws NumberFormatException ?????????????????????{@link String}????????????????????????{@link Integer}
     * @throws TypeCastException     ????????????????????????{@link Integer}
     */
    public static Integer castInteger(Object value) {
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Integer.class);
        if (typeCast != null) {
            return (Integer) typeCast.cast(value);
        }
        return null;
    }

    /**
     * ???????????????????????? int
     *
     * @param value ????????????
     * @return int
     * @throws NumberFormatException ?????????????????????{@link String}???????????????????????? int
     * @throws TypeCastException     ???????????????????????? int
     */
    public static int castIntValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(int.class);
        if (typeCast != null) {
            return (int) typeCast.cast(value);
        }
        return 0;
    }

    /**
     * ????????????????????????{@link Short}
     *
     * @param value ????????????
     * @return {@link Short} or null
     * @throws NumberFormatException ?????????????????????{@link String}????????????????????????{@link Short}
     * @throws TypeCastException     ????????????????????????{@link Short}
     */
    public static Short castShort(Object value) {
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Short.class);
        if (typeCast != null) {
            return (Short) typeCast.cast(value);
        }
        return null;
    }

    /**
     * ???????????????????????? short
     *
     * @param value ????????????
     * @return short
     * @throws NumberFormatException ?????????????????????{@link String}???????????????????????? short
     * @throws TypeCastException     ???????????????????????? short
     */
    public static short castShortValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(short.class);
        if (typeCast != null) {
            return (short) typeCast.cast(value);
        }
        return 0;
    }

    /**
     * ????????????????????????{@link Byte}
     *
     * @param value ????????????
     * @return {@link Byte} or null
     * @throws NumberFormatException ?????????????????????{@link String}????????????????????????{@link Byte}
     * @throws TypeCastException     ????????????????????????{@link Byte}
     */
    public static Byte castByte(Object value) {
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Byte.class);
        if (typeCast != null) {
            return (Byte) typeCast.cast(value);
        }
        return null;
    }

    /**
     * ???????????????????????? byte
     *
     * @param value ????????????
     * @return byte
     * @throws NumberFormatException ?????????????????????{@link String}???????????????????????? byte
     * @throws TypeCastException     ???????????????????????? byte
     */
    public static byte castByteValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(byte.class);
        if (typeCast != null) {
            return (byte) typeCast.cast(value);
        }
        return 0;
    }

    /**
     * ????????????????????????{@link Boolean}
     *
     * @param value ????????????
     * @return {@link Boolean} or null
     * @throws TypeCastException ????????????????????????{@link Boolean}
     */
    public static Boolean castBoolean(Object value) {
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Boolean.class);
        if (typeCast != null) {
            return (Boolean) typeCast.cast(value);
        }
        return null;
    }

    /**
     * ???????????????????????? boolean
     *
     * @param value ????????????
     * @return boolean
     * @throws TypeCastException ???????????????????????? boolean
     */
    public static boolean castBooleanValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(boolean.class);
        if (typeCast != null) {
            return (boolean) typeCast.cast(value);
        }
        return false;
    }

    /**
     * ????????????????????????{@link BigInteger}
     *
     * @param value ????????????
     * @return {@link BigInteger} or null
     * @throws NumberFormatException ?????????????????????{@link String}????????????????????????{@link BigInteger}
     * @throws TypeCastException     ????????????????????????{@link BigInteger}
     */
    public static BigInteger castBigInteger(Object value) {
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(BigInteger.class);
        if (typeCast != null) {
            return (BigInteger) typeCast.cast(value);
        }
        return null;
    }

    /**
     * ????????????????????????{@link BigDecimal}
     *
     * @param value ????????????
     * @return {@link BigDecimal} or null
     * @throws NumberFormatException ?????????????????????{@link String}????????????????????????{@link BigDecimal}
     * @throws TypeCastException     ????????????????????????{@link BigDecimal}
     */
    public static BigDecimal castBigDecimal(Object value) {
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(BigDecimal.class);
        if (typeCast != null) {
            return (BigDecimal) typeCast.cast(value);
        }
        return null;
    }

    /**
     * ????????????????????????{@link Date}
     *
     * @param value    ????????????
     * @param generate
     * @return {@link Date} or null
     * @throws TypeCastException ????????????????????????{@link Date}
     */
    public static Date castDate(Object value, UniversalGenerate<Codec> generate) {
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
     * ????????????????????????{@link Instant}
     *
     * @param value    ????????????
     * @param generate
     * @return {@link Instant} or null
     * @throws TypeCastException ????????????????????????{@link Instant}
     */
    public static Instant castInstant(Object value, UniversalGenerate<Codec> generate) {
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
     * ?????????????????????????????????{@link TypeToken}
     *
     * @param value         ????????????
     * @param typeToken     ??????{@link TypeToken}
     * @param converter
     * @param codecGenerate
     * @return T
     */
    public static <T> T castType(Object value, TypeToken<T> typeToken, Converter<?> converter, DataCodecGenerate codecGenerate) {
        if (typeToken.getRawType().isInstance(value)) {
            return (T) value;
        }

        Type type = typeToken.getType();

        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(type);
        if (typeCast != null) {
            return (T) typeCast.cast(value);
        }

        if (type == Date.class) {
            return (T) castDate(value, codecGenerate);
        }

        if (type == Instant.class) {
            return (T) castInstant(value, codecGenerate);
        }

        Class<?> clazz = typeToken.getRawType();
        if (List.class.isAssignableFrom(clazz)) {
            if (clazz == DataArray.class) {
                return (T) converter.toArray(value);
            }
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
            if (clazz == DataMapper.class) {
                return (T) converter.toMapper(value);
            }
            DataMapper dataMapper = castMapper(value, converter);
            if (dataMapper != null) {
                return dataMapper.toJavaBean(type);
            }
        }

        if (value != null && !typeToken.getRawType().isInstance(value)) {
            Codec codec = codecGenerate.get(typeToken);
            if (codec != null) {
                T t = (T) codec.decode(value);
                if (!typeToken.getRawType().isInstance(t)) {
                    throw new TypeCastException("Can not cast '" + value.getClass() + "' to " + typeToken.getRawType());
                }
                return t;
            }
        } else {
            return (T) value;
        }

        throw new TypeCastException("Can not cast '" + value.getClass() + "' to " + typeToken.getRawType());
    }
}
