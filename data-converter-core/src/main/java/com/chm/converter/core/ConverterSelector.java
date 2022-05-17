package com.chm.converter.core;

import com.chm.converter.core.utils.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-01
 **/
public class ConverterSelector implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ConverterSelector.class);

    private static final Map<DataType, Map<Class<? extends Converter>, Converter>> CONVERTER_MAP = new ConcurrentHashMap<>();

    protected ConverterSelector() {
    }

    static {
        // 加载数据转换器类
        ServiceLoader<Converter> converters = ServiceLoader.load(Converter.class);
        converters.forEach(converter -> {
            try {
                if (converter.loadConverter()) {
                    converter.logLoadSuccess();
                } else {
                    converter.logLoadFail();
                }
            } catch (Throwable e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * 获取所有数据类型
     *
     * @return 数据类型
     */
    public static DataType[] getDateTypes() {
        return CONVERTER_MAP.keySet().toArray(new DataType[0]);
    }

    /**
     * 获取所有数据类型
     *
     * @return 数据类型
     */
    public static List<DataType> getDateTypeList() {
        return new ArrayList<>(CONVERTER_MAP.keySet());
    }

    /**
     * 根据数据类型获取所有转换器数组
     *
     * @param dataType 数据类型
     * @return 转换器数组
     */
    public static Converter[] getConvertersByDateType(DataType dataType) {
        return CONVERTER_MAP.get(dataType).values().toArray(new Converter[0]);
    }

    /**
     * 根据数据类型获取所有转换器列表
     *
     * @param dataType 数据类型
     * @return 转换器列表
     */
    public static List<Converter> getConverterListByDateType(DataType dataType) {
        return new ArrayList<>(CONVERTER_MAP.get(dataType).values());
    }

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link Converter}接口实例
     */
    public static Converter select(DataType dataType) {
        Map<Class<? extends Converter>, Converter> classConverterMap = CONVERTER_MAP.get(dataType);
        return MapUtil.isNotEmpty(classConverterMap) ? classConverterMap.values().stream().findFirst().orElse(null) : null;
    }

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link Converter}接口实例
     */
    public static <T extends Converter> T select(Class<T> cls) {
        for (DataType dataType : CONVERTER_MAP.keySet()) {
            T converter = select(dataType, cls);
            if (converter != null) {
                return converter;
            }
        }
        return null;
    }

    /**
     * 选择数据转换器
     * <p>动态选择一个可用的数据转换器</p>
     *
     * @return 数据转换器，{@link Converter}接口实例
     */
    public static <T extends Converter> T select(DataType dataType, Class<T> cls) {
        Map<Class<? extends Converter>, Converter> classConverterMap = CONVERTER_MAP.get(dataType);
        if (MapUtil.isNotEmpty(classConverterMap)) {
            Converter converter = classConverterMap.get(cls);
            if (converter != null) {
                return (T) converter;
            }
            for (Map.Entry<Class<? extends Converter>, Converter> classConverterEntry : classConverterMap.entrySet()) {
                if (cls.isAssignableFrom(classConverterEntry.getKey())) {
                    return (T) classConverterEntry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 注册数据转换器
     *
     * @param cls
     * @param converter
     * @return
     */
    public static boolean register(Class<? extends Converter> cls, Converter converter) {
        DataType dataType = converter.getDataType();
        Map<Class<? extends Converter>, Converter> classConverterMap = CONVERTER_MAP.get(dataType);
        if (classConverterMap == null) {
            classConverterMap = new ConcurrentHashMap<>();
            CONVERTER_MAP.put(dataType, classConverterMap);
        }
        classConverterMap.put(cls, converter);
        return true;
    }

    /**
     * 注册数据转换器
     *
     * @param converter
     * @return
     */
    public static boolean register(Converter converter) {
        if (converter == null) {
            return false;
        }
        Class<? extends Converter> converterClass = converter.getClass();
        return ConverterSelector.register(converterClass, converter);
    }
}
