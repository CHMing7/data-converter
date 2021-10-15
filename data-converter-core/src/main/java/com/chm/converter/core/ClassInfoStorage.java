package com.chm.converter.core;

import com.chm.converter.core.utils.MapUtil;

import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
public interface ClassInfoStorage {

    Map<Class<?>, Map<Class<? extends Converter>, JavaBeanInfo>> BEAN_INFO_MAP = MapUtil.newHashMap();

    Map<Class<?>, Map<Class<? extends Converter>, Map<String, FieldInfo>>> NAME_FIELD_INFO_MAP = MapUtil.newHashMap();

    Map<Class<?>, Map<Class<? extends Converter>, Map<String, FieldInfo>>> FIELD_NAME_FIELD_INFO_MAP = MapUtil.newHashMap();

    ClassInfoStorage INSTANCE = (clazz, converterClass) -> contains(BEAN_INFO_MAP, clazz, converterClass);

    /**
     * 初始化class信息
     *
     * @param clazz
     * @param converterClass
     */
    default <T> void initClassInfo(Class<T> clazz, Class<? extends Converter> converterClass) {
        JavaBeanInfo<T> javaBeanInfo = JavaBeanInfo.build(clazz, converterClass);
        put(BEAN_INFO_MAP, clazz, converterClass, javaBeanInfo);
        put(NAME_FIELD_INFO_MAP, clazz, converterClass, javaBeanInfo.getNameFieldInfoMap());
        put(FIELD_NAME_FIELD_INFO_MAP, clazz, converterClass, javaBeanInfo.getFieldNameFieldInfoMap());
    }

    /**
     * 获取bean信息
     *
     * @param clazz
     * @param converterClass
     * @return
     */
    default <T> JavaBeanInfo<T> getJavaBeanInfo(Class<T> clazz, Class<? extends Converter> converterClass) {
        if (!contains(BEAN_INFO_MAP, clazz, converterClass) || !isInit(clazz, converterClass)) {
            initClassInfo(clazz, converterClass);
        }
        return get(BEAN_INFO_MAP, clazz, converterClass);
    }

    /**
     * 是否初始化过javaBeanInfo
     *
     * @param clazz
     * @param converterClass
     * @return
     */
    boolean isInit(Class<?> clazz, Class<? extends Converter> converterClass);

    /**
     * 获取field信息
     *
     * @param clazz
     * @param converterClass
     * @return
     */
    default Map<String, FieldInfo> getNameFieldInfoMap(Class<?> clazz, Class<? extends Converter> converterClass) {
        if (!contains(NAME_FIELD_INFO_MAP, clazz, converterClass) || !isInit(clazz, converterClass)) {
            initClassInfo(clazz, converterClass);
        }
        return get(NAME_FIELD_INFO_MAP, clazz, converterClass);
    }


    /**
     * 获取field信息
     *
     * @param clazz
     * @param converterClass
     * @return
     */
    default Map<String, FieldInfo> getFieldNameFieldInfoMap(Class<?> clazz, Class<? extends Converter> converterClass) {
        if (!contains(FIELD_NAME_FIELD_INFO_MAP, clazz, converterClass) || !isInit(clazz, converterClass)) {
            initClassInfo(clazz, converterClass);
        }
        return get(FIELD_NAME_FIELD_INFO_MAP, clazz, converterClass);
    }

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


}

