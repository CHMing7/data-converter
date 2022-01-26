package com.chm.converter.core;

import com.chm.converter.core.cfg.ConvertFeature;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-08-13
 **/
public interface Converter<S> {

    Map<Converter<?>, Logger> CONVERTER_LOGGER_MAP = new ConcurrentHashMap<>();

    Map<Converter<?>, String> CONVERTER_NAME_MAP = new ConcurrentHashMap<>();

    Map<Converter<?>, String> CONVERTER_DATE_FORMAT_PATTERN_MAP = new ConcurrentHashMap<>();

    Map<Converter<?>, DateTimeFormatter> CONVERTER_DATE_TIME_FORMATTER_MAP = new ConcurrentHashMap<>();

    Map<Converter<?>, TimeZone> CONVERTER_TIME_ZONE_MAP = new ConcurrentHashMap<>();

    Map<Converter<?>, Locale> CONVERTER_LOCALE_MAP = new ConcurrentHashMap<>();

    Map<Converter<?>, Integer> CONVERTER_FEATURES_MAP = new ConcurrentHashMap<>();

    /**
     * 将源数据转换为目标类型（Class）的java对象
     *
     * @param source     源数据
     * @param targetType 目标类型 (Class对象)
     * @param <T>        目标类型泛型
     * @return 转换后的目标类型对象
     */
    <T> T convertToJavaObject(S source, Class<T> targetType);

    /**
     * 将源数据转换为目标类型（Type）的java对象
     *
     * @param source     源数据
     * @param targetType 目标类型 (Type对象)
     * @param <T>        目标类型泛型
     * @return 转换后的目标类型对象
     */
    <T> T convertToJavaObject(S source, Type targetType);

    /**
     * 将源数据转换为目标类型（Type）的java对象
     *
     * @param source     源数据
     * @param targetType 目标类型 (Type对象)
     * @param <T>        目标类型泛型
     * @return 转换后的目标类型对象
     */
    default <T> T convertToJavaObject(S source, TypeToken<T> targetType) {
        return convertToJavaObject(source, targetType.getType());
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
        if (!CONVERTER_LOGGER_MAP.containsKey(this)) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            CONVERTER_LOGGER_MAP.put(this, logger);
        }
        return CONVERTER_LOGGER_MAP.get(this);
    }

    /**
     * 获取转换器名称
     *
     * @return
     */
    default String getConverterName() {
        if (!CONVERTER_NAME_MAP.containsKey(this)) {
            String converterName = this.getClass().getName();
            CONVERTER_NAME_MAP.put(this, converterName);
        }
        return CONVERTER_NAME_MAP.get(this);
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
        if (checkCanBeLoad()) {
            logLoadSuccess();
            return ConverterSelector.register(this);
        } else {
            logLoadFail();
        }
        return false;
    }

    /**
     * 设置日期格式
     *
     * @param dateFormatPattern 日期格式化模板字符
     * @return
     */
    default void setDateFormat(String dateFormatPattern) {
        CONVERTER_DATE_FORMAT_PATTERN_MAP.put(this, dateFormatPattern);
        CONVERTER_DATE_TIME_FORMATTER_MAP.remove(this);
    }

    /**
     * 设置日期格式
     *
     * @param dateFormatter 日期格式化模板字符
     * @return
     */
    default void setDateFormat(DateTimeFormatter dateFormatter) {
        CONVERTER_DATE_FORMAT_PATTERN_MAP.remove(this);
        CONVERTER_DATE_TIME_FORMATTER_MAP.put(this, dateFormatter);
    }

    /**
     * 获取日期格式
     *
     * @return 日期格式化模板字符
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
     * 生成日期格式化模版
     *
     * @param dateFormatPattern
     * @return
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
        if (!CONVERTER_TIME_ZONE_MAP.containsKey(this)) {
            CONVERTER_TIME_ZONE_MAP.put(this, TimeZone.getDefault());
        }
        return CONVERTER_TIME_ZONE_MAP.get(this);
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
        if (!CONVERTER_LOCALE_MAP.containsKey(this)) {
            CONVERTER_LOCALE_MAP.put(this, Locale.getDefault());
        }
        return CONVERTER_LOCALE_MAP.get(this);
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
}