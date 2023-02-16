package com.chm.converter.ion;

import com.chm.converter.core.BytesConverter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;

/**
 * ion数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-05-17
 **/
public interface IonConverter extends BytesConverter {

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link IonConverter}接口实例
     */
    static IonConverter select() {
        return ConverterSelector.select(IonConverter.class);
    }

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.ION;
    }
}
