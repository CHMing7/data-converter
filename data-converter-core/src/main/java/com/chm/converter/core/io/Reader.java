package com.chm.converter.core.io;

import java.io.IOException;

/**
 * 读操作接口类
 *
 * @author caihongming
 * @version v1.0
 * @date 2021-12-23
 **/
public interface Reader<T> {

    /**
     * 读取操作
     *
     * @return
     * @throws IOException
     */
    T read() throws IOException;
}
