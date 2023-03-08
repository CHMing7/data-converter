package com.chm.converter.xml.jackson.deserializer;

import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.UseRawJudge;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codec.UniversalCodecAdapterCreator;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.StringUtil;
import com.chm.converter.jackson.deserializer.JacksonCoreCodecDeserializer;
import com.chm.converter.xml.XmlClassInfoStorage;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.UnsupportedTypeDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.dataformat.xml.deser.WrapperHandlingDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.XmlBeanDeserializerModifier;
import com.fasterxml.jackson.dataformat.xml.deser.XmlTextDeserializer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @date 2021-09-09
 **/
public class JacksonXmlBeanDeserializerModifier extends XmlBeanDeserializerModifier {

    private final Class<? extends Converter> converterClass;

    private final UniversalGenerate<Codec> generate;

    private final UseRawJudge useRawJudge;

    public JacksonXmlBeanDeserializerModifier(String nameForTextValue, Converter<?> converter, UseRawJudge useRawJudge) {
        this(nameForTextValue, converter, null, useRawJudge);
    }

    public JacksonXmlBeanDeserializerModifier(String nameForTextValue, Converter<?> converter, UniversalGenerate<Codec> generate, UseRawJudge useRawJudge) {
        super(nameForTextValue);
        this.converterClass = converter != null ? converter.getClass() : null;
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);
        this.useRawJudge = useRawJudge;
    }

    @Override
    public List<BeanPropertyDefinition> updateProperties(DeserializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> propDefs) {
        if (useRawJudge.useRawImpl(beanDesc.getBeanClass())) {
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
        if (useRawJudge.useRawImpl(beanDesc.getBeanClass())) {
            return super.updateBuilder(config, beanDesc, builder);
        }
        Iterator<SettableBeanProperty> properties = builder.getProperties();
        Map<String, FieldInfo> fieldInfoMap = XmlClassInfoStorage.INSTANCE.getNameFieldInfoMap(beanDesc.getBeanClass(), converterClass);
        CollUtil.forEach(properties, (property, index) -> {
            FieldInfo fieldInfo = fieldInfoMap.get(property.getName());
            // 修改时间类型反序列化类
            JsonDeserializer<Object> dateTimeDeserializer = createCoreCodecSerializer(fieldInfo);
            if (dateTimeDeserializer != null) {
                SettableBeanProperty newProperty = property.withValueDeserializer(dateTimeDeserializer);
                builder.addOrReplaceProperty(newProperty, true);
            }
        });
        return builder;
    }

    private JsonDeserializer<Object> createCoreCodecSerializer(FieldInfo fieldInfo) {
        if (fieldInfo == null) {
            return null;
        }
        // 时间类型需设置format
        String format = fieldInfo.getFormat();
        JacksonCoreCodecDeserializer coreCodecDeserializer = UniversalCodecAdapterCreator.createPriorityUse(this.generate,
                fieldInfo.getFieldType(), (type, codec) -> {
                    if (codec instanceof WithFormat && StringUtil.isNotBlank(format)) {
                        return new JacksonCoreCodecDeserializer<>((Codec) ((WithFormat) codec).withDatePattern(format));
                    }
                    return new JacksonCoreCodecDeserializer<>(codec);
                });

        return coreCodecDeserializer;
    }

    @Override
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deser0) {
        Class<?> beanClass = beanDesc.getBeanClass();
        if (beanClass == Object.class
                || useRawJudge.useRawImpl(beanClass)
                || deser0 instanceof JacksonCoreCodecDeserializer) {
            return super.modifyDeserializer(config, beanDesc, deser0);
        }

        JsonDeserializer<?> returnDeserializer = deser0;
        JacksonCoreCodecDeserializer coreCodecDeserializer = UniversalCodecAdapterCreator.create(this.generate, beanClass,
                (type, codec) -> new JacksonCoreCodecDeserializer<>(codec));

        if (coreCodecDeserializer != null) {
            if (coreCodecDeserializer.getCodec().isPriorityUse() ||
                    deser0 instanceof UnsupportedTypeDeserializer) {
                returnDeserializer = coreCodecDeserializer;
            }
        }

        if (!(deser0 instanceof BeanDeserializerBase)) {
            return returnDeserializer;
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
        Map<String, FieldInfo> nameFieldInfoMap = javaBeanInfo.getNameFieldInfoMap();
        SettableBeanProperty textProp = null;
        while (propIt.hasNext()) {
            SettableBeanProperty prop = propIt.next();
            AnnotatedMember m = prop.getMember();
            if (m != null) {
                // Ok, let's use a simple check: we should have renamed it earlier so:
                PropertyName n = prop.getFullName();
                FieldInfo fieldInfo = nameFieldInfoMap.get(n.getSimpleName());
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

    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        if (deserializer instanceof JacksonCoreCodecDeserializer) {
            return deserializer;
        }
        Codec codec = this.generate.get(type.getRawClass());
        return new JacksonCoreCodecDeserializer<>(codec);
    }
}
