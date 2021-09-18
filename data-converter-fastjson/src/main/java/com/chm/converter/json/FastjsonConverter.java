package com.chm.converter.json;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.*;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.json.fastjson.deserializer.FastjsonParserConfig;
import com.chm.converter.json.fastjson.serializer.FastjsonSerializeConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    protected static ParserConfig staticParserConfig = new FastjsonParserConfig(null, FastjsonConverter::checkExistFastjsonAnnotation);

    private static Field nameField;

    private static Method nameMethod;

    static {
        Class<FieldInfo> clazz = FieldInfo.class;
        try {
            nameField = clazz.getField("name");
        } catch (NoSuchFieldException e) {
            try {
                nameMethod = clazz.getMethod("getName", new Class[0]);
            } catch (NoSuchMethodException ignored) {
            }
        }
    }

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
            throw new ConvertException("json", th);
        }
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return JSON.parseObject(source, targetType, parserConfig);
        } catch (Throwable th) {
            throw new ConvertException("json", th);
        }

    }

    public <T> T convertToJavaObject(String source, TypeReference<T> typeReference) {
        try {
            return JSON.parseObject(source, typeReference.getType(), parserConfig);
        } catch (Throwable th) {
            throw new ConvertException("json", th);
        }

    }

    private String parseToString(Object obj) {
        if (CollectionUtil.isEmpty(serializerFeatureList)) {
            return JSON.toJSONString(obj, serializeConfig);
        }
        return JSON.toJSONString(obj, serializeConfig, serializerFeatureArray);
    }

    @Override
    public String encode(Object obj) {
        if (obj instanceof CharSequence) {
            obj.toString();
        }
        try {
            return parseToString(obj);
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }

    private static Object toJSON(Object javaObject) {
        return toJSON(javaObject, staticParserConfig);
    }

    private static Object toJSON(Object javaObject, ParserConfig mapping) {
        if (javaObject == null) {
            return null;
        }

        if (javaObject instanceof JSON) {
            return (JSON) javaObject;
        }

        if (javaObject instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) javaObject;

            JSONObject json = new JSONObject(map.size());

            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                Object key = entry.getKey();
                String jsonKey = TypeUtils.castToString(key);
                Object jsonValue = toJSON(entry.getValue());
                json.put(jsonKey, jsonValue);
            }

            return json;
        }

        if (javaObject instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) javaObject;

            JSONArray array = new JSONArray(collection.size());

            for (Object item : collection) {
                Object jsonValue = toJSON(item);
                array.add(jsonValue);
            }

            return array;
        }

        Class<?> clazz = javaObject.getClass();

        if (clazz.isEnum()) {
            return ((Enum<?>) javaObject).name();
        }

        if (clazz.isArray()) {
            int len = Array.getLength(javaObject);

            JSONArray array = new JSONArray(len);

            for (int i = 0; i < len; ++i) {
                Object item = Array.get(javaObject, i);
                Object jsonValue = toJSON(item);
                array.add(jsonValue);
            }

            return array;
        }

        if (mapping.isPrimitive(clazz)) {
            return javaObject;
        }

        try {
            List<FieldInfo> getters = TypeUtils.computeGetters(clazz, null);

            JSONObject json = new JSONObject(getters.size(), true);

            for (FieldInfo field : getters) {
                Object value = field.get(javaObject);
                Object jsonValue = JSON.toJSON(value);
                if (nameField != null) {
                    json.put((String) nameField.get(field), jsonValue);
                } else if (nameMethod != null) {
                    json.put((String) nameMethod.invoke(field), jsonValue);
                }
            }

            return json;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new JSONException("toJSON error", e);
        }
    }


    @Override
    public Map<String, Object> convertObjectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        if (nameField == null && nameMethod == null) {
            return defaultJsonMap(obj);
        }
        if (obj instanceof CharSequence) {
            return convertToJavaObject(obj.toString(), LinkedHashMap.class);
        }
        List<FieldInfo> getters = TypeUtils.computeGetters(obj.getClass(), null);
        JSONObject json = new JSONObject(getters.size(), true);

        try {
            for (FieldInfo field : getters) {
                Object value = field.get(obj);
                if (nameField != null) {
                    json.put((String) nameField.get(field), value);
                } else if (nameMethod != null) {
                    json.put((String) nameMethod.invoke(field), value);
                }
            }
            return json;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return defaultJsonMap(obj);
        }
    }

    public Map<String, Object> defaultJsonMap(Object obj) {
        Object jsonObj = JSON.toJSON(obj);
        return (Map<String, Object>) jsonObj;
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

