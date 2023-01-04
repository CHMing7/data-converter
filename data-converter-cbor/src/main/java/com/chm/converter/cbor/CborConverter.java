package com.chm.converter.cbor;

import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;

/**
 * cbor数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-03-08
 **/
public interface CborConverter extends Converter<byte[]> {

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link CborConverter}接口实例
     */
    static CborConverter select() {
        return ConverterSelector.select(CborConverter.class);
    }

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.CBOR;
    }
}

