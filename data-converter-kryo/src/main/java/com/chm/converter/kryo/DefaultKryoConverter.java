package com.chm.converter.kryo;

import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.kryo.utils.KryoUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-26
 **/
public class DefaultKryoConverter implements KryoConverter {

    public static final String KRYO_NAME = "com.esotericsoftware.kryo.Kryo";

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        Kryo kryo = KryoUtil.get();
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
        Kryo kryo = KryoUtil.get();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObjectOrNull(output, source, source.getClass());
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Avro相关类型是否存在
            Class.forName(KRYO_NAME);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
