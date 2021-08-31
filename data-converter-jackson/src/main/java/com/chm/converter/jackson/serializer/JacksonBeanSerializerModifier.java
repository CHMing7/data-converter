package com.chm.converter.jackson.serializer;

import com.chm.converter.constant.TimeConstant;
import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.json.JsonConverter;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.util.NameTransformer;

import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-17
 **/
public class JacksonBeanSerializerModifier extends BeanSerializerModifier {

    private final JsonConverter jsonConverter;

    public JacksonBeanSerializerModifier(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        List<BeanPropertyWriter> resultList = new LinkedList<>();
        Map<String, BeanPropertyWriter> propertyWriterMap = beanProperties.stream()
                .collect(Collectors.toMap(BeanPropertyWriter::getName, beanPropertyWriter -> beanPropertyWriter));
        PropertyNameTransformer propertyNameTransformer = new PropertyNameTransformer(beanDesc.getBeanClass());
        JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(beanDesc.getBeanClass());
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        // 去除不序列化的属性
        sortedFieldList.stream().filter(FieldInfo::isSerialize).forEach(fieldInfo -> {
            BeanPropertyWriter beanPropertyWriter = propertyWriterMap.get(fieldInfo.getFieldName());
            // 修改时间类型序列化类
            JsonSerializer<Object> dateTimeSerializer = createDateTimeSerializer(beanPropertyWriter, fieldInfo);
            if (dateTimeSerializer != null) {
                beanPropertyWriter.assignSerializer(dateTimeSerializer);
            }
            // 修改key值
            BeanPropertyWriter newWriter = beanPropertyWriter.rename(propertyNameTransformer);
            resultList.add(newWriter);
        });
        return resultList;
    }

    /**
     * 属性名称转换类
     *
     * @author pengming
     * @Description
     * @Date 2016年7月28日 下午3:03:40
     */
    private static class PropertyNameTransformer extends NameTransformer {

        private final Map<String, FieldInfo> fieldInfoMap;

        public PropertyNameTransformer(Class<?> clazz) {
            this.fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(clazz);
        }

        @Override
        public String transform(String name) {
            // 返回新属性名称
            FieldInfo fieldInfo = fieldInfoMap.get(name);
            return fieldInfo != null ? fieldInfo.getName() : name;
        }

        @Override
        public String reverse(String transformed) {
            return transformed;
        }
    }

    private JsonSerializer<Object> createDateTimeSerializer(BeanPropertyWriter propertyWriter, FieldInfo fieldInfo) {
        // java8时间类型需设置format
        String format = fieldInfo != null ? fieldInfo.getFormat() : null;
        Class<?> cls = propertyWriter.getType().getRawClass();
        Optional<Class<? extends TemporalAccessor>> jdk8TimeFirst = TimeConstant.TEMPORAL_ACCESSOR_SET.stream()
                .filter(temporalAccessorClass -> temporalAccessorClass.isAssignableFrom(cls)).findFirst();
        JsonSerializer<Object> serializer = jdk8TimeFirst.map(clazz -> new JacksonJava8TimeSerializer(cls, format, jsonConverter)).orElse(null);
        if (serializer != null) {
            return serializer;
        }
        Optional<Class<? extends Date>> defaultDateFirst = TimeConstant.DEFAULT_DATE_SET.stream()
                .filter(dateClass -> dateClass.isAssignableFrom(cls)).findFirst();
        return defaultDateFirst.map(clazz -> new JacksonDefaultDateTypeSerializer(format, jsonConverter)).orElse(null);
    }
}
