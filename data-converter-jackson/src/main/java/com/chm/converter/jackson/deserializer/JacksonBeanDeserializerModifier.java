package com.chm.converter.jackson.deserializer;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.UseRawJudge;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codec.UniversalCodecAdapterCreator;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.core.utils.StringUtil;
import com.chm.converter.jackson.AbstractModule;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.UnsupportedTypeDeserializer;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeBindings;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-17
 **/
public class JacksonBeanDeserializerModifier extends BeanDeserializerModifier {

    private final Class<? extends Converter> converterClass;

    private final UniversalGenerate<Codec> generate;

    private final UseRawJudge useRawJudge;

    public JacksonBeanDeserializerModifier(Converter<?> converter, UseRawJudge useRawJudge) {
        this(converter, null, useRawJudge);
    }

    public JacksonBeanDeserializerModifier(Converter<?> converter, UniversalGenerate<Codec> generate, UseRawJudge useRawJudge) {
        this.converterClass = converter != null ? converter.getClass() : null;
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);
        this.useRawJudge = useRawJudge;
    }

    @Override
    public List<BeanPropertyDefinition> updateProperties(DeserializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> propDefs) {
        if (useRawJudge.useRawImpl(beanDesc.getBeanClass())) {
            return super.updateProperties(config, beanDesc, propDefs);
        }
        List<BeanPropertyDefinition> resultList = ListUtil.list(true);
        TypeToken<?> typeToken = AbstractModule.jacksonTypeToLangType(beanDesc.getType());
        Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(typeToken, converterClass);
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
        if (useRawJudge.useRawImpl(beanDesc.getBeanClass())) {
            return super.updateBuilder(config, beanDesc, builder);
        }
        Iterator<SettableBeanProperty> properties = builder.getProperties();
        TypeToken<?> typeToken = AbstractModule.jacksonTypeToLangType(beanDesc.getType());
        Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getNameFieldInfoMap(typeToken, converterClass);
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
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        Class<?> beanClass = beanDesc.getBeanClass();
        JsonDeserializer<?> jsonDeserializer = super.modifyDeserializer(config, beanDesc, deserializer);
        if (beanClass == Object.class
                || useRawJudge.useRawImpl(beanClass)
                || jsonDeserializer instanceof JacksonCoreCodecDeserializer) {
            return jsonDeserializer;
        }

        TypeToken<?> typeToken = AbstractModule.jacksonTypeToLangType(beanDesc.getType());
        JacksonCoreCodecDeserializer<?> coreCodecDeserializer = UniversalCodecAdapterCreator.create(this.generate, typeToken,
                (type, codec) -> new JacksonCoreCodecDeserializer<>(codec));

        if (coreCodecDeserializer != null) {
            if (coreCodecDeserializer.getCodec().isPriorityUse() ||
                    deserializer instanceof UnsupportedTypeDeserializer) {
                return coreCodecDeserializer;
            }
        }

        return jsonDeserializer;
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
