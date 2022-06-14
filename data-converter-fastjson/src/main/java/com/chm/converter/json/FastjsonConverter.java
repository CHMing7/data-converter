package com.chm.converter.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chm.converter.core.Converter;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ArrayUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.json.fastjson.deserializer.FastjsonParserConfig;
import com.chm.converter.json.fastjson.serializer.FastjsonSerializeConfig;
import com.google.auto.service.AutoService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 使用Fastjson实现的消息转换实现类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
@AutoService(Converter.class)
public class FastjsonConverter implements JsonConverter {

    public static final List<Class<? extends Annotation>> FASTJSON_ANNOTATION_LIST = ListUtil.of(JSONCreator.class,
            JSONField.class,
            JSONPOJOBuilder.class,
            JSONType.class);

    public static final String FAST_JSON_NAME = "com.alibaba.fastjson.JSON";

    /**
     * Fastjson序列化方式
     */
    private final String disableCircularReferenceDetect = "DisableCircularReferenceDetect";

    private final String customMapDeserializer = "CustomMapDeserializer";

    private SerializerFeature[] serializerFeatureArray;

    private Feature[] featureArray;

    protected SerializeConfig serializeConfig = new FastjsonSerializeConfig(this, FastjsonConverter::checkExistFastjsonAnnotation);

    protected ParserConfig parserConfig = new FastjsonParserConfig(this, FastjsonConverter::checkExistFastjsonAnnotation);

    public FastjsonConverter() {
        addSerializerFeature(SerializerFeature.valueOf(disableCircularReferenceDetect));
        addFeature(Feature.valueOf(customMapDeserializer));
    }

    /**
     * 设置FastJson的序列化特性对象
     *
     * @param serializerFeature FastJson的序列化特性对象，{@link SerializerFeature}枚举实例
     */
    public void addSerializerFeature(SerializerFeature serializerFeature) {
        this.serializerFeatureArray = ArrayUtil.append(this.serializerFeatureArray, serializerFeature);
    }

    /**
     * 设置FastJson的序列化特性对象
     *
     * @param feature FastJson的反序列化特性对象，{@link Feature}枚举实例
     */
    public void addFeature(Feature feature) {
        this.featureArray = ArrayUtil.append(this.featureArray, feature);
    }

    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        return privateConvertToJavaObject(source, targetType);
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        return privateConvertToJavaObject(source, targetType);
    }

    public <T> T convertToJavaObject(String source, TypeReference<T> typeReference) {
        return privateConvertToJavaObject(source, typeReference.getType());
    }

    private <T> T privateConvertToJavaObject(String source, Type targetType) {
        try {
            if (ArrayUtil.isEmpty(featureArray)) {
                return JSON.parseObject(source, targetType, parserConfig);
            } else {
                return JSON.parseObject(source, targetType, parserConfig, featureArray);
            }
        } catch (Throwable th) {
            throw new ConvertException(getConverterName(), String.class.getName(), targetType.getTypeName(), th);
        }
    }

    private String parseToString(Object obj) {
        if (ArrayUtil.isEmpty(serializerFeatureArray)) {
            return JSON.toJSONString(obj, serializeConfig);
        }
        return JSON.toJSONString(obj, serializeConfig, serializerFeatureArray);
    }

    @Override
    public String encode(Object source) {
        if (source instanceof CharSequence) {
            return source.toString();
        }
        try {
            return parseToString(source);
        } catch (Throwable th) {
            throw new ConvertException(getConverterName(), source.getClass().getName(), String.class.getName(), th);
        }
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Fastjson相关类型是否存在
            Class.forName(FAST_JSON_NAME);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean checkExistFastjsonAnnotation(Class<?> cls) {
        return JavaBeanInfo.checkExistAnnotation(cls, FASTJSON_ANNOTATION_LIST);
    }
}

