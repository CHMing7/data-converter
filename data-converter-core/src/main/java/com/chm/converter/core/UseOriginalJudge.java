package com.chm.converter.core;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-08
 **/
@FunctionalInterface
public interface UseOriginalJudge {

    /**
     * 判断使用使用原装实现
     *
     * @param cls
     * @return
     */
    boolean useOriginalImpl(Class<?> cls);
}
