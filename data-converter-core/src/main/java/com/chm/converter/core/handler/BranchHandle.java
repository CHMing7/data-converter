package com.chm.converter.core.handler;

/**
 * 分支处理接口
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-29
 **/
@FunctionalInterface
public interface BranchHandle {

    /**
     * 分支操作
     *
     * @param trueHandle  为true时要进行的操作
     * @param falseHandle 为false时要进行的操作
     * @return void
     **/
    void trueOrFalseHandle(Runnable trueHandle, Runnable falseHandle);
}
