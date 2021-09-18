package com.chm.converter.binary;

import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ClassUtil;

import java.io.*;
import java.lang.reflect.Type;

/**
 * 默认的二进制数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-10
 **/
public class DefaultBinaryConverter implements BinaryConverter {

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        if (InputStream.class.isAssignableFrom(targetType)) {
            return (T) new ByteArrayInputStream(source);
        }
        if (byte[].class.isAssignableFrom(targetType)) {
            return (T) source;
        }
        if (String.class.isAssignableFrom(targetType)) {
            return (T) new String(source);
        }
        return convertToJavaObjectEx(source, targetType);
    }

    protected <T> T convertToJavaObjectEx(byte[] source, Class<T> targetType) {
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
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        Class<?> clazz = ClassUtil.getClassByType(targetType);
        return (T) convertToJavaObject(source, clazz);
    }

    @Override
    public byte[] encode(Object source) {
        if (source instanceof InputStream) {
            InputStream in = (InputStream) source;
            return inputStreamToByteArray(in);
        } else if (source instanceof String) {
            return ((String) source).getBytes();
        } else if (source instanceof File) {
            File file = (File) source;
            try {
                InputStream in = new FileInputStream(file);
                return inputStreamToByteArray(in);
            } catch (FileNotFoundException e) {
                throw new ConvertException("binary", e);
            }
        } else if (source instanceof byte[]) {
            return (byte[]) source;
        }
        return encodeEx(source);
    }

    protected byte[] encodeEx(Object source) {
        return null;
    }

    @Override
    public boolean checkCanBeLoad() {
        return true;
    }

}
