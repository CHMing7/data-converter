package com.chm.converter.avro;

import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;

/**
 * avro数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-16
 **/
public interface AvroConverter extends Converter<byte[]> {

    /**
     * 选择Avro数据转换器
     * <p>动态选择一个可用的Avro数据转换器</p>
     *
     * @return Avro数据转换器，{@link AvroConverter}接口实例
     */
    static AvroConverter select() {
        return ConverterSelector.select(AvroConverter.class);
    }

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.AVRO_BINARY;
    }
}
