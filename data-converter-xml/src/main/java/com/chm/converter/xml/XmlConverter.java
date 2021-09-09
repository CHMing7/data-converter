package com.chm.converter.xml;


import com.chm.converter.core.Converter;
import com.chm.converter.core.DataType;

/**
 * Xml消息转化接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
public interface XmlConverter extends Converter<String> {

    /**
     * 获取当前数据转换器转换类型
     *
     * @return
     */
    @Override
    default DataType getDataType() {
        return DataType.XML;
    }
}
