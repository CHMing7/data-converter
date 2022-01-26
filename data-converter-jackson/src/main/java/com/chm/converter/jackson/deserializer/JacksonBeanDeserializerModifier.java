package com.chm.converter.jackson.deserializer;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.UseOriginalJudge;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.utils.CollUtil;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-17
 **/
public class JacksonBeanDeserializerModifier extends BeanDeserializerModifier {

    private final Converter<?> converter;

    private final Class<? extends Converter> converterClass;

    private final UseOriginalJudge useOriginalJudge;

    public JacksonBeanDeserializerModifier(Converter<?> converter, UseOriginalJudge useOriginalJudge) {
        this.converter = converter;
        this.converterClass = converter != null ? converter.getClass() : null;
        this.useOriginalJudge = useOriginalJudge;
    }

    @Override
    public List<BeanPropertyDefinition> updateProperties(DeserializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> propDefs) {
        if (useOriginalJudge.useOriginalImpl(beanDesc.getBeanClass())) {
            return super.updateProperties(config, beanDesc, propDefs);
        }
        List<BeanPropertyDefinition> resultList = new LinkedList<>();
        Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(beanDesc.getBeanClass(), converterClass);
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
        if (useOriginalJudge.useOriginalImpl(beanDesc.getBeanClass())) {
            return super.updateBuilder(config, beanDesc, builder);
        }
        Iterator<SettableBeanProperty> properties = builder.getProperties();
        Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(beanDesc.getBeanClass(), converterClass);
        CollUtil.forEach(properties, (property, index) -> {
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
        JsonDeserializer<Object> serializer = jdk8TimeFirst.map(clazz -> new JacksonJava8TimeDeserializer(cls, format, converter)).orElse(null);
        if (serializer != null) {
            return serializer;
        }
        Optional<Class<? extends Date>> defaultDateFirst = TimeConstant.DEFAULT_DATE_SET.stream()
                .filter(dateClass -> dateClass.isAssignableFrom(cls)).findFirst();
        return defaultDateFirst.map(clazz -> new JacksonDefaultDateTypeDeserializer(cls, format, converter)).orElse(null);
    }

    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return new JacksonEnumDeserializer(type.getRawClass(), converter);
    }
}
