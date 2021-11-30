package com.chm.converter.core.utils;

import com.chm.converter.core.handler.BranchHandle;
import com.chm.converter.core.handler.PresentOrElseHandler;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-29
 **/
public class HandlerUtil {

    /**
     * 参数为true或false时，分别进行不同的操作
     *
     * @param b
     * @return BranchHandle
     * @see com.chm.converter.core.handler.BranchHandle
     **/
    public static BranchHandle isTureOrFalse(boolean b) {

        return (trueHandle, falseHandle) -> {
            if (b) {
                trueHandle.run();
            } else {
                falseHandle.run();
            }
        };
    }

    /**
     * 字符串为blank时，分别进行不同的操作
     *
     * @param str
     * @return PresentOrElseHandler
     * @see com.chm.converter.core.handler.PresentOrElseHandler
     **/
    public static PresentOrElseHandler<?> isBlankOrNoBlank(String str) {

        return (consumer, runnable) -> {
            if (StringUtil.isBlank(str)) {
                runnable.run();
            } else {
                consumer.accept(str);
            }
        };
    }
}
