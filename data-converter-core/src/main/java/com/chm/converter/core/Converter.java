package com.chm.converter.core;

import com.chm.converter.core.cfg.ConvertFeature;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-08-13
 **/
public interface Converter<S> {

    Converter<Object> DEFAULT = new Converter<Object>() {

        @Override
        public Object encode(Object source) {
            return source;
        }

        @Override
        public DataType getDataType() {
            return DataType.createDataType("default");
        }

        @Override
        public boolean checkCanBeLoad() {
            return true;
        }

        @Override
        public Object convertToJavaObject(Object source, Type targetType) {
            return source;
        }

        @Override
        public Object convertToJavaObject(Object source, Class targetType) {
            return source;
        }
    };

    Map<Converter<?>, Logger> CONVERTER_LOGGER_MAP = MapUtil.newConcurrentHashMap();

    Map<Converter<?>, String> CONVERTER_NAME_MAP = MapUtil.newConcurrentHashMap();

    Map<Converter<?>, String> CONVERTER_DATE_FORMAT_PATTERN_MAP = MapUtil.newConcurrentHashMap();

    Map<Converter<?>, DateTimeFormatter> CONVERTER_DATE_TIME_FORMATTER_MAP = MapUtil.newConcurrentHashMap();

    Map<Converter<?>, TimeZone> CONVERTER_TIME_ZONE_MAP = MapUtil.newConcurrentHashMap();

    Map<Converter<?>, Locale> CONVERTER_LOCALE_MAP = MapUtil.newConcurrentHashMap();

    Map<Converter<?>, Integer> CONVERTER_FEATURES_MAP = MapUtil.newConcurrentHashMap();

    /**
     * 将指定对象转为{@link DataMapper} or {@link DataArray} or 原类型
     *
     * @param converter
     * @param obj       指定对象
     * @return
     */
    static Object toData(Converter<?> converter, Object obj) {
        if (obj instanceof Map) {
            return DataMapper.of(converter, (Map<?, ?>) obj);
        }

        if (obj instanceof Collection) {
            return DataArray.of(converter, (Collection<?>) obj);
        }

        if (obj != null && obj.getClass().isArray()) {
            return DataArray.of(converter, (Object[]) obj);
        }

        return obj;
    }

    /**
     * 获取默认功能配置
     *
     * @return
     */
    static int getDefaultFeature() {
        int flags = 0;
        Class<ConvertFeature> enumClass = ConvertFeature.class;
        for (ConvertFeature value : enumClass.getEnumConstants()) {
            if (value.enabledByDefault()) {
                flags |= value.getMask();
            }
        }
        return flags;
    }

    /**
     * 将源数据转换为目标类型{@link Class<T>}的java对象
     *
     * @param source     源数据
     * @param targetType 目标类型 (Class对象)
     * @param <T>        目标类型泛型
     * @return 转换后的目标类型对象
     */
    <T> T convertToJavaObject(S source, Class<T> targetType);

    /**
     * 将源数据转换为目标类型{@link Type}的java对象
     *
     * @param source     源数据
     * @param targetType 目标类型 (Type对象)
     * @param <T>        目标类型泛型
     * @return 转换后的目标类型对象
     */
    <T> T convertToJavaObject(S source, Type targetType);

    /**
     * 将源数据转换为目标类型{@link TypeToken<T>}的java对象
     *
     * @param source     源数据
     * @param targetType 目标类型 (TypeToken对象)
     * @param <T>        目标类型泛型
     * @return 转换后的目标类型对象
     */
    default <T> T convertToJavaObject(S source, TypeToken<T> targetType) {
        return convertToJavaObject(source, targetType.getType());
    }

    /**
     * 将源数据转换为{@link List<T>}对象
     *
     * @param source     源数据
     * @param targetType 目标类型 (Class对象)
     * @param <T>        目标类型泛型
     * @return 转换后的目标类型List对象
     */
    default <T> List<T> convertToList(S source, Class<T> targetType) {
        TypeToken<List<T>> listType = TypeToken.getParameterized(List.class, targetType);
        return convertToJavaObject(source, listType);
    }

    /**
     * 将源数据转换为{@link List<T>}对象
     *
     * @param source     源数据
     * @param targetType 目标类型 (Type对象)
     * @param <T>        目标类型泛型
     * @return 转换后的目标类型List对象
     */
    default <T> List<T> convertToList(S source, Type targetType) {
        TypeToken<List<T>> listType = TypeToken.getParameterized(List.class, targetType);
        return convertToJavaObject(source, listType);
    }

