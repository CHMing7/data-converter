package com.chm.converter.core;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.chm.converter.core.exception.TypeCastException;
import com.chm.converter.core.reflect.ConverterPreconditions;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.ArrayUtil;
import com.chm.converter.core.utils.TypeUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-05-24
 **/
public class DataMapper extends LinkedHashMap<String, Object> {

    private final Converter<?> converter;

    private final DataCodecGenerate codecGenerate;

    /**
     * 默认构造方法
     */
    public DataMapper(Converter<?> converter) {
        ConverterPreconditions.checkNotNull(converter, "param converter cannot be null");
        this.converter = converter;
        this.codecGenerate = DataCodecGenerate.getDataCodecGenerate(converter);
    }

    public DataMapper(Converter<?> converter, int initialCapacity) {
        super(initialCapacity);
        ConverterPreconditions.checkNotNull(converter, "param converter cannot be null");
        this.converter = converter;
        this.codecGenerate = DataCodecGenerate.getDataCodecGenerate(converter);
    }

    public DataMapper(Converter<?> converter, int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        ConverterPreconditions.checkNotNull(converter, "param converter cannot be null");
        this.converter = converter;
        this.codecGenerate = DataCodecGenerate.getDataCodecGenerate(converter);
    }

    public DataMapper(Converter<?> converter, int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
        ConverterPreconditions.checkNotNull(converter, "param converter cannot be null");
        this.converter = converter;
        this.codecGenerate = DataCodecGenerate.getDataCodecGenerate(converter);
    }

    @SuppressWarnings("unchecked")
    public DataMapper(Converter<?> converter, Map map) {
        super(map);
        ConverterPreconditions.checkNotNull(converter, "param converter cannot be null");
        this.converter = converter;
        this.codecGenerate = DataCodecGenerate.getDataCodecGenerate(converter);
    }

    public Converter<?> getConverter() {
        return converter;
    }

    /**
     * 返回{@link DataMapper}中key键映射的值
     *
     * @param key 键名
     * @return value Object对象
     */
    public Object get(String key) {
        return super.get(key);
    }

    /**
     * 返回{@link DataMapper}中key键映射的值
     *
     * @param key 键名
     * @return value Object对象
     */
    @Override
    public Object get(Object key) {
        if (key instanceof Number || key instanceof Character || key instanceof Boolean) {
            return super.get(key.toString());
        }

        return super.get(key);
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return super.entrySet();
    }

    @Override
    public Set<String> keySet() {
        return super.keySet();
    }

    /**
     * 如果{@link DataMapper}中包含key键则返回true，否则返回false
     *
     * @param key 键名
     * @return 检查结果 true或false
     */
    @Override
    public boolean containsKey(Object key) {
        boolean result = super.containsKey(key);
        if (!result) {
            if (key instanceof Number
                    || key instanceof Character
                    || key instanceof Boolean
                    || key instanceof UUID) {
                result = super.containsKey(key.toString());
            }
        }
        return result;
    }

    /**
     * 返回{@link DataMapper}中指定键映射到的值，如果此映射不包含该键的映射，则返回 {@code defaultValue}
     *
     * @param key          键名
     * @param defaultValue 键的默认映射
     * @return value Object对象
     */
    public Object getOrDefault(String key, Object defaultValue) {
        return super.getOrDefault(key, defaultValue);
    }

    /**
     * 返回{@link DataMapper}中指定键映射到的值，如果此映射不包含该键的映射，则返回 {@code defaultValue}
     *
     * @param key          键名
     * @param defaultValue 键的默认映射
     * @return value Object对象
     */
    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        if (key instanceof Number
                || key instanceof Character
                || key instanceof Boolean) {
            return super.getOrDefault(key.toString(), defaultValue);
        }

