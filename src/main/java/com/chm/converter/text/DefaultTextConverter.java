/*
 * Copyright (C) 2011-2021 ShenZhen iBOXCHAIN Information Technology Co.,Ltd.
 *
 * All right reserved.
 *
 * This software is the confidential and proprietary
 * information of iBOXCHAIN Company of China.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with iBOXCHAIN inc.
 *
 */
package com.chm.converter.text;

import com.chm.converter.Converter;

import java.lang.reflect.Type;

/**
 * 默认的文本数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
public class DefaultTextConverter implements Converter<String> {

  @Override
  public <T> T convertToJavaObject(String source, Class<T> targetType) {
    return (T) source;
  }

  @Override
  public <T> T convertToJavaObject(String source, Type targetType) {
    return (T) source;
  }
}
