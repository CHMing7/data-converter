package com.chm.converter.json.jackson.deserializer;

import cn.hutool.core.collection.CollectionUtil;
import com.chm.converter.ClassInfoStorage;
import com.chm.converter.FieldInfo;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-17
 **/
public class JacksonBeanDeserializerModifier extends BeanDeserializerModifier {

    @Override
    public List<BeanPropertyDefinition> updateProperties(DeserializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> propDefs) {
        List<BeanPropertyDefinition> resultList = new LinkedList<>();
        Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldInfoMap(beanDesc.getBeanClass());
        propDefs.forEach(beanPropertyDefinition -> {
            // 去除不反序列化的属性
            String fieldName = beanPropertyDefinition.getName();
            FieldInfo fieldInfo = fieldInfoMap.get(fieldName);
            if (!fieldInfo.isDeserialize()) {
                // 去除不序列化的属性
                return;
            }
            // 修改key值
            BeanPropertyDefinition newProperty = beanPropertyDefinition.withSimpleName(fieldInfo.getName());
            resultList.add(newProperty);
        });
        return resultList;
    }

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
        Iterator<SettableBeanProperty> properties = builder.getProperties();
        Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldInfoMap(beanDesc.getBeanClass());
        CollectionUtil.forEach(properties, (property, index) -> {
            FieldInfo fieldInfo = fieldInfoMap.get(property.getName());
            // 修改时间类型反序列化类
            JsonDeserializer<Object> dateTimeDeserializer = createDateTimeDeserializer(property, fieldInfo);

            if (dateTimeDeserializer != null) {
                SettableBeanProperty newProperty = property.withValueDeserializer(dateTimeDeserializer);
                builder.addOrReplaceProperty(newProperty, true);
            }
        });
        return builder;
    }

    private JsonDeserializer<Object> createDateTimeDeserializer(SettableBeanProperty property, FieldInfo fieldInfo) {
        JsonDeserializer<Object> valueDeserializer = property.getValueDeserializer();
        if (valueDeserializer instanceof JacksonJava8TimeDeserializer) {
            // java8时间类型需设置format
            String format = fieldInfo != null ? fieldInfo.getFormat() : null;
            return ((JacksonJava8TimeDeserializer) valueDeserializer).withDatePattern(format);
        }
        if (valueDeserializer instanceof JacksonDefaultDateTypeDeserializer) {
            // 设置format
            String format = fieldInfo != null ? fieldInfo.getFormat() : null;
            return ((JacksonDefaultDateTypeDeserializer) valueDeserializer).withDatePattern(format);
        }
        return null;
    }
}
