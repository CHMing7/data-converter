package com.chm.converter.utils.formatter;

import java.util.regex.Pattern;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-15
 **/
public interface Constant {

    //===========================正则定义============================

    /**
     * 纯数字
     */
    Pattern NUMERIC_REGEX = Pattern.compile("[0-9]+");

    /**
     * 字母开头
     */
    Pattern START_WITH_WORD_REGEX = Pattern.compile("^[A-Za-z].*");

    /**
     * 中文
     */
    Pattern CHINESE_REGEX = Pattern.compile("[\u4E00-\u9FFF]");

    //===========================异常定义============================
    /**
     * 解析日期时异常
     */
    public static final String PARSE_LOCAL_DATE_EXCEPTION = "Unable to obtain";
}
