package com.chm.converter.smile;

import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;

/**
 * smile数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-05-17
 **/
public interface SmileConverter extends Converter<byte[]> {

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.SMILE;
    }

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link SmileConverter}接口实例
     */
    static SmileConverter select() {
        return (SmileConverter) ConverterSelector.select(DataType.SMILE);
    }
}