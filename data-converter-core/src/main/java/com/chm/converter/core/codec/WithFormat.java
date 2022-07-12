package com.chm.converter.core.codec;

import java.time.format.DateTimeFormatter;

/**
 * 格式化接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-07-01
 **/
public interface WithFormat {

    /**
     * 修改日期格式
     *
     * @param datePattern
     * @return FormatInterface
     */
    WithFormat withDatePattern(String datePattern);

    /**
     * 修改日期格式
     *
     * @param dateFormatter
     * @return FormatInterface
     */
    WithFormat withDateFormatter(DateTimeFormatter dateFormatter);
}
