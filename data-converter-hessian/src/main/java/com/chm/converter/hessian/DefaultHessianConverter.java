package com.chm.converter.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.core.utils.StringUtil;
import com.chm.converter.hessian.factory.Java8TimeConverterFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-17
 **/
public class DefaultHessianConverter implements HessianConverter {

    public static final String[] HESSIAN_NAME_ARRAY = new String[]{"com.caucho.hessian.io.Hessian2Input",
            "com.caucho.hessian.io.Hessian2Output"};

    private final SerializerFactory serializerFactory = new SerializerFactory();

    {
        serializerFactory.addFactory(Java8TimeConverterFactory.get());
    }

    public SerializerFactory getSerializerFactory() {
        return serializerFactory;
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        try {
            return deserializer(source, targetType);
        } catch (IOException e) {
            throw new ConvertException(StringUtil.format("data cannot be deserialized to hessian, data type: {}", source.getClass()), e);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        Class<?> cls = ClassUtil.getClassByType(targetType);
        try {
            return deserializer(source, cls);
        } catch (IOException e) {
            throw new ConvertException(StringUtil.format("data cannot be deserialized to hessian, data type: {}", source.getClass()), e);
        }
    }

    private <T> T deserializer(byte[] source, Class<?> cls) throws IOException {
        if (source == null) {
            return null;
        }
        InputStream is = new ByteArrayInputStream(source);
        Hessian2Input hi = new Hessian2Input(is);
        hi.setSerializerFactory(serializerFactory);
        return (T) hi.readObject(cls);
    }

    @Override
    public byte[] encode(Object source) {
        if (source == null) {
            return new byte[0];
        }
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Hessian2Output ho = new Hessian2Output(os);
            ho.setSerializerFactory(serializerFactory);
            ho.writeObject(source);
            ho.flush();
            return os.toByteArray();
        } catch (IOException e) {
            throw new ConvertException(StringUtil.format("data cannot be serialized to hessian, data type: {}", source.getClass()), e);
        }
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Hessian相关类型是否存在
            for (String hessianName : HESSIAN_NAME_ARRAY) {
                Class.forName(hessianName);
            }
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}