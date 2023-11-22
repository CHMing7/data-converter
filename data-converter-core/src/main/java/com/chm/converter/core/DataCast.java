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
import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.exception.TypeCastException;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.ArrayUtil;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.core.utils.TypeUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-05-30
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
     * 将指定对象转化为 {@link DataArray}
     *
     * @param value     指定对象
     * @param converter
     * @return {@link DataArray} or null
     */
    public static DataArray castArray(Object value, Converter<?> converter) {
        if (value == null) {
            return null;
        }
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
     * 将指定对象转化为 {@link DataMapper}
     *
     * @param value     指定对象
     * @param converter
     * @return {@link DataMapper} or null
     */
    public static DataMapper castMapper(Object value, Converter<?> converter) {
        if (value == null) {
            return null;
        }
        if (value instanceof DataMapper) {
            return (DataMapper) value;
        }

        if (value instanceof Map) {
            return DataMapper.of(converter, (Map) value);
        }
        return converter.toMapper(value);
    }

    /**
     * 将指定对象转化为 {@link String}
     *
     * @param value 指定对象
     * @return {@link String} or null
     */
    public static String castString(Object value) {
        if (value == null) {
            return null;
        }
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(String.class);
        if (typeCast != null) {
            return (String) typeCast.cast(value);
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
    public static Double castDouble(Object value) {
        if (value == null) {
            return null;
        }
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Double.class);
        if (typeCast != null) {
            return (Double) typeCast.cast(value);
        }
        return null;
    }

    /**
     * 将指定对象转化为 double
     *
     * @param value 指定对象
     * @return double
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 double
     * @throws TypeCastException     类型不支持转化为 double
     */
    public static double castDoubleValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(double.class);
        if (typeCast != null) {
            return (double) typeCast.cast(value);
        }
        return 0D;
    }

    /**
     * 将指定对象转化为{@link Float}
     *
     * @param value 指定对象
     * @return {@link Float} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Float}
     * @throws TypeCastException     类型不支持转化为{@link Float}
     */
    public static Float castFloat(Object value) {
        if (value == null) {
            return null;
        }
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Float.class);
        if (typeCast != null) {
            return (Float) typeCast.cast(value);
        }
        return null;
    }

    /**
     * 将指定对象转化为 float
     *
     * @param value 指定对象
     * @return float
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 float
     * @throws TypeCastException     类型不支持转化为 float
     */
    public static float castFloatValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(float.class);
        if (typeCast != null) {
            return (float) typeCast.cast(value);
        }
        return 0F;
    }

    /**
     * 将指定对象转化为{@link Long}
     *
     * @param value 指定对象
     * @return {@link Long} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Long}
     * @throws TypeCastException     类型不支持转化为{@link Long}
     */
    public static Long castLong(Object value) {
        if (value == null) {
            return null;
        }
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Long.class);
        if (typeCast != null) {
            return (Long) typeCast.cast(value);
        }
        return null;
    }

    /**
     * 将指定对象转化为 long
     *
     * @param value 指定对象
     * @return long
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 long
     * @throws TypeCastException     类型不支持转化为 long
     */
    public static long castLongValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(long.class);
        if (typeCast != null) {
            return (long) typeCast.cast(value);
        }
        return 0L;
    }

    /**
     * 将指定对象转化为{@link Integer}
     *
     * @param value 指定对象
     * @return {@link Integer} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Integer}
     * @throws TypeCastException     类型不支持转化为{@link Integer}
     */
    public static Integer castInteger(Object value) {
        if (value == null) {
            return null;
        }
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Integer.class);
        if (typeCast != null) {
            return (Integer) typeCast.cast(value);
        }
        return null;
    }

    /**
     * 将指定对象转化为 int
     *
     * @param value 指定对象
     * @return int
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 int
     * @throws TypeCastException     类型不支持转化为 int
     */
    public static int castIntValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(int.class);
        if (typeCast != null) {
            return (int) typeCast.cast(value);
        }
        return 0;
    }

    /**
     * 将指定对象转化为{@link Short}
     *
     * @param value 指定对象
     * @return {@link Short} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Short}
     * @throws TypeCastException     类型不支持转化为{@link Short}
     */
    public static Short castShort(Object value) {
        if (value == null) {
            return null;
        }
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Short.class);
        if (typeCast != null) {
            return (Short) typeCast.cast(value);
        }
        return null;
    }

    /**
     * 将指定对象转化为 short
     *
     * @param value 指定对象
     * @return short
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 short
     * @throws TypeCastException     类型不支持转化为 short
     */
    public static short castShortValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(short.class);
        if (typeCast != null) {
            return (short) typeCast.cast(value);
        }
        return 0;
    }

    /**
     * 将指定对象转化为{@link Byte}
     *
     * @param value 指定对象
     * @return {@link Byte} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Byte}
     * @throws TypeCastException     类型不支持转化为{@link Byte}
     */
    public static Byte castByte(Object value) {
        if (value == null) {
            return null;
        }
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Byte.class);
        if (typeCast != null) {
            return (Byte) typeCast.cast(value);
        }
        return null;
    }

    /**
     * 将指定对象转化为 byte
     *
     * @param value 指定对象
     * @return byte
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 byte
     * @throws TypeCastException     类型不支持转化为 byte
     */
    public static byte castByteValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(byte.class);
        if (typeCast != null) {
            return (byte) typeCast.cast(value);
        }
        return 0;
    }

    /**
     * 将指定对象转化为{@link Boolean}
     *
     * @param value 指定对象
     * @return {@link Boolean} or null
     * @throws TypeCastException 类型不支持转化为{@link Boolean}
     */
    public static Boolean castBoolean(Object value) {
        if (value == null) {
            return null;
        }
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(Boolean.class);
        if (typeCast != null) {
            return (Boolean) typeCast.cast(value);
        }
        return null;
    }

    /**
     * 将指定对象转化为 boolean
     *
     * @param value 指定对象
     * @return boolean
     * @throws TypeCastException 类型不支持转化为 boolean
     */
    public static boolean castBooleanValue(Object value) {
        TypeCast typeCast = CLASS_TYPE_CAST_MAP.get(boolean.class);
        if (typeCast != null) {
            return (boolean) typeCast.cast(value);
        }
        return false;
    }

    /**
     * 将指定对象转化为{@link BigInteger}
     *
     * @param value 指定对象
     * @return {@link BigInteger} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link BigInteger}
     * @throws TypeCastException     类型不支持转化为{@link BigInteger}
     */
    public static BigInteger castBigInteger(Object value) {
        if (value == null) {
            return null;
        }
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(BigInteger.class);
        if (typeCast != null) {
            return (BigInteger) typeCast.cast(value);
        }
        return null;
    }

    /**
     * 将指定对象转化为{@link BigDecimal}
     *
     * @param value 指定对象
     * @return {@link BigDecimal} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link BigDecimal}
     * @throws TypeCastException     类型不支持转化为{@link BigDecimal}
     */
    public static BigDecimal castBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        TypeCast<?> typeCast = CLASS_TYPE_CAST_MAP.get(BigDecimal.class);
        if (typeCast != null) {
            return (BigDecimal) typeCast.cast(value);
        }
        return null;
    }

    /**
     * 将指定对象转化为{@link Date}
     *
     * @param value    指定对象
     * @param generate
     * @return {@link Date} or null
     * @throws TypeCastException 类型不支持转化为{@link Date}
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
     * 将指定对象转化为{@link Instant}
     *
     * @param value    指定对象
     * @param generate
     * @return {@link Instant} or null
     * @throws TypeCastException 类型不支持转化为{@link Instant}
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
     * 将指定对象转化为指定的{@link TypeToken}
     *
     * @param value         指定对象
     * @param typeToken     指定{@link TypeToken}
     * @param converter
     * @param codecGenerate
     * @return T
     */
    public static <T> T castType(Object value, TypeToken<T> typeToken, Converter<?> converter, DataCodecGenerate codecGenerate) {
        if (typeToken.getRawType().isInstance(value) &&
                ArrayUtil.isEmpty(TypeUtil.getTypeArguments(typeToken.getType()))) {
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
        if (Collection.class.isAssignableFrom(clazz)) {
            if (clazz == DataArray.class) {
                return (T) converter.toArray(value);
            }
            DataArray dataArray;
            if (value instanceof Map) {
                // 兼容反序列化为map时，xml无法识别是collection还是map
                dataArray = castArray(((Map<?, ?>) value).values(), converter);
            } else {
                dataArray = castArray(value, converter);
            }
            if (dataArray != null) {
                Collection<Object> list = (Collection<Object>) ConstructorFactory.INSTANCE.get(typeToken).construct();
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
