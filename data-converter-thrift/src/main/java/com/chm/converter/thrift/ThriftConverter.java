package com.chm.converter.thrift;

import com.chm.converter.core.BytesConverter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;

/**
 * thrift数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @date 2021-09-30
 **/
public interface ThriftConverter extends BytesConverter {

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.THRIFT_BINARY;
    }

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link ThriftConverter}接口实例
     */
    static ThriftConverter select() {
        return (ThriftConverter) ConverterSelector.select(DataType.THRIFT_BINARY);
    }
}
