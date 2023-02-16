package com.chm.converter.jsonb;

import com.chm.converter.core.BytesConverter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;

/**
 * JSONB数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-07-06
 **/
public interface JsonbConverter extends BytesConverter {

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.JSONB;
    }

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link JsonbConverter}接口实例
     */
    static JsonbConverter select() {
        return ConverterSelector.select(JsonbConverter.class);
    }
}
