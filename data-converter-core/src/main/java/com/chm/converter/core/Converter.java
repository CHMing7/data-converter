package com.chm.converter.core;

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

    Map<Converter<?>, String> CONVERTER_DATE_FORMAT_PATTERN_MAP = new ConcurrentHashMap<>();

    Map<Converter<?>, DateTimeFormatter> CONVERTER_DATE_TIME_FORMATTER_MAP = new ConcurrentHashMap<>();

    Map<Converter<?>, TimeZone> CONVERTER_TIME_ZONE_MAP = new ConcurrentHashMap<>();

    Map<Converter<?>, Locale> CONVERTER_LOCALE_MAP = new ConcurrentHashMap<>();

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
     * 将java对象进行编码
     *
     * @param obj
     * @return
     */
    S encode(Object obj);

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
     * 载入数据转换接口
     *
     * @return
     */
    default boolean loadConverter() {
        if (checkCanBeLoad()) {
            return ConverterSelector.put(this);
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

}