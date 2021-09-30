package com.chm.converter.spearal;

import com.chm.converter.core.Converter;
import com.chm.converter.core.DataType;

/**
 * spearal数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-29
 **/
public interface SpearalConverter extends Converter<byte[]> {

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.SPEARAL;
    }
}
