package com.chm.converter.jackson;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.utils.MapUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;

import java.util.Map;

/**
 * 属性名称转换类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-08
 **/
public class PropertyNameTransformer extends NameTransformer {

    private static final Map<Class<?>, PropertyNameTransformer> CLASS_PROPERTY_NAME_TRANSFORMER_MAP = MapUtil.newConcurrentHashMap();

    private final Map<String, FieldInfo> fieldInfoMap;

    private final Map<String, FieldInfo> nameFieldInfoMap;

    private PropertyNameTransformer(Class<?> clazz, Class<? extends Converter> converterClass) {
        this.fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(clazz, converterClass);
        this.nameFieldInfoMap = ClassInfoStorage.INSTANCE.getNameFieldInfoMap(clazz, converterClass);
    }

    public static PropertyNameTransformer get(Class<?> clazz, Class<? extends Converter> converterClass) {
        if (!CLASS_PROPERTY_NAME_TRANSFORMER_MAP.containsKey(clazz)) {
            CLASS_PROPERTY_NAME_TRANSFORMER_MAP.put(clazz, new PropertyNameTransformer(clazz, converterClass));
        }
        return CLASS_PROPERTY_NAME_TRANSFORMER_MAP.get(clazz);
    }

    @Override
    public String transform(String name) {
        // 返回新属性名称
        FieldInfo fieldInfo = fieldInfoMap.get(name);
        return fieldInfo != null ? fieldInfo.getName() : name;
    }

    @Override
    public String reverse(String transformed) {
        // 返回新属性名称
        FieldInfo fieldInfo = nameFieldInfoMap.get(transformed);
        return fieldInfo != null ? fieldInfo.getFieldName() : transformed;
    }
}
