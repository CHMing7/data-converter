package com.chm.converter.core;

/**
 * @author caihongming
 * @version v1.0
 * @date 2021-09-08
 **/
@FunctionalInterface
public interface UseRawJudge {

    /**
     * 判断使用使用原装实现
     *
     * @param cls
     * @return
     */
    boolean useRawImpl(Class<?> cls);
}
