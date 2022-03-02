package com.chm.converter.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ArrayUtil;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.json.fastjson.deserializer.FastjsonParserConfig;
import com.chm.converter.json.fastjson.serializer.FastjsonSerializeConfig;

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
public class FastjsonConverter implements JsonConverter {

    public static final List<Class<? extends Annotation>> FASTJSON_ANNOTATION_LIST = ListUtil.of(JSONCreator.class,
            JSONField.class,
            JSONPOJOBuilder.class,
            JSONType.class);

    public static final String FAST_JSON_NAME = "com.alibaba.fastjson.JSON";

    /**
     * Fastjson序列化方式
     */
    private final String serializerFeatureName = "DisableCircularReferenceDetect";

    private List<SerializerFeature> serializerFeatureList;

    private SerializerFeature[] serializerFeatureArray;

    protected SerializeConfig serializeConfig = new FastjsonSerializeConfig(this, FastjsonConverter::checkExistFastjsonAnnotation);

    protected ParserConfig parserConfig = new FastjsonParserConfig(this, FastjsonConverter::checkExistFastjsonAnnotation);

    /**
     * 获取FastJson的序列化特性对象
     *
     * @return FastJson的序列化特性对象，{@link SerializerFeature}枚举实例
     */
    public List<SerializerFeature> getSerializerFeatureList() {
        return serializerFeatureList;
    }

    public FastjsonConverter() {
        addSerializerFeature(SerializerFeature.valueOf(serializerFeatureName));
    }

    /**
     * 设置FastJson的序列化特性对象
     *
     * @param serializerFeature FastJson的序列化特性对象，{@link SerializerFeature}枚举实例
     */
    public void addSerializerFeature(SerializerFeature serializerFeature) {
        if (serializerFeatureList == null) {
            this.serializerFeatureList = ListUtil.list(true);
        }
        this.serializerFeatureList.add(serializerFeature);
        this.serializerFeatureArray = ArrayUtil.toArray(this.serializerFeatureList, SerializerFeature.class);
    }

    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        try {
            return JSON.parseObject(source, targetType, parserConfig);
        } catch (Throwable th) {
            throw new ConvertException(getConverterName(), String.class.getName(), targetType.getName(), th);
        }
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return JSON.parseObject(source, targetType, parserConfig);
        } catch (Throwable th) {
            throw new ConvertException(getConverterName(), String.class.getName(), targetType.getTypeName(), th);
        }

    }

    public <T> T convertToJavaObject(String source, TypeReference<T> typeReference) {
        try {
            return JSON.parseObject(source, typeReference.getType(), parserConfig);
        } catch (Throwable th) {
            throw new ConvertException(getConverterName(), String.class.getName(), typeReference.getType().getTypeName(), th);
        }

    }

    private String parseToString(Object obj) {
        if (CollUtil.isEmpty(serializerFeatureList)) {
            return JSON.toJSONString(obj, serializeConfig);
        }
        return JSON.toJSONString(obj, serializeConfig, serializerFeatureArray);
    }

    @Override
    public String encode(Object source) {
        if (source instanceof CharSequence) {
            source.toString();
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

