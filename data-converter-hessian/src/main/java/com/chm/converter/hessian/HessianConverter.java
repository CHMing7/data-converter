package com.chm.converter.hessian;

import com.chm.converter.core.BytesConverter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;

/**
 * hessian数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-17
 **/
public interface HessianConverter extends BytesConverter {

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.HESSIAN;
    }

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link HessianConverter}接口实例
     */
    static HessianConverter select() {
        return ConverterSelector.select(HessianConverter.class);
    }
}
