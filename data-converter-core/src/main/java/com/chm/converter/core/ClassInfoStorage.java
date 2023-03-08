package com.chm.converter.core;

import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.MapUtil;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @date 2021-08-16
 **/
public interface ClassInfoStorage {

    Map<TypeToken<?>, Map<Class<? extends Converter>, JavaBeanInfo>> BEAN_INFO_MAP = MapUtil.newHashMap();

    Map<TypeToken<?>, Map<Class<? extends Converter>, Map<String, FieldInfo>>> NAME_FIELD_INFO_MAP = MapUtil.newHashMap();

    Map<TypeToken<?>, Map<Class<? extends Converter>, Map<String, FieldInfo>>> FIELD_NAME_FIELD_INFO_MAP = MapUtil.newHashMap();

    ClassInfoStorage INSTANCE = new ClassInfoStorage() {
        @Override
        public <T> boolean isInit(TypeToken<T> type, Class<? extends Converter> converterClass) {
            return contains(BEAN_INFO_MAP, type, converterClass);
        }
    };

    /**
     * put
     *
     * @param map
     * @param k
     * @param r
     * @param v
     * @param <K>
     * @param <R>
     * @param <V>
     */
    static <K, R, V> void put(Map<K, Map<R, V>> map, K k, R r, V v) {
        Map<R, V> orDefault = map.getOrDefault(k, MapUtil.newHashMap());
        orDefault.put(r, v);
        map.put(k, orDefault);
    }

    /**
     * get
     *
     * @param map
     * @param k
     * @param r
     * @param <K>
     * @param <R>
     * @param <V>
     * @return
     */
    static <K, R, V> V get(Map<K, Map<R, V>> map, K k, R r) {
        Map<R, V> orDefault = map.getOrDefault(k, MapUtil.newHashMap());
        return orDefault.get(r);
    }

    /**
     * contains
     *
     * @param map
     * @param k
     * @param r
     * @param <K>
     * @param <R>
     * @param <V>
     * @return
     */
    static <K, R, V> boolean contains(Map<K, Map<R, V>> map, K k, R r) {
        Map<R, V> orDefault = map.getOrDefault(k, MapUtil.newHashMap());
        return orDefault.containsKey(r);
    }

    /**
     * 初始化 type 信息
     *
     * @param type
     * @param converter
     */
    default void initClassInfo(Type type, Converter converter) {
        initClassInfo(TypeToken.get(type), converter);
    }

    /**
     * 初始化 TypeToken 信息
     *
     * @param typeToken
     * @param converter
     */
    default <T> void initClassInfo(TypeToken<T> typeToken, Converter converter) {
        Class<? extends Converter> converterClass = converter != null ? converter.getClass() : null;
        initClassInfo(typeToken, converterClass);
    }

    /**
     * 初始化 TypeToken 信息
     *
     * @param type
     * @param converterClass
     */
    default void initClassInfo(Type type, Class<? extends Converter> converterClass) {
        initClassInfo(TypeToken.get(type), converterClass);
    }

    /**
     * 初始化 TypeToken 信息
     *
     * @param typeToken
     * @param converterClass
     */
    default <T> void initClassInfo(TypeToken<T> typeToken, Class<? extends Converter> converterClass) {
        JavaBeanInfo<T> javaBeanInfo = JavaBeanInfo.build(typeToken, converterClass);
        put(BEAN_INFO_MAP, typeToken, converterClass, javaBeanInfo);
        put(NAME_FIELD_INFO_MAP, typeToken, converterClass, javaBeanInfo.getNameFieldInfoMap());
        put(FIELD_NAME_FIELD_INFO_MAP, typeToken, converterClass, javaBeanInfo.getFieldNameFieldInfoMap());
    }

    /**
     * 获取 bean 信息
     *
     * @param type
     * @param converter
     * @return
     */
    default <T> JavaBeanInfo<T> getJavaBeanInfo(Type type, Converter converter) {
        return getJavaBeanInfo(TypeToken.get(type), converter);
    }

    /**
     * 获取 bean 信息
     *
     * @param typeToken
     * @param converter
     * @return
     */
    default <T> JavaBeanInfo<T> getJavaBeanInfo(TypeToken<T> typeToken, Converter converter) {
        Class<? extends Converter> converterClass = converter != null ? converter.getClass() : null;
        if (!contains(BEAN_INFO_MAP, typeToken, converterClass) || !isInit(typeToken, converterClass)) {
            initClassInfo(typeToken, converterClass);
        }
        return get(BEAN_INFO_MAP, typeToken, converterClass);
    }

    /**
     * 获取 bean 信息
     *
     * @param type
     * @param converterClass
     * @return
     */
    default <T> JavaBeanInfo<T> getJavaBeanInfo(Type type, Class<? extends Converter> converterClass) {
        return getJavaBeanInfo(TypeToken.get(type), converterClass);
    }

    /**
     * 获取bean信息
     *
     * @param typeToken
     * @param converterClass
     * @return
     */
    default <T> JavaBeanInfo<T> getJavaBeanInfo(TypeToken<T> typeToken, Class<? extends Converter> converterClass) {
        if (!contains(BEAN_INFO_MAP, typeToken, converterClass) || !isInit(typeToken, converterClass)) {
            initClassInfo(typeToken, converterClass);
        }
        return get(BEAN_INFO_MAP, typeToken, converterClass);
    }

    /**
     * 是否初始化过javaBeanInfo
     *
     * @param type
     * @param converterClass
     * @return
     */
    <T> boolean isInit(TypeToken<T> type, Class<? extends Converter> converterClass);

    /**
     * 获取 field 信息
     *
     * @param type
     * @param converterClass
     * @return
     */
    default Map<String, FieldInfo> getNameFieldInfoMap(Type type, Class<? extends Converter> converterClass) {
        return getNameFieldInfoMap(TypeToken.get(type), converterClass);
    }

    /**
     * 获取 field 信息
     *
     * @param typeToken
     * @param converterClass
     * @return
     */
    default <T> Map<String, FieldInfo> getNameFieldInfoMap(TypeToken<T> typeToken, Class<? extends Converter> converterClass) {
        if (!contains(NAME_FIELD_INFO_MAP, typeToken, converterClass) || !isInit(typeToken, converterClass)) {
            initClassInfo(typeToken, converterClass);
        }
        return get(NAME_FIELD_INFO_MAP, typeToken, converterClass);
    }

    /**
     * 获取 field 信息
     *
     * @param type
     * @param converterClass
     * @return
     */
    default Map<String, FieldInfo> getFieldNameFieldInfoMap(Type type, Class<? extends Converter> converterClass) {
        return getFieldNameFieldInfoMap(TypeToken.get(type), converterClass);
    }

    /**
     * 获取 field 信息
     *
     * @param typeToken
     * @param converterClass
     * @return
     */
    default <T> Map<String, FieldInfo> getFieldNameFieldInfoMap(TypeToken<T> typeToken, Class<? extends Converter> converterClass) {
        if (!contains(FIELD_NAME_FIELD_INFO_MAP, typeToken, converterClass) || !isInit(typeToken, converterClass)) {
            initClassInfo(typeToken, converterClass);
        }
        return get(FIELD_NAME_FIELD_INFO_MAP, typeToken, converterClass);
    }
}

