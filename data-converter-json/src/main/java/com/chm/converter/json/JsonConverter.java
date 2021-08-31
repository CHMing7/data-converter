package com.chm.converter.json;

import com.chm.converter.core.Converter;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JSON数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
public interface JsonConverter extends Converter<String> {

    Map<JsonConverter, String> JSON_CONVERTER_DATE_FORMAT_PATTERN_MAP = new ConcurrentHashMap<>();

    Map<JsonConverter, DateTimeFormatter> JSON_CONVERTER_DATE_TIME_FORMATTER_MAP = new ConcurrentHashMap<>();

    Map<JsonConverter, TimeZone> JSON_CONVERTER_TIME_ZONE_MAP = new ConcurrentHashMap<>();

    Map<JsonConverter, Locale> JSON_CONVERTER_LOCALE_MAP = new ConcurrentHashMap<>();

    /**
     * 将源对象转换为Map对象
     *
     * @param obj 源对象
     * @return 转换后的Map对象
     */
    Map<String, Object> convertObjectToMap(Object obj);

    /**
     * 设置日期格式
     *
     * @param dateFormatPattern 日期格式化模板字符
     * @return
     */
    default void setDateFormat(String dateFormatPattern) {
        JSON_CONVERTER_DATE_FORMAT_PATTERN_MAP.put(this, dateFormatPattern);
        JSON_CONVERTER_DATE_TIME_FORMATTER_MAP.remove(this);
    }

    /**
     * 设置日期格式
     *
     * @param dateFormatter 日期格式化模板字符
     * @return
     */
    default void setDateFormat(DateTimeFormatter dateFormatter) {
        JSON_CONVERTER_DATE_FORMAT_PATTERN_MAP.remove(this);
        JSON_CONVERTER_DATE_TIME_FORMATTER_MAP.put(this, dateFormatter);
    }

    /**
     * 获取日期格式
     *
     * @return 日期格式化模板字符
     */
    default DateTimeFormatter getDateFormat() {
        DateTimeFormatter dateTimeFormatter = JSON_CONVERTER_DATE_TIME_FORMATTER_MAP.get(this);
        String dateFormatPattern = JSON_CONVERTER_DATE_FORMAT_PATTERN_MAP.get(this);
        if (dateTimeFormatter == null && dateFormatPattern != null) {
            dateTimeFormatter = this.generateDateFormat(dateFormatPattern);
            JSON_CONVERTER_DATE_TIME_FORMATTER_MAP.put(this, dateTimeFormatter);
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
        if (!JSON_CONVERTER_TIME_ZONE_MAP.containsKey(this)) {
            JSON_CONVERTER_TIME_ZONE_MAP.put(this, TimeZone.getDefault());
        }
        return JSON_CONVERTER_TIME_ZONE_MAP.get(this);
    }

    /**
     * 设置时区
     *
     * @param timeZone
     */
    default void setTimeZone(TimeZone timeZone) {
        JSON_CONVERTER_TIME_ZONE_MAP.put(this, timeZone);
    }

    /**
     * 获取语言环境
     *
     * @return
     */
    default Locale getLocale() {
        if (!JSON_CONVERTER_LOCALE_MAP.containsKey(this)) {
            JSON_CONVERTER_LOCALE_MAP.put(this, Locale.getDefault());
        }
        return JSON_CONVERTER_LOCALE_MAP.get(this);
    }

    /**
     * 设置语言环境
     *
     * @param locale
     */
    default void setLocale(Locale locale) {
        JSON_CONVERTER_LOCALE_MAP.put(this, locale);
    }

    /**
     * 载入json数据转换接口
     */
    void loadJsonConverter();
}
