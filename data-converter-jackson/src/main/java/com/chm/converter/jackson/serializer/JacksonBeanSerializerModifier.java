package com.chm.converter.jackson.serializer;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.UseOriginalJudge;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.jackson.PropertyNameTransformer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.util.NameTransformer;

import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-17
 **/
public class JacksonBeanSerializerModifier extends BeanSerializerModifier {

    private final Converter<?> converter;

    private final Class<? extends Converter> converterClass;

    private final UseOriginalJudge useOriginalJudge;

    public JacksonBeanSerializerModifier(Converter<?> converter, UseOriginalJudge useOriginalJudge) {
        this.converter = converter;
        this.converterClass = converter != null ? converter.getClass() : null;
        this.useOriginalJudge = useOriginalJudge;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        if (useOriginalJudge.useOriginalImpl(beanDesc.getBeanClass())) {
            return super.changeProperties(config, beanDesc, beanProperties);
        }
        List<BeanPropertyWriter> resultList = new LinkedList<>();
        Map<String, BeanPropertyWriter> propertyWriterMap = beanProperties.stream()
                .collect(Collectors.toMap(BeanPropertyWriter::getName, beanPropertyWriter -> beanPropertyWriter));
        NameTransformer nameTransformer = PropertyNameTransformer.get(beanDesc.getBeanClass(), converterClass);
        JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(beanDesc.getBeanClass(), converterClass);
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
            BeanPropertyWriter newWriter = beanPropertyWriter.rename(nameTransformer);
            resultList.add(newWriter);
        });
        return resultList;
    }

    private JsonSerializer<Object> createDateTimeSerializer(BeanPropertyWriter propertyWriter, FieldInfo fieldInfo) {
        // java8时间类型需设置format
        String format = fieldInfo != null ? fieldInfo.getFormat() : null;
        Class<?> cls = propertyWriter.getType().getRawClass();
        Optional<Class<? extends TemporalAccessor>> jdk8TimeFirst = TimeConstant.TEMPORAL_ACCESSOR_SET.stream()
                .filter(temporalAccessorClass -> temporalAccessorClass.isAssignableFrom(cls)).findFirst();
        JsonSerializer<Object> serializer = jdk8TimeFirst.map(clazz -> new JacksonJava8TimeSerializer(clazz, format, converter)).orElse(null);
        if (serializer != null) {
            return serializer;
        }
        Optional<Class<? extends Date>> defaultDateFirst = TimeConstant.DEFAULT_DATE_SET.stream()
                .filter(dateClass -> dateClass.isAssignableFrom(cls)).findFirst();
        return defaultDateFirst.map(clazz -> new JacksonDefaultDateTypeSerializer(clazz, format, converter)).orElse(null);
    }

    @Override
    public JsonSerializer<?> modifyEnumSerializer(SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        return new JacksonEnumSerializer(valueType.getRawClass(), converter);
    }
}
