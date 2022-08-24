package com.chm.converter.json;

import com.chm.converter.core.Converter;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.core.utils.TypeUtil;
import com.chm.converter.json.gson.GsonTypeAdapterFactory;
import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 使用Gson实现的数据转换器实现类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
@AutoService(Converter.class)
public class GsonConverter implements JsonConverter {

    public static final List<Class<? extends Annotation>> GSON_ANNOTATION_LIST = ListUtil.of(Expose.class,
            JsonAdapter.class,
            SerializedName.class,
            Since.class,
            Until.class);

    private final List<TypeAdapterFactory> factories = ListUtil.toLinkedList(
            new GsonTypeAdapterFactory(this, GsonConverter::checkExistGsonAnnotation));

    public static final String GSON_NAME = "com.google.gson.JsonParser";

    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        try {
            if (Map.class.isAssignableFrom(targetType)) {
                JsonObject jsonObject = JsonParser.parseString(source).getAsJsonObject();
                return (T) toMap(jsonObject, false);
            } else if (List.class.isAssignableFrom(targetType)) {
                JsonArray jsonArray = JsonParser.parseString(source).getAsJsonArray();
                return (T) toList(jsonArray);
            }
            Gson gson = createGson();
            return gson.fromJson(source, targetType);
        } catch (Throwable th) {
            throw new ConvertException(getConverterName(), String.class.getName(), targetType.getName(), th);
        }
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            if (targetType instanceof ParameterizedType
                    || targetType.getClass().getName().startsWith("com.google.gson")) {
                Gson gson = createGson();
                return gson.fromJson(source, targetType);
            }
            Class clazz = TypeUtil.getClass(targetType);
            return (T) convertToJavaObject(source, clazz);
        } catch (Exception ex) {
            throw new ConvertException(getConverterName(), String.class.getName(), targetType.getTypeName(), ex);
        }
    }


    private static Map<String, Object> toMap(JsonObject json, boolean singleLevel) {
        Map<String, Object> map = new HashMap<>();
        Set<Map.Entry<String, JsonElement>> entrySet = json.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (singleLevel) {
                if (value instanceof JsonArray) {
                    map.put(key, toList((JsonArray) value));
                } else if (value instanceof JsonPrimitive) {
                    map.put(key, toObject((JsonPrimitive) value));
                } else {
                    map.put(key, value);
                }
                continue;
            }
            if (value instanceof JsonArray) {
                map.put(key, toList((JsonArray) value));
            } else if (value instanceof JsonObject) {
                map.put(key, toMap((JsonObject) value, singleLevel));
            } else if (value instanceof JsonPrimitive) {
                map.put(key, toObject((JsonPrimitive) value));
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    private static Object toObject(JsonPrimitive jsonPrimitive) {
        if (jsonPrimitive.isBoolean()) {
            return jsonPrimitive.getAsBoolean();
        }
        if (jsonPrimitive.isString()) {
            return jsonPrimitive.getAsString();
        }
        if (jsonPrimitive.isNumber()) {
            BigDecimal num = jsonPrimitive.getAsBigDecimal();
            int index = num.toString().indexOf('.');
            if (index == -1) {
                if (num.compareTo(new BigDecimal(Long.MAX_VALUE)) == 1) {
                    return num;
                }
                if (num.compareTo(new BigDecimal(Long.MIN_VALUE)) == -1) {
                    return num;
                }
                if (num.compareTo(new BigDecimal(Integer.MAX_VALUE)) == 1
                        || num.compareTo(new BigDecimal(Integer.MIN_VALUE)) == -1) {
                    return jsonPrimitive.getAsLong();
                }
                return jsonPrimitive.getAsInt();
            }
            double dvalue = jsonPrimitive.getAsDouble();
            float fvalue = jsonPrimitive.getAsFloat();
            if (String.valueOf(dvalue).equals(fvalue)) {
                return fvalue;
            }
            return dvalue;
        }
        if (jsonPrimitive.isJsonArray()) {
            return toList(jsonPrimitive.getAsJsonArray());
        }
        if (jsonPrimitive.isJsonObject()) {
            return toMap(jsonPrimitive.getAsJsonObject(), false);
        }
        return null;
    }

    private static List<Object> toList(JsonArray json) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < json.size(); i++) {
            Object value = json.get(i);
            if (value instanceof JsonArray) {
                list.add(toList((JsonArray) value));
            } else if (value instanceof JsonObject) {
                list.add(toMap((JsonObject) value, false));
            } else if (value instanceof JsonPrimitive) {
                list.add(toObject((JsonPrimitive) value));
            } else {
                list.add(value);
            }
        }
        return list;
    }

    public void addTypeAdapterFactory(TypeAdapterFactory typeAdapterFactory) {
        this.factories.add(typeAdapterFactory);
    }

    /**
     * 创建GSON对象
     *
     * @return New instance of {@code com.google.gson.Gson}
     */
    protected Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        this.factories.forEach(gsonBuilder::registerTypeAdapterFactory);
        return gsonBuilder.create();
    }

    @Override
    public String encode(Object source) {
        Gson gson = createGson();
        return gson.toJson(source);
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Gson相关类型是否存在
            Class.forName(GSON_NAME);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean checkExistGsonAnnotation(Class<?> cls) {
        return JavaBeanInfo.checkExistAnnotation(cls, GSON_ANNOTATION_LIST);
    }
}
