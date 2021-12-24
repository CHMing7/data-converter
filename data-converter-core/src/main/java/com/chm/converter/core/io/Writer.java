package com.chm.converter.core.io;

import java.io.IOException;

/**
 * 写操作接口类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-12-23
 **/
public interface Writer<T> {

    /**
     * 写入操作
     *
     * @param t
     * @throws IOException
     */
    void write(T t) throws IOException;
}
