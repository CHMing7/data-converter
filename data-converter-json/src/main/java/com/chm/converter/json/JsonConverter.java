package com.chm.converter.json;

import com.chm.converter.core.Converter;
import com.chm.converter.core.DataType;
import com.chm.converter.core.reflect.TypeToken;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JSON数据转换接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
public interface JsonConverter extends Converter<String> {

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
        return DataType.JSON;
    }
}