    /**
     * 将源数据转换为{@link List<T>}对象
     *
     * @param source     源数据
     * @param targetType 目标类型 (TypeToken对象)
     * @param <T>        目标类型泛型
     * @return 转换后的目标类型List对象
     */
    default <T> List<T> convertToList(S source, TypeToken<T> targetType) {
        TypeToken<List<T>> listType = TypeToken.getParameterized(List.class, targetType.getType());
        return convertToJavaObject(source, listType);
    }

    /**
     * 将源数据转换为{@link Map<String, Object>}对象
     *
     * @param source 源数据
     * @return 转换后的目标类型对象
     */
    default Map<String, Object> convertToMap(S source) {
        TypeToken<Map<String, Object>> mapType = new TypeToken<Map<String, Object>>() {
        };
        return convertToJavaObject(source, mapType);
    }

    /**
     * 将源数据转换为{@link DataMapper}对象
     *
     * @param source 源数据
     * @return 转换后的目标类型对象
     */
    default DataMapper convertToMapper(S source) {
        Map<String, Object> map = convertToMap(source);
        return DataMapper.of(this, map);
    }

    /**
     * 将源数据转换为{@link DataArray}对象
     *
     * @param source 源数据
     * @return 转换后的目标类型对象
     */
    default DataArray convertToArray(S source) {
        TypeToken<Map<String, Object>> mapType = new TypeToken<Map<String, Object>>() {
        };
        List<Map<String, Object>> list = convertToList(source, mapType);
        return DataArray.of(this, list);
    }

    /**
     * 将指定对象转成{@link DataMapper}
     *
     * @param obj 指定对象
     * @return {@link DataMapper}
     */
    default DataMapper toMapper(Object obj) {
        if (obj == null || ClassUtil.isSimpleTypeOrArray(obj.getClass())) {
            return null;
        }

        if (obj instanceof DataMapper) {
            return (DataMapper) obj;
        }

        if (obj instanceof Map) {
            return (DataMapper) toData(this, obj);
        }

        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(obj.getClass(), this);
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        DataMapper dataMapper = DataMapper.of(this);
        for (FieldInfo fieldInfo : sortedFieldList) {
            Object val = fieldInfo.get(obj);
            if (val == null) {
                continue;
            }
            dataMapper.put(fieldInfo.getName(), toData(this, val));
        }
        return dataMapper;
    }

    /**
     * 将指定对象转成{@link DataArray}
     *
     * @param obj 指定对象
     * @return {@link DataArray}
     */
    default DataArray toArray(Object obj) {
        if (obj == null || ClassUtil.isSimpleValueType(obj.getClass())) {
            return null;
        }

        if (obj instanceof DataArray) {
            return (DataArray) obj;
        }

        if (obj instanceof Collection) {
            return (DataArray) toData(this, obj);
        }

        if (obj.getClass().isArray()) {
            return DataArray.of(this, (Object[]) obj);
        }

        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(obj.getClass(), this);
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        DataArray dataArray = DataArray.of(this);
        for (FieldInfo fieldInfo : sortedFieldList) {
            Object val = fieldInfo.get(obj);
            if (val == null) {
                continue;
            }
            dataArray.add(toData(this, val));
        }
        return dataArray;
    }

    /**
     * 将java对象进行编码
     *
     * @param source
     * @return
     */
    S encode(Object source);

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    DataType getDataType();

    /**
     * 判断是否要载入转换器
     *
     * @return
     */
    boolean checkCanBeLoad();

    /**
     * 获取logger
     *
     * @return
     */
    default Logger getLogger() {
        return MapUtil.computeIfAbsent(CONVERTER_LOGGER_MAP, this, converter -> LoggerFactory.getLogger(converter.getClass()));
    }

    /**
     * 获取转换器名称
     *
     * @return
     */
    default String getConverterName() {
        return MapUtil.computeIfAbsent(CONVERTER_NAME_MAP, this, converter -> converter.getClass().getName());
    }

    /**
     * 打印转换器载入成功信息
     */
    default void logLoadSuccess() {
        getLogger().info("converter loaded success: " + this.getClass().getName());
    }

    /**
     * 打印转换器载入失败信息
     */
    default void logLoadFail() {
        getLogger().warn("converter loaded fail: " + this.getClass().getName());
    }

