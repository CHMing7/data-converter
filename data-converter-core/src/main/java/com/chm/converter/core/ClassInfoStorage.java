package com.chm.converter.core;

import cn.hutool.core.map.MapUtil;

import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
public interface ClassInfoStorage {

    Map<Class<?>, JavaBeanInfo> BEAN_INFO_MAP = MapUtil.newConcurrentHashMap();

    Map<Class<?>, Map<String, FieldInfo>> FIELD_INFO_MAP = MapUtil.newConcurrentHashMap();

    ClassInfoStorage INSTANCE = new ClassInfoStorage() {
    };

    /**
     * 初始化class信息
     *
     * @param clazz
     */
    default void initClassInfo(Class<?> clazz) {
        JavaBeanInfo javaBeanInfo = JavaBeanInfo.build(clazz);
        BEAN_INFO_MAP.put(clazz, javaBeanInfo);
        FIELD_INFO_MAP.put(clazz, javaBeanInfo.getFieldInfoMap());
    }

    /**
     * 获取bean信息
     *
     * @param clazz
     * @return
     */
    default JavaBeanInfo getJavaBeanInfo(Class<?> clazz) {
        if (!BEAN_INFO_MAP.containsKey(clazz)) {
            initClassInfo(clazz);
        }
        return BEAN_INFO_MAP.get(clazz);
    }


    /**
     * 获取field信息
     *
     * @param clazz
     * @return
     */
    default Map<String, FieldInfo> getFieldInfoMap(Class<?> clazz) {
        if (!FIELD_INFO_MAP.containsKey(clazz)) {
            initClassInfo(clazz);
        }
        return FIELD_INFO_MAP.get(clazz);
    }
}

