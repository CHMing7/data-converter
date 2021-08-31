package com.chm.converter.jackson.deserializer;

import cn.hutool.core.collection.CollectionUtil;
import com.chm.converter.constant.TimeConstant;
import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.json.JsonConverter;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-17
 **/
public class JacksonBeanDeserializerModifier extends BeanDeserializerModifier {

    private final JsonConverter jsonConverter;

    public JacksonBeanDeserializerModifier(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public List<BeanPropertyDefinition> updateProperties(DeserializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> propDefs) {
        List<BeanPropertyDefinition> resultList = new LinkedList<>();
        Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(beanDesc.getBeanClass());
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
        Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(beanDesc.getBeanClass());
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
        // java8时间类型需设置format
        String format = fieldInfo != null ? fieldInfo.getFormat() : null;
        Class<?> cls = property.getType().getRawClass();
        Optional<Class<? extends TemporalAccessor>> jdk8TimeFirst = TimeConstant.TEMPORAL_ACCESSOR_SET.stream()
                .filter(temporalAccessorClass -> temporalAccessorClass.isAssignableFrom(cls)).findFirst();
        JsonDeserializer<Object> serializer = jdk8TimeFirst.map(clazz -> new JacksonJava8TimeDeserializer(cls, format, jsonConverter)).orElse(null);
        if (serializer != null) {
            return serializer;
        }
        Optional<Class<? extends Date>> defaultDateFirst = TimeConstant.DEFAULT_DATE_SET.stream()
                .filter(dateClass -> dateClass.isAssignableFrom(cls)).findFirst();
        return defaultDateFirst.map(clazz -> new JacksonDefaultDateTypeDeserializer(cls, format, jsonConverter)).orElse(null);

    }
}