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

/**
 * @author caihongming
 * @version v1.0
 * @since 2020-12-31
 **/
public interface Encoder {

  /**
   * 将java对象转化成字符串
   *
   * @param obj
   * @return
   */
  String encodeToString(Object obj);
}
