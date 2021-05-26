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
package com.chm.converter.json;

import cn.hutool.core.map.MapUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON转换器选择策略
 * 此类负责选择对应的可用JSON转转器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
public class JsonConverterSelector implements Serializable {

  public static final String FAST_JSON_NAME = "com.alibaba.fastjson.JSON";
  public static final String JACKSON_NAME = "com.fasterxml.jackson.databind.ObjectMapper";
  public static final String GSON_NAME = "com.google.gson.JsonParser";

  private static final Map<Class<? extends JsonConverter>, JsonConverter> JSON_CONVERTER_MAP = new HashMap<>();

  private JsonConverterSelector() {
  }

  static {
    try {
      checkFastJSONClass();
      JSON_CONVERTER_MAP.put(FastjsonConverter.class, new FastjsonConverter());
    } catch (Throwable ignored) {
    }
    try {
      checkJacsonClass();
      JSON_CONVERTER_MAP.put(JacksonConverter.class, new JacksonConverter());
    } catch (Throwable ignored) {
    }
    try {
      checkGsonClass();
      JSON_CONVERTER_MAP.put(GsonConverter.class, new GsonConverter());
    } catch (Throwable ignored) {
    }
  }

  /**
   * 检测FastJSON相关类型
   *
   * @return FastJSON相关类型
   */
  public static Class<?> checkFastJSONClass() throws Throwable {
    return Class.forName(FAST_JSON_NAME);
  }

  /**
   * 检测Jaskon相关类型
   *
   * @return Jaskon相关类型
   */
  public static Class<?> checkJacsonClass() throws Throwable {
    return Class.forName(JACKSON_NAME);
  }

  /**
   * 检测Gson相关类型
   *
   * @return Gson相关类型
   */
  public static Class<?> checkGsonClass() throws Throwable {
    return Class.forName(GSON_NAME);
  }

  /**
   * 选择Forest的JSON转换器
   * <p>从FastJson、Jackson以及Gson中动态选择一个可用的JSON转换器</p>
   *
   * @return Forest的JSON转换器，{@link JsonConverter}接口实例
   */
  public static JsonConverter select() {
    return MapUtil.isNotEmpty(JSON_CONVERTER_MAP) ? JSON_CONVERTER_MAP.values().stream().findFirst().orElse(null) : null;
  }

  /**
   * 选择Forest的JSON转换器
   * <p>从FastJson、Jackson以及Gson中动态选择一个可用的JSON转换器</p>
   *
   * @return Forest的JSON转换器，{@link JsonConverter}接口实例
   */
  public JsonConverter select(Class<? extends JsonConverter> className) {
    return JSON_CONVERTER_MAP.get(className);
  }

  public static void put(Class<? extends JsonConverter> className, JsonConverter jsonConverter) {
    JSON_CONVERTER_MAP.put(className, jsonConverter);
  }
}
