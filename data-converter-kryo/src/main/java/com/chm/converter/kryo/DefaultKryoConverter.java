package com.chm.converter.kryo;

import com.chm.converter.core.Converter;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.kryo.factory.AbstractKryoFactory;
import com.chm.converter.kryo.factory.ThreadLocalKryoFactory;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.auto.service.AutoService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

/**
 * 默认kryo数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-26
 **/
@AutoService(Converter.class)
public class DefaultKryoConverter implements KryoConverter {

    public static final String KRYO_NAME = "com.esotericsoftware.kryo.Kryo";

    protected AbstractKryoFactory kryoFactory = new ThreadLocalKryoFactory(this);

    /**
     * 获取KryoFactory对象
     *
     * @return KryoFactory对象，{@link AbstractKryoFactory}类实例
     */
    public AbstractKryoFactory getKryoFactory() {
        return kryoFactory;
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        Kryo kryo = kryoFactory.getKryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(source);
        Input input = new Input(byteArrayInputStream);
        input.close();
        return kryo.readObjectOrNull(input, targetType);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        Class cls = ClassUtil.getClassByType(targetType);
        return (T) convertToJavaObject(source, cls);
    }

    @Override
    public byte[] encode(Object source) {
        if (source == null) {
            return new byte[0];
        }
        Kryo kryo = kryoFactory.getKryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObjectOrNull(output, source, source.getClass());
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Kryo相关类型是否存在
            Class.forName(KRYO_NAME);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
