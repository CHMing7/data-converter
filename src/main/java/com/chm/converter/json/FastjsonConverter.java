package com.chm.converter.json;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.*;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import com.chm.converter.Converter;
import com.chm.converter.exceptions.ConvertException;

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
 * @since 2021-01-04
 **/
public class FastjsonConverter implements JsonConverter {

    /**
     * Fastjson序列化方式
     */
    private String serializerFeatureName = "DisableCircularReferenceDetect";

    private SerializerFeature serializerFeature;

    /**
     * 日期格式
     */
    private String dateFormat;

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
     * 获取FastJson的序列化特性名
     *
     * @return FastJson的序列化特性名字符串
     */
    public String getSerializerFeatureName() {
        return serializerFeatureName;
    }

    /**
     * 设置FastJson的序列化特性名
     *
     * @param serializerFeatureName FastJson的序列化特性名字符串
     */
    public void setSerializerFeatureName(String serializerFeatureName) {
        this.serializerFeatureName = serializerFeatureName;
        SerializerFeature feature = SerializerFeature.valueOf(serializerFeatureName);
        setSerializerFeature(feature);
    }

    /**
     * 获取FastJson的序列化特性对象
     *
     * @return FastJson的序列化特性对象，{@link SerializerFeature}枚举实例
     */
    public SerializerFeature getSerializerFeature() {
        return serializerFeature;
    }

    public FastjsonConverter() {
        setSerializerFeature(SerializerFeature.valueOf(serializerFeatureName));
    }

    /**
     * 设置FastJson的序列化特性对象
     *
     * @param serializerFeature FastJson的序列化特性对象，{@link SerializerFeature}枚举实例
     */
    public void setSerializerFeature(SerializerFeature serializerFeature) {
        this.serializerFeature = serializerFeature;
        if (serializerFeature == null) {
            this.serializerFeatureName = null;
        } else {
            this.serializerFeatureName = serializerFeature.name();
        }
    }

    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        try {
            return JSON.parseObject(source, targetType);
        } catch (Throwable th) {
            throw new ConvertException("json", th);
        }
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return JSON.parseObject(source, targetType);
        } catch (Throwable th) {
            throw new ConvertException("json", th);
        }

    }

    public <T> T convertToJavaObject(String source, TypeReference<T> typeReference) {
        try {
            return JSON.parseObject(source, typeReference);
        } catch (Throwable th) {
            throw new ConvertException("json", th);
        }

    }


    private String parseToString(Object obj) {
        if (serializerFeature == null) {
            if (dateFormat != null) {
                return JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat);
            }
            return JSON.toJSONString(obj);
        }
        if (dateFormat != null) {
            return JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat, serializerFeature);
        }
        return JSON.toJSONString(obj, serializerFeature);

    }

    @Override
    public String encodeToString(Object obj) {
        if (obj instanceof CharSequence) {
            obj.toString();
        }
        try {
            return parseToString(obj);
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }

    private static final Object toJSON(Object javaObject) {
        ParserConfig parserConfig = ParserConfig.getGlobalInstance();
        return toJSON(javaObject, parserConfig);
    }

    private static final Object toJSON(Object javaObject, ParserConfig mapping) {
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
        } catch (IllegalAccessException e) {
            throw new JSONException("toJSON error", e);
        } catch (InvocationTargetException e) {
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

    @Override
    public Converter setDateFormat(String format) {
        this.dateFormat = format;
        if (StrUtil.isNotBlank(format)) {
            JSON.DEFFAULT_DATE_FORMAT = format;
        }
        return this;
    }

    @Override
    public String getDateFormat() {
        return this.dateFormat;
    }

    public Map<String, Object> defaultJsonMap(Object obj) {
        Object jsonObj = JSON.toJSON(obj);
        return (Map<String, Object>) jsonObj;
    }
}
