/*
 * Copyright (C) 2011-2020 ShenZhen iBOXCHAIN Information Technology Co.,Ltd.
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
package com.chm.converter;

import java.lang.reflect.Type;

/**
 * Mbp的数据转换器
 * 转换器包含序列化以及反序列化过程
 *
 * @author caihongming
 * @version v1.0
 * @since 2020-12-31
 **/
public interface Converter<S> {

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
}
