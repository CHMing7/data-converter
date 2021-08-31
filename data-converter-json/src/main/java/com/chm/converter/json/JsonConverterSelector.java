package com.chm.converter.json;

import cn.hutool.core.map.MapUtil;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * JSON转换器选择策略
 * 此类负责选择对应的可用JSON转转器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
public class JsonConverterSelector implements Serializable {

    private static final Map<Class<? extends JsonConverter>, JsonConverter> JSON_CONVERTER_MAP = new HashMap<>();

    private JsonConverterSelector() {
    }

    static {
        // 加载json数据转换类
        Reflections reflections = new Reflections(new SubTypesScanner());
        Set<Class<? extends JsonConverter>> jsonConverterClasses = reflections.getSubTypesOf(JsonConverter.class);
        jsonConverterClasses.forEach(jsonConverterClass -> {
            try {
                JsonConverter jsonConverter = jsonConverterClass.newInstance();
                jsonConverter.loadJsonConverter();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 选择Forest的JSON转换器
     * <p>从FastJson、Jackson以及Gson中动态选择一个可用的JSON转换器</p>
     *
     * @return Forest的JSON转换器，{@link JsonConverter}接口实例
     */
    public static JsonConverter select() {
        return MapUtil.isNotEmpty(JSON_CONVERTER_MAP) ? JSON_CONVERTER_MAP.values().stream().findFirst().orElse(null) : null;
    }

    /**
     * 选择Forest的JSON转换器
     * <p>从FastJson、Jackson以及Gson中动态选择一个可用的JSON转换器</p>
     *
     * @return Forest的JSON转换器，{@link JsonConverter}接口实例
     */
    public static JsonConverter select(Class<? extends JsonConverter> className) {
        return JSON_CONVERTER_MAP.get(className);
    }

    public static void put(Class<? extends JsonConverter> className, JsonConverter jsonConverter) {
        JSON_CONVERTER_MAP.put(className, jsonConverter);
    }
}
