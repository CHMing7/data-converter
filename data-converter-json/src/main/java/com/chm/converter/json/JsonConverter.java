package com.chm.converter.json;


import com.chm.converter.core.Converter;
import com.chm.converter.core.Encoder;

import java.util.Map;

/**
 * JSON数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
public interface JsonConverter extends Converter<String>, Encoder {

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
     * @param format 日期格式化模板字符
     * @return {@link Converter}接口实例
     */
    void setDateFormat(String format);

    /**
     * 获取日期格式
     *
     * @return 日期格式化模板字符
     */
    String getDateFormat();

    /**
     * 载入json数据转换接口
     */
    void loadJsonConverter();
}
