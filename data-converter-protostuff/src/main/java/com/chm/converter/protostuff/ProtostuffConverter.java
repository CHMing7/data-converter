package com.chm.converter.protostuff;

import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;

/**
 * Protostuff数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-15
 **/
public interface ProtostuffConverter extends Converter<byte[]> {

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.PROTOSTUFF;
    }

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link ProtostuffConverter}接口实例
     */
    static ProtostuffConverter select() {
        return ConverterSelector.select(ProtostuffConverter.class);
    }
}
