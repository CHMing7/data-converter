package com.chm.converter.binary;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.chm.converter.Converter;
import com.chm.converter.exceptions.ConvertException;
import com.chm.converter.utils.ByteEncodeUtils;
import com.chm.converter.utils.ReflectUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * 默认的二进制数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
public class DefaultBinaryConverter implements Converter<Object> {

    @Override
    public <T> T convertToJavaObject(Object source, Class<T> targetType) {
        if (source instanceof InputStream) {
            InputStream in = (InputStream) source;
            if (InputStream.class.isAssignableFrom(targetType)) {
                return (T) source;
            }
            if (byte[].class.isAssignableFrom(targetType)) {
                return (T) inputStreamToByteArray(in);
            }
            if (String.class.isAssignableFrom(targetType)) {
                byte[] tmp = inputStreamToByteArray(in);
                String result = null;
                String encode = ByteEncodeUtils.getCharsetName(tmp);
                if (encode.toUpperCase().startsWith("GB")) {
                    encode = "GBK";
                }
                result = StrUtil.str(tmp, encode);
                return (T) result;
            }
        } else if (source instanceof File) {
            File file = (File) source;
            if (File.class.isAssignableFrom(targetType)) {
                return (T) file;
            }
            if (InputStream.class.isAssignableFrom(targetType)) {
                return (T) FileUtil.getInputStream(file);
            }
            if (byte[].class.isAssignableFrom(targetType)) {
                return (T) FileUtil.readBytes(file);
            }
            if (String.class.isAssignableFrom(targetType)) {
                return (T) FileUtil.readString(file, (String) null);
            }
        }
        return convertToJavaObjectEx(source, targetType);
    }


    protected <T> T convertToJavaObjectEx(Object source, Class<T> targetType) {
        return null;
    }


    private byte[] inputStreamToByteArray(InputStream in) {
        byte[] tmp = new byte[4096];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int len;
            while ((len = in.read(tmp)) != -1) {
                out.write(tmp, 0, len);
            }
            out.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new ConvertException("binary", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new ConvertException("binary", e);
            }
        }
    }

    @Override
    public <T> T convertToJavaObject(Object source, Type targetType) {
        Class<?> clazz = ReflectUtils.getClassByType(targetType);
        return (T) convertToJavaObject(source, clazz);
    }

}
