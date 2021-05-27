package com.chm.converter.utils;


import cn.hutool.core.util.StrUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
public final class NameUtils {

    /**
     * 按驼峰命名法的规则将字符串分割
     *
     * @param name 源字符串
     * @return 分割后的字符串数组
     */
    public static String[] splitCamelName(String name) {
        int len = name.length();
        List<String> names = new LinkedList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char ch = name.charAt(i);
            if (Character.isUpperCase(ch)) {
                String item = builder.toString();
                if (StrUtil.isNotBlank(item)) {
                    names.add(item);
                }
                builder = new StringBuilder();
                ch = Character.toLowerCase(ch);
            }
            builder.append(ch);
        }
        String last = builder.toString();
        if (StrUtil.isNotBlank(last)) {
            names.add(last);
        }
        String[] result = new String[names.size()];
        return names.toArray(result);
    }

    public static String setterName(String name) {
        String[] strs = splitCamelName(name);
        String prefix = strs[0];
        if ("set".equals(prefix)) {
            return name;
        } else if ("is".equals(prefix)) {
            StringBuilder builder = new StringBuilder("set");
            for (int i = 1; i < strs.length; i++) {
                String str = strs[i];
                builder.append(Character.toUpperCase(str.charAt(0))).append(str.substring(1));
            }
            return builder.toString();
        } else {
            StringBuilder builder = new StringBuilder("set");
            for (String str : strs) {
                builder.append(Character.toUpperCase(str.charAt(0))).append(str.substring(1));
            }
            return builder.toString();
        }
    }
}
