package com.chm.converter.yaml;

import com.chm.converter.core.Converter;
import com.chm.converter.core.DataType;

/**
 * Yaml数据转换接口
 * @author caihongming
 * @version v1.0
 * @since 2022-01-13
 **/
public interface YamlConverter extends Converter<String> {

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.YAML;
    }
}
