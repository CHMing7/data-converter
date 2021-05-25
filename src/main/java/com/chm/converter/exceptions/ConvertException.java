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
package com.chm.converter.exceptions;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
public class ConvertException extends RuntimeException {

  public ConvertException(String converterName, Throwable th) {
    super("Converter '" + converterName + "' Error: " + th.getMessage(), th);
  }
}
