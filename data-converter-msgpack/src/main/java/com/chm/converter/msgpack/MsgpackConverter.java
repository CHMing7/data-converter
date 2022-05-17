package com.chm.converter.msgpack;

import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
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

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link MsgpackConverter}接口实例
     */
    static MsgpackConverter select() {
        return (MsgpackConverter) ConverterSelector.select(DataType.MSGPACK);
    }
}
