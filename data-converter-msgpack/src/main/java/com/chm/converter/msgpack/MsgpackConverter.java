package com.chm.converter.msgpack;

import com.chm.converter.core.Converter;
import com.chm.converter.core.DataType;

/**
 * msgpack数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-28
 **/
public interface MsgpackConverter extends Converter<byte[]> {

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.MSGPACK;
    }
}
