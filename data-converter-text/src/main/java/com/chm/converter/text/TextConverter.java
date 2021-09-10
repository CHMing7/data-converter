package com.chm.converter.text;

import com.chm.converter.core.Converter;
import com.chm.converter.core.DataType;

/**
 * 文本数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-10
 **/
public interface TextConverter extends Converter<String> {

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.TEXT;
    }
}
