package com.chm.converter.spearal;

import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
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

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link SpearalConverter}接口实例
     */
    static SpearalConverter select() {
        return ConverterSelector.select(SpearalConverter.class);
    }
}
