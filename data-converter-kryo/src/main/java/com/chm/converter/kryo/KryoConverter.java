package com.chm.converter.kryo;

import com.chm.converter.core.Converter;
import com.chm.converter.core.DataType;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-26
 **/
public interface KryoConverter extends Converter<byte[]> {

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.KRYO;
    }
}
