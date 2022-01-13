package com.chm.converter.core.utils;

import cn.hutool.core.io.IORuntimeException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-07
 **/
public class IoUtil {


    /**
     * 读取指定长度的byte数组，不关闭流
     *
     * @param in     {@link InputStream}，为null返回null
     * @param length 长度，小于等于0返回空byte数组
     * @return bytes
     * @throws IORuntimeException IO异常
     */
    public static byte[] readBytes(InputStream in, int length) throws IORuntimeException {
        if (null == in) {
            return null;
        }
        if (length <= 0) {
            return new byte[0];
        }

        byte[] b = new byte[length];
        int readLength;
        try {
            readLength = in.read(b);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        if (readLength > 0 && readLength < length) {
            byte[] b2 = new byte[readLength];
            System.arraycopy(b, 0, b2, 0, readLength);
            return b2;
        } else {
            return b;
        }
    }
}
