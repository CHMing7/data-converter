package com.chm.converter.xml.jackson.deserializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.UseOriginalJudge;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.jackson.deserializer.JacksonDefaultDateTypeDeserializer;
import com.chm.converter.jackson.deserializer.JacksonJava8TimeDeserializer;
import com.chm.converter.xml.XmlClassInfoStorage;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.dataformat.xml.deser.WrapperHandlingDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.XmlBeanDeserializerModifier;
import com.fasterxml.jackson.dataformat.xml.deser.XmlTextDeserializer;

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
 * @since 2021-09-09
 **/
public class JacksonXmlBeanDeserializerModifier extends XmlBeanDeserializerModifier {

    private final Converter<?> converter;

    private final Class<? extends Converter> converterClass;

    private final UseOriginalJudge useOriginalJudge;

    public JacksonXmlBeanDeserializerModifier(String nameForTextValue, Converter<?> converter, UseOriginalJudge useOriginalJudge) {
        super(nameForTextValue);
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
        Map<String, FieldInfo> fieldInfoMap = XmlClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(beanDesc.getBeanClass(), converterClass);
        propDefs.forEach(beanPropertyDefinition -> {
            // 去除不反序列化的属性
            String fieldName = beanPropertyDefinition.getName();
            FieldInfo fieldInfo = fieldInfoMap.get(fieldName);
            if (!fieldInfo.isDeserialize()) {
                // 去除不序列化的属性
                return;
            }
            Boolean b = (Boolean) fieldInfo.getExpandProperty("isText", false);
            String newName = b ? _cfgNameForTextValue : fieldInfo.getName();
            // 修改key值
            BeanPropertyDefinition newProperty = beanPropertyDefinition.withSimpleName(newName);
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
        Map<String, FieldInfo> fieldInfoMap = XmlClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(beanDesc.getBeanClass(), converterClass);
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
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deser0) {
        if (useOriginalJudge.useOriginalImpl(beanDesc.getBeanClass())) {
            return super.modifyDeserializer(config, beanDesc, deser0);
        }
        if (!(deser0 instanceof BeanDeserializerBase)) {
            return deser0;
        }
        /* 17-Aug-2013, tatu: One important special case first: if we have one "XML Text"
         * property, it may be exposed as VALUE_STRING token (depending on whether any attribute
         * values are exposed): and to deserialize from that, we need special handling unless POJO
         * has appropriate single-string creator method.
         */
        BeanDeserializerBase deser = (BeanDeserializerBase) deser0;

        // Heuristics are bit tricky; but for now let's assume that if POJO
        // can already work with VALUE_STRING, it's ok and doesn't need extra support
        ValueInstantiator inst = deser.getValueInstantiator();
        // 03-Aug-2017, tatu: [dataformat-xml#254] suggests we also should
        //    allow passing `int`/`Integer`/`long`/`Long` cases, BUT
        //    unfortunately we can not simply use default handling. Would need
        //    coercion.
        // 30-Apr-2020, tatu: Complication from [dataformat-xml#318] as we now
        //    have a delegate too...
        if (!inst.canCreateFromString()) {
            SettableBeanProperty textProp = findSoleTextProp(beanDesc, deser.properties());
            if (textProp != null) {
                return new XmlTextDeserializer(deser, textProp);
            }
        }
        return new WrapperHandlingDeserializer(deser);
    }

    private SettableBeanProperty findSoleTextProp(BeanDescription beanDesc,
                                                  Iterator<SettableBeanProperty> propIt) {
        JavaBeanInfo javaBeanInfo = XmlClassInfoStorage.INSTANCE.getJavaBeanInfo(beanDesc.getBeanClass(), converterClass);
        Map<String, FieldInfo> fieldNameFieldInfoMap = javaBeanInfo.getFieldNameFieldInfoMap();
        SettableBeanProperty textProp = null;
        while (propIt.hasNext()) {
            SettableBeanProperty prop = propIt.next();
            AnnotatedMember m = prop.getMember();
            if (m != null) {
                // Ok, let's use a simple check: we should have renamed it earlier so:
                PropertyName n = prop.getFullName();
                FieldInfo fieldInfo = fieldNameFieldInfoMap.get(n.getSimpleName());
                Boolean isText = (Boolean) fieldInfo.getExpandProperty("isText", false);
                if (isText) {
                    // should we verify we only got one?
                    textProp = prop;
                    continue;
                }
                // as-attribute are ok as well

                Boolean b = (Boolean) fieldInfo.getExpandProperty("isAttribute", false);
                if (b != null && b) {
                    continue;
                }
            }
            // Otherwise, it's something else; no go
            return null;
        }
        return textProp;
    }

}
