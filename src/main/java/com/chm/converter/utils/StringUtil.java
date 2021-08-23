package com.chm.converter.utils;

import com.chm.converter.utils.formatter.Constant;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-15
 **/
public final class StringUtil {


    /**
     * 判断是否为空字符串
     *
     * @param str 字符串
     * @return boolean 如果为空，则返回true
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否非空
     *
     * @param str 如果不为空，则返回true
     * @return boolean
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白
     *
     * @param str 被检测的字符串
     * @return boolean
     */
    public static boolean isBlank(CharSequence str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            // 只要有一个非空字符即为非空字符串
            if (!isBlankChar(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否为非空白
     *
     * @param str 被检测的字符串
     * @return boolean
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * 是否空白符<br>
     * 空白符包括空格、制表符、全角空格和不间断空格<br>
     *
     * @param c 字符
     * @return 是否空白符
     * @see Character#isWhitespace(int)
     * @see Character#isSpaceChar(int)
     * @since 4.0.10
     */
    public static boolean isBlankChar(char c) {
        return isBlankChar((int) c);
    }

    /**
     * 是否空白符<br>
     * 空白符包括空格、制表符、全角空格和不间断空格<br>
     *
     * @param c 字符
     * @return 是否空白符
     * @see Character#isWhitespace(int)
     * @see Character#isSpaceChar(int)
     * @since 4.0.10
     */
    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c)
                || Character.isSpaceChar(c)
                || c == '\ufeff'
                || c == '\u202a'
                || c == '\u0000';
    }

    /**
     * 是否纯数字
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        return Constant.NUMERIC_REGEX.matcher(str).matches();
    }

    /**
     * 判断字符串是否以字母开头
     *
     * @param str 如果不为空，则返回false
     * @return boolean
     */
    public static boolean isStartWithWord(String str) {
        if (str == null) {
            return false;
        }
        return Constant.START_WITH_WORD_REGEX.matcher(str).matches();
    }

    /**
     * 计算字符出现次数
     *
     * @param str    如果不为空，则返回0
     * @param target 需要计算的字符串
     * @return 出现次数
     */
    public static int countWord(String str, String target) {
        if (str == null) {
            return 0;
        }
        int len1 = str.length();
        int len2 = str.replace(target, "").length();
        return (len1 - len2);
    }

    /**
     * 判断字符串是包含中文
     *
     * @param str 如果不为空，则返回false
     * @return boolean
     */
    public static boolean hasChinese(String str) {
        if (str == null) {
            return false;
        }
        return Constant.CHINESE_REGEX.matcher(str).find();
    }
}
