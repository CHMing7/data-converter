package com.chm.converter.protobuf;

import com.chm.converter.core.Converter;
import com.chm.converter.core.DataType;

import java.nio.ByteBuffer;

/**
 * Protobuf数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-13
 **/
public interface ProtobufConverter extends Converter<ByteBuffer> {

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.PROTOBUF;
    }
}
