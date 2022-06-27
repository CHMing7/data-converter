package com.chm.converter.xml;


import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;

/**
 * Xml数据转换接口
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

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link XmlConverter}接口实例
     */
    static XmlConverter select() {
        return ConverterSelector.select(XmlConverter.class);
    }
}
