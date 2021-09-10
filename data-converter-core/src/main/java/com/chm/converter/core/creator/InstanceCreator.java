package com.chm.converter.core.creator;

import java.lang.reflect.Type;

/**
 * 实例创建接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-07
 **/
public interface InstanceCreator<T> {

    /**
     * 创建给定类型的实例
     *
     * @param type
     * @return
     */
    T createInstance(Type type);
}