        return super.getOrDefault(key, defaultValue);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link DataArray}
     *
     * @param key 键名
     * @return {@link DataArray} or null
     */
    @SuppressWarnings("unchecked")
    public DataArray getArray(String key) {
        Object value = super.get(key);

        return DataCast.castArray(value, this.converter);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link DataMapper}
     *
     * @param key 键名
     * @return{@link Mapper}or null
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public DataMapper getMapper(String key) {
        Object value = super.get(key);

        return DataCast.castMapper(value, this.converter);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link String}
     *
     * @param key 键名
     * @return {@link String} or null
     */
    public String getString(String key) {
        Object value = super.get(key);

        return DataCast.castString(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link Double}
     *
     * @param key 键名
     * @return {@link Double} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Double}
     * @throws TypeCastException     类型不支持转化为{@link Double}
     */
    public Double getDouble(String key) {
        Object value = super.get(key);

        return DataCast.castDouble(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的 double
     *
     * @param key 键名
     * @return double
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 double
     * @throws TypeCastException     类型不支持转化为double value
     */
    public double getDoubleValue(String key) {
        Object value = super.get(key);

        return DataCast.castDoubleValue(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的 {@link Float}
     *
     * @param key 键名
     * @return {@link Float} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Float}
     * @throws TypeCastException     类型不支持转化为{@link Float}
     */
    public Float getFloat(String key) {
        Object value = super.get(key);

        return DataCast.castFloat(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的 float
     *
     * @param key 键名
     * @return float
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 float
     * @throws TypeCastException     类型不支持转化为 float
     */
    public float getFloatValue(String key) {
        Object value = super.get(key);

        return DataCast.castFloatValue(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的 {@link Long}
     *
     * @param key 键名
     * @return {@link Long} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Long}
     * @throws TypeCastException     类型不支持转化为{@link Long}
     */
    public Long getLong(String key) {
        Object value = super.get(key);

        return DataCast.castLong(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的 long
     *
     * @param key 键名
     * @return long
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 long
     * @throws TypeCastException     类型不支持转化为 long
     */
    public long getLongValue(String key) {
        Object value = super.get(key);

        return DataCast.castLongValue(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link Integer}
     *
     * @param key 键名
     * @return {@link Integer} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Integer}
     * @throws TypeCastException     类型不支持转化为{@link Integer}
     */
    public Integer getInteger(String key) {
        Object value = super.get(key);

        return DataCast.castInteger(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的 int
     *
     * @param key 键名
     * @return int
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 int
     * @throws TypeCastException     类型不支持转化为 int
     */
    public int getIntValue(String key) {
        Object value = super.get(key);

        return DataCast.castIntValue(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link Short}
     *
     * @param key 键名
     * @return {@link Short} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Short}
     * @throws TypeCastException     类型不支持转化为{@link Short}
     */
    public Short getShort(String key) {
        Object value = super.get(key);

        return DataCast.castShort(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的 short
     *
     * @param key 键名
     * @return short
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 short
     * @throws TypeCastException     类型不支持转化为 short
     */
    public short getShortValue(String key) {
        Object value = super.get(key);

        return DataCast.castShortValue(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link Byte}
     *
     * @param key 键名
     * @return {@link Byte} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Byte}
     * @throws TypeCastException     类型不支持转化为{@link Byte}
     */
    public Byte getByte(String key) {
        Object value = super.get(key);

        return DataCast.castByte(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的 byte
     *
     * @param key 键名
     * @return byte
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为 byte
     * @throws TypeCastException     类型不支持转化为 byte
     */
    public byte getByteValue(String key) {
        Object value = super.get(key);

        return DataCast.castByteValue(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link Boolean}
     *
     * @param key 键名
     * @return {@link Boolean} or null
     * @throws TypeCastException 类型不支持转化为{@link Boolean}
     */
    public Boolean getBoolean(String key) {
        Object value = super.get(key);

        return DataCast.castBoolean(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的 boolean
     *
     * @param key 键名
     * @return boolean
     * @throws TypeCastException 类型不支持转化为 boolean
     */
    public boolean getBooleanValue(String key) {
        Object value = super.get(key);

        return DataCast.castBooleanValue(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link BigInteger}
     *
     * @param key 键名
     * @return {@link BigInteger} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link BigInteger}
     * @throws TypeCastException     类型不支持转化为{@link BigInteger}
     */
    public BigInteger getBigInteger(String key) {
        Object value = super.get(key);

        return DataCast.castBigInteger(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link BigDecimal}
     *
     * @param key 键名
     * @return {@link BigDecimal} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link BigDecimal}
     * @throws TypeCastException     类型不支持转化为{@link BigDecimal}
     */
    public BigDecimal getBigDecimal(String key) {
        Object value = super.get(key);

        return DataCast.castBigDecimal(value);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link Date}
     *
     * @param key 键名
     * @return {@link Date} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Date}
     * @throws TypeCastException     类型不支持转化为{@link Date}
     */
    public Date getDate(String key) {
        Object value = super.get(key);

        return DataCast.castDate(value, this.codecGenerate);
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link Instant}
     *
     * @param key 键名
     * @return {@link Instant} or null
     * @throws NumberFormatException 如果映射的值为{@link String}并且它不可解析为{@link Instant}
     * @throws TypeCastException     类型不支持转化为{@link Instant}
     */
    public Instant getInstant(String key) {
        Object value = super.get(key);

        return DataCast.castInstant(value, this.codecGenerate);
    }


    /**
     * 将此 {@link DataMapper} 转换为指定的对象
     * <p>
     * {@code Map<String, User> users = mapper.toJavaBean(new TypeToken<HashMap<String, User>>(){}.getType());}
     *
     * @param clazz 指定要转换的{@link Class<T>}
     * @return T
     */
    @SuppressWarnings("unchecked")
    public <T> T toJavaBean(Class<T> clazz) {
        return toJavaBean(TypeToken.get(clazz).getType());
    }

    /**
     * 将此{@link DataMapper}转换为指定的对象
     * <p>
     * {@code Map<String, User> users = mapper.toJavaBean(new TypeToken<HashMap<String, User>>(){}.getType());}
     *
     * @param type 指定要转换的{@link Type}
     * @return T
     */
    @SuppressWarnings("unchecked")
    public <T> T toJavaBean(Type type) {
        if (type == DataMapper.class) {
            return (T) this;
        }
        Type[] typeArgs = null;
        if (type instanceof ParameterizedType) {
            typeArgs = ((ParameterizedType) type).getActualTypeArguments();
        }
        Class<?> clazz = TypeUtil.getClass(type);
        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz, converter);
        Object construct = javaBeanInfo.getObjectConstructor().construct();
        if (Map.class.isAssignableFrom(clazz)) {
            Map<String, Object> map = (Map<String, Object>) construct;
            for (String key : keySet()) {
                if (ArrayUtil.length(typeArgs) > 1) {
                    map.put(key, getObject(key, typeArgs[1]));
                } else {
                    map.put(key, getObject(key, String.class));
                }
            }
            return (T) map;
        }
        Map<String, FieldInfo> nameFieldInfoMap = javaBeanInfo.getNameFieldInfoMap();
        for (String key : keySet()) {
            FieldInfo fieldInfo = nameFieldInfoMap.get(key);
            if (fieldInfo == null) {
                continue;
            }
            Object val = get(fieldInfo.getName());
            if (fieldInfo.getFieldClass().isInstance(val)) {
                fieldInfo.set(construct, val);
                continue;
            }
            Codec codec = codecGenerate.get(fieldInfo.getFieldType());
            if (codec instanceof DefaultDateCodec) {
                String str = getObject(fieldInfo.getName(), String.class);
                fieldInfo.set(construct, ((DefaultDateCodec<?>) codec).decode(str, fieldInfo.getFormat()));
                continue;
            }
            if (codec instanceof Java8TimeCodec) {
                String str = getObject(fieldInfo.getName(), String.class);
                fieldInfo.set(construct, ((Java8TimeCodec<?>) codec).decode(str, fieldInfo.getFormat()));
                continue;
            }
            fieldInfo.set(construct, getObject(fieldInfo.getName(), fieldInfo.getFieldType()));
        }
        return (T) construct;
    }

    /**
     * 将此{@link DataMapper}转换为指定的对象
     * <p>
     * {@code Map<String, User> users = mapper.toJavaBean(new TypeToken<HashMap<String, User>>(){}.getType());}
     *
     * @param typeToken 指定要转换的{@link TypeToken}
     * @return T
     */
    @SuppressWarnings("unchecked")
    public <T> T toJavaBean(TypeToken<T> typeToken) {
        return toJavaBean(typeToken.getType());
    }


    /**
     * 返回此{@link DataMapper}中key键映射的{@link Type}
     * <p>
     * {@code User user = mapper.getObject("user", User.class);}
     *
     * @param key   键名
     * @param clazz 指定要转换的{@link Class<T>}
     * @return {@code <T>} or null
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T getObject(String key, Class<T> clazz) {
        return getObject(key, TypeToken.get(clazz));
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link Type}
     * <p>
     * {@code User user = mapper.getObject("user", User.class);}
     *
     * @param key  键名
     * @param type 指定要转换的{@link Type}
     * @return {@code <T>} or null
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T getObject(String key, Type type) {
        return getObject(key, TypeToken.get(type));
    }

    /**
     * 返回此{@link DataMapper}中key键映射的{@link Type}
     * <p>
     * {@code User user = mapper.getObject("user", User.class);}
     *
     * @param key       键名
     * @param typeToken 指定要转换的{@link TypeToken}
     * @return {@code <T>} or null
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T getObject(String key, TypeToken<T> typeToken) {
        Object value = get(key);

        return DataCast.castType(value, typeToken, this.converter, this.codecGenerate);
    }

    /**
     * 元素的链式添加
     *
     * <pre>
     * DataMapper mapper = DataMapper.of(converter).fluentPut("a", 1).fluentPut("b", 2).fluentPut("c", 3);
     * </pre>
     *
     * @param key   键名
     * @param value 与指定键关联的值
     * @return {@link DataMapper}
     */
    public DataMapper fluentPut(String key, Object value) {
        put(key, value);
        return this;
    }

    /**
     * 新建一个{@link DataMapper}
     *
     * @return {@link DataMapper}
     */
    public static DataMapper of(Converter<?> converter) {
        return new DataMapper(converter);
    }

    /**
     * 将一对键值打包为{@link DataMapper}
     *
     * <pre>
     * DataMapper mapper = DataMapper.of("name", "dataMapper");
     * </pre>
     *
     * @param key   键名
     * @param value 与指定键关联的值
     * @return {@link DataMapper}
     */
    public static DataMapper of(Converter<?> converter, String key, Object value) {
        DataMapper dataMapper = new DataMapper(converter, 2);
        dataMapper.put(key, value);
        return dataMapper;
    }

    /**
     * 将两个键值对打包为{@link DataMapper}
     *
     * <pre>
     * DataMapper mapper = DataMapper.of(converter, "key1", "value1", "key2", "value2");
     * </pre>
     *
     * @param k1 第一个键名
     * @param v1 第一个值
     * @param k2 第二个键名
     * @param v2 第二个值
     * @return {@link DataMapper}
     */
    public static DataMapper of(Converter<?> converter, String k1, Object v1, String k2, Object v2) {
        DataMapper dataMapper = new DataMapper(converter, 4);
        dataMapper.put(k1, v1);
        dataMapper.put(k2, v2);
        return dataMapper;
    }

    /**
     * 将三个键值对打包为{@link DataMapper}
     *
     * <pre>
     * DataMapper mapper = DataMapper.of(converter, "key1", "value1", "key2", "value2", "key3", "value3");
     * </pre>
     *
     * @param k1 第一个键名
     * @param v1 第一个值
     * @param k2 第二个键名
     * @param v2 第二个值
     * @param k3 第三个键名
     * @param v3 第三个值
     * @return {@link DataMapper}
     */
    public static DataMapper of(Converter<?> converter, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        DataMapper dataMapper = new DataMapper(converter, 5);
        dataMapper.put(k1, v1);
        dataMapper.put(k2, v2);
        dataMapper.put(k3, v3);
        return dataMapper;
    }


    /**
     * 将键值对打包为{@link DataMapper}
     *
     * <pre>
     * DataMapper mapper = DataMapper.of(converter, map);
     * </pre>
     *
     * @param map 键值对
     * @return {@link DataMapper}
     */
    public static DataMapper of(Converter<?> converter, Map<?, ?> map) {
        return new DataMapper(converter, map);
    }

}