    /**
     * 载入数据转换接口
     *
     * @return
     */
    default boolean loadConverter() {
        return checkCanBeLoad() && ConverterSelector.register(this);
    }

    /**
     * 获取日期格式
     *
     * @return 日期格式化模板
     */
    default DateTimeFormatter getDateFormat() {
        DateTimeFormatter dateTimeFormatter = CONVERTER_DATE_TIME_FORMATTER_MAP.get(this);
        String dateFormatPattern = CONVERTER_DATE_FORMAT_PATTERN_MAP.get(this);
        if (dateTimeFormatter == null && dateFormatPattern != null) {
            dateTimeFormatter = this.generateDateFormat(dateFormatPattern);
            CONVERTER_DATE_TIME_FORMATTER_MAP.put(this, dateTimeFormatter);
        }
        return dateTimeFormatter;
    }

    /**
     * 设置日期格式
     *
     * @param dateFormatPattern 日期格式化模板字符
     */
    default void setDateFormat(String dateFormatPattern) {
        if (StringUtil.isBlank(dateFormatPattern)) {
            CONVERTER_DATE_FORMAT_PATTERN_MAP.remove(this);
        } else {
            CONVERTER_DATE_FORMAT_PATTERN_MAP.put(this, dateFormatPattern);
        }
        CONVERTER_DATE_TIME_FORMATTER_MAP.remove(this);
    }

    /**
     * 设置日期格式
     *
     * @param dateFormatter 日期格式化模板
     */
    default void setDateFormat(DateTimeFormatter dateFormatter) {
        if (dateFormatter == null) {
            CONVERTER_DATE_TIME_FORMATTER_MAP.remove(this);
        } else {
            CONVERTER_DATE_TIME_FORMATTER_MAP.put(this, dateFormatter);
        }
        CONVERTER_DATE_FORMAT_PATTERN_MAP.remove(this);
    }

    /**
     * 生成日期格式化模版
     *
     * @param dateFormatPattern 日期格式化模板字符
     * @return 日期格式化模板
     */
    default DateTimeFormatter generateDateFormat(String dateFormatPattern) {
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendPattern(dateFormatPattern).toFormatter(getLocale());
        dateTimeFormatter.withZone(getTimeZone().toZoneId());

        return dateTimeFormatter;
    }

    /**
     * 获取时区
     *
     * @return
     */
    default TimeZone getTimeZone() {
        return MapUtil.computeIfAbsent(CONVERTER_TIME_ZONE_MAP, this, converter -> TimeZone.getDefault());
    }

    /**
     * 设置时区
     *
     * @param timeZone
     */
    default void setTimeZone(TimeZone timeZone) {
        CONVERTER_TIME_ZONE_MAP.put(this, timeZone);
    }

    /**
     * 获取语言环境
     *
     * @return
     */
    default Locale getLocale() {
        return MapUtil.computeIfAbsent(CONVERTER_LOCALE_MAP, this, converter -> Locale.getDefault());
    }

    /**
     * 设置语言环境
     *
     * @param locale
     */
    default void setLocale(Locale locale) {
        CONVERTER_LOCALE_MAP.put(this, locale);
    }

    /**
     * 检查功能是否启用
     *
     * @param f 功能配置
     * @return
     */
    default boolean isEnabled(ConvertFeature f) {
        int features = MapUtil.computeIfAbsent(CONVERTER_FEATURES_MAP, this, converter -> getDefaultFeature());
        return (features & f.getMask()) != 0;
    }

    /**
     * 启用功能
     *
     * @param f 功能配置
     * @return
     */
    default Converter<S> enable(ConvertFeature f) {
        int features = MapUtil.computeIfAbsent(CONVERTER_FEATURES_MAP, this, converter -> getDefaultFeature());
        int newFeatures = features | f.getMask();
        CONVERTER_FEATURES_MAP.put(this, newFeatures);
        return this;
    }

    /**
     * 禁用功能
     *
     * @param f 功能配置
     * @return
     */
    default Converter<S> disable(ConvertFeature f) {
        int features = MapUtil.computeIfAbsent(CONVERTER_FEATURES_MAP, this, converter -> getDefaultFeature());
        int newFeatures = features & ~f.getMask();
        CONVERTER_FEATURES_MAP.put(this, newFeatures);
        return this;
    }
}