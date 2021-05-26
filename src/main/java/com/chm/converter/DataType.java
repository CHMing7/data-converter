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


import cn.hutool.core.util.StrUtil;
import com.chm.converter.auto.DefaultAutoConverter;
import com.chm.converter.binary.DefaultBinaryConverter;
import com.chm.converter.json.JsonConverterSelector;
import com.chm.converter.text.DefaultTextConverter;
import com.chm.converter.xml.JaxbConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 数据类型封装类型
 *
 * @author caihongming
 * @version v1.0
 * @since 2020-12-31
 **/
public class DataType {

  /**
   * 数据类型表
   * <p>所有在Mbp中创建的数据类型对象都会放入这个哈希表中</p>
   *
   * @since 1.5.0-BETA4
   */
  public final static Map<String, DataType> DATA_TYPES = new HashMap<>();

  public static Map<String, DataType> getDataTypes() {
    return DATA_TYPES;
  }

  /**
   * 数据类型： 自动类型
   */
  public final static DataType AUTO = DataType.createDataType("auto");

  /**
   * 数据类型： 文本类型
   */
  public final static DataType TEXT = DataType.createDataType("text");

  /**
   * 数据类型： JSON类型
   */
  public final static DataType JSON = DataType.createDataType("json");

  /**
   * 数据类型： XML类型
   */
  public final static DataType XML = DataType.createDataType("xml");

  /**
   * 数据类型： 二进制类型
   */
  public final static DataType BINARY = DataType.createDataType("binary");

  /**
   * 全局数据转换器表
   */
  private final static Map<DataType, Converter> CONVERTER_MAP = new HashMap<>();

  public static Map<DataType, Converter> getConverterMap() {
    return CONVERTER_MAP;
  }

  static {
    CONVERTER_MAP.put(DataType.AUTO, new DefaultAutoConverter());
    CONVERTER_MAP.put(DataType.BINARY, new DefaultBinaryConverter());
    CONVERTER_MAP.put(DataType.TEXT, new DefaultTextConverter());
    CONVERTER_MAP.put(DataType.XML, new JaxbConverter());
    CONVERTER_MAP.put(DataType.JSON, JsonConverterSelector.select());
  }

  /**
   * 数据类型名称
   */
  private final String name;

  /**
   * 创建新的数据类型
   *
   * @param name Data type name
   * @return New instance of {@code com.dtflys.forest.utils.ForestDataType}
   * @since 1.5.0-BETA4
   */
  public static DataType createDataType(String name) {
    if (StrUtil.isEmpty(name)) {
      throw new RuntimeException("Data type name cannot be empty!");
    }
    name = name.toLowerCase();
    DataType dataType = new DataType(name);
    if (DATA_TYPES.containsKey(name)) {
      throw new RuntimeException("Data type '" + name + "' has already been existed!");
    }
    DATA_TYPES.put(name, dataType);
    return dataType;
  }

  /**
   * 数据类型构造函数
   * <p>该构造函数为私有方法，外部代码不能直接通过new ForestDataType(name)进行创建数据类型对象</p>
   * <p>需要通过静态方法ForestDataType.createDataType或ForestDataType.findOrCreateDataType进行创建</p>
   *
   * @param name Date type name
   * @since 1.5.0-BETA4
   */
  private DataType(String name) {
    this.name = name;
  }

  /**
   * 获取数据类型名称
   *
   * @return Name of this data type
   */
  public String getName() {
    return name;
  }

  /**
   * Find data type object by data type name
   *
   * @param name Data type name
   * @return Instance of {@code com.dtflys.forest.utils.ForestDataType}
   * @since 1.5.0-BETA4
   */
  public static DataType findByName(String name) {
    return DATA_TYPES.get(name.toLowerCase());
  }

  /**
   * Find or create a data type
   *
   * @param name Data type name
   * @return Instance of {@code com.dtflys.forest.utils.ForestDataType}
   * @since 1.5.0-BETA4
   */
  public static DataType findOrCreateDataType(String name) {
    if (StrUtil.isEmpty(name)) {
      return null;
    }
    name = name.toLowerCase();
    DataType dataType = DATA_TYPES.get(name);
    if (dataType == null) {
      dataType = createDataType(name);
    }
    return dataType;
  }

  /**
   * 重载equals方法
   *
   * @param o 相比较的对象
   * @return {@code true}：相同对象; {@code false}：不同对象
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DataType)) {
      return false;
    }
    DataType that = (DataType) o;
    return Objects.equals(getName(), that.getName());
  }

  /**
   * 重载HashCode
   *
   * @return 哈希值
   */
  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }
}
