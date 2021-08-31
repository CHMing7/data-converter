package com.chm.converter.core;

import java.lang.reflect.Type;

/**
 * 数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-08-13
 **/
public interface Converter<S> {

    /**
     * 将源数据转换为目标类型（Class）的java对象
     *
     * @param source     源数据
     * @param targetType 目标类型 (Class对象)
     * @param <T>        目标类型泛型
     * @return 转换后的目标类型对象
     */
    <T> T convertToJavaObject(S source, Class<T> targetType);

    /**
     * 将源数据转换为目标类型（Type）的java对象
     *
     * @param source     源数据
     * @param targetType 目标类型 (Type对象)
     * @param <T>        目标类型泛型
     * @return 转换后的目标类型对象
     */
    <T> T convertToJavaObject(S source, Type targetType);

    /**
     * 将java对象转化成字符串
     *
     * @param obj
     * @return
     */
    S encodeToString(Object obj);
}