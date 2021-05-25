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
package com.chm.converter.auto;

import com.chm.converter.Converter;
import com.chm.converter.DataType;
import com.chm.converter.exceptions.ConvertException;
import com.chm.converter.utils.ReflectUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * @author caihongming
 * @version v1.0
 * @since 2020-12-31
 **/
public class DefaultAutoConverter implements Converter<Object> {

  @Override
  public <T> T convertToJavaObject(Object source, Class<T> targetType) {
    if (source instanceof InputStream
            || source instanceof byte[]
            || source instanceof File) {
      return tryConvert(source, targetType, DataType.BINARY);
    }
    T result = null;
    if (source instanceof CharSequence) {
      String str = source.toString();
      if (String.class.isAssignableFrom(targetType)) {
        return (T) str;
      }
      String trimmedStr = str.trim();
      char ch = trimmedStr.charAt(0);
      try {
        if (ch == '{' || ch == '[') {
          result = tryConvert(trimmedStr, targetType, DataType.JSON);
        } else if (ch == '<') {
          result = tryConvert(trimmedStr, targetType, DataType.XML);
        } else if (Character.isDigit(ch)) {
          try {
            result = tryConvert(trimmedStr, targetType, DataType.JSON);
          } catch (Throwable th) {
            result = tryConvert(source, targetType, DataType.TEXT);
          }
        } else if ("true".equalsIgnoreCase(trimmedStr)) {
          if (boolean.class.isAssignableFrom(targetType) || Boolean.class.isAssignableFrom(targetType)) {
            result = (T) Boolean.TRUE;
          } else  {
            result = tryConvert(trimmedStr, targetType, DataType.TEXT);
          }
        } else if ("false".equalsIgnoreCase(trimmedStr)) {
          if (boolean.class.isAssignableFrom(targetType) || Boolean.class.isAssignableFrom(targetType)) {
            result = (T) Boolean.FALSE;
          } else {
            result = tryConvert(trimmedStr, targetType, DataType.TEXT);
          }
        } else {
          result = tryConvert(source, targetType, DataType.TEXT);
        }
      } catch (Throwable th) {
        try {
          result = tryConvert(trimmedStr, targetType, DataType.TEXT);
        } catch (Throwable th2) {
          throw new ConvertException("auto", th2);
        }
      }
    }
    return result;
  }

  private <T> T tryConvert(Object source, Class<T> targetType, DataType dataType) {
    return (T) DataType.getConverterMap().get(dataType).convertToJavaObject(source, targetType);
  }

  private <T> T tryConvert(Object source, Type targetType, DataType dataType) {
    return (T) DataType.getConverterMap().get(dataType).convertToJavaObject(source, targetType);
  }


  @Override
  public <T> T convertToJavaObject(Object source, Type targetType) {
    if (source instanceof InputStream
            || source instanceof byte[]
            || source instanceof File) {
      return tryConvert(source, targetType, DataType.BINARY);
    }
    T result = null;
    Class<?> clazz = ReflectUtils.getClassByType(targetType);
    if (source instanceof CharSequence) {
      String str = source.toString();
      if (String.class.isAssignableFrom(clazz)) {
        return (T) str;
      }
      String trimmedStr = str.trim();
      char ch = trimmedStr.charAt(0);
      try {
        if (ch == '{' || ch == '[') {
          result = tryConvert(trimmedStr, targetType, DataType.JSON);
        } else if (ch == '<') {
          result = tryConvert(trimmedStr, targetType, DataType.XML);
        } else if (Character.isDigit(ch)) {
          try {
            result = tryConvert(trimmedStr, targetType, DataType.JSON);
          } catch (Throwable th) {
            result = tryConvert(source, targetType, DataType.TEXT);
          }
        }  else if ("true".equalsIgnoreCase(trimmedStr)) {
          if (boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)) {
            result = (T) Boolean.TRUE;
          } else {
            result = tryConvert(trimmedStr, targetType, DataType.TEXT);
          }
        } else if ("false".equalsIgnoreCase(trimmedStr)) {
          if (boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)) {
            result = (T) Boolean.FALSE;
          } else {
            result = tryConvert(trimmedStr, targetType, DataType.TEXT);
          }
        } else {
          result = tryConvert(source, targetType, DataType.TEXT);
        }
      } catch (Throwable th) {
        try {
          result = tryConvert(trimmedStr, targetType, DataType.TEXT);
        } catch (Throwable th2) {
          throw new ConvertException("auto", th2);
        }
      }
    }
    return result;
  }

}
