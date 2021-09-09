package com.chm.converter.core;

import com.chm.converter.utils.MapUtil;

import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
public interface ClassInfoStorage {

    Map<Class<?>, JavaBeanInfo> BEAN_INFO_MAP = MapUtil.newConcurrentHashMap();

    Map<Class<?>, Map<String, FieldInfo>> NAME_FIELD_INFO_MAP = MapUtil.newConcurrentHashMap();

    Map<Class<?>, Map<String, FieldInfo>> FIELD_NAME_FIELD_INFO_MAP = MapUtil.newConcurrentHashMap();

    ClassInfoStorage INSTANCE = BEAN_INFO_MAP::containsKey;

    /**
     * 初始化class信息
     *
     * @param clazz
     */
    default void initClassInfo(Class<?> clazz) {
        JavaBeanInfo javaBeanInfo = JavaBeanInfo.build(clazz);
        BEAN_INFO_MAP.put(clazz, javaBeanInfo);
        NAME_FIELD_INFO_MAP.put(clazz, javaBeanInfo.getNameFieldInfoMap());
        FIELD_NAME_FIELD_INFO_MAP.put(clazz, javaBeanInfo.getFieldNameFieldInfoMap());
    }

    /**
     * 获取bean信息
     *
     * @param clazz
     * @return
     */
    default JavaBeanInfo getJavaBeanInfo(Class<?> clazz) {
        if (!BEAN_INFO_MAP.containsKey(clazz) || !isInit(clazz)) {
            initClassInfo(clazz);
        }
        return BEAN_INFO_MAP.get(clazz);
    }

    /**
     * 是否初始化过javaBeanInfo
     *
     * @param clazz
     * @return
     */
    boolean isInit(Class<?> clazz);

    /**
     * 获取field信息
     *
     * @param clazz
     * @return
     */
    default Map<String, FieldInfo> getNameFieldInfoMap(Class<?> clazz) {
        if (!NAME_FIELD_INFO_MAP.containsKey(clazz) || !isInit(clazz)) {
            initClassInfo(clazz);
        }
        return NAME_FIELD_INFO_MAP.get(clazz);
    }


    /**
     * 获取field信息
     *
     * @param clazz
     * @return
     */
    default Map<String, FieldInfo> getFieldNameFieldInfoMap(Class<?> clazz) {
        if (!FIELD_NAME_FIELD_INFO_MAP.containsKey(clazz) || !isInit(clazz)) {
            initClassInfo(clazz);
        }
        return FIELD_NAME_FIELD_INFO_MAP.get(clazz);
    }
}

