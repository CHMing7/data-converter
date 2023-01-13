package com.chm.converter.fst;

import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;

/**
 * fst数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-27
 **/
public interface FstConverter extends Converter<byte[]> {

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link FstConverter}接口实例
     */
    static FstConverter select() {
        return ConverterSelector.select(FstConverter.class);
    }

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.FST;
    }
}
