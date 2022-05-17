package com.chm.converter.xml;


import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.reflect.TypeToken;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Xml数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
public interface XmlConverter extends Converter<String> {

    /**
     * 将源对象转换为Map对象
     *
     * @param obj 源对象
     * @return 转换后的Map对象
     */
    @Override
    default Map<String, Object> convertObjectToMap(Object obj) {
        if (obj instanceof CharSequence) {
            return convertToJavaObject(obj.toString(), new TypeToken<LinkedHashMap<String, Object>>() {
            });
        }
        return Converter.super.convertObjectToMap(obj);
    }

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
        return (XmlConverter) ConverterSelector.select(DataType.XML);
    }
}
