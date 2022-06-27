package com.chm.converter.yaml;

import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;

/**
 * Yaml数据转换接口
 *
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

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link YamlConverter}接口实例
     */
    static YamlConverter select() {
        return ConverterSelector.select(YamlConverter.class);
    }
}
