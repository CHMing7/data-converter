package com.chm.converter.fst;

import cn.hutool.core.map.MapUtil;
import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.fst.factory.FstFactory;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

/**
 * 默认fst数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-27
 **/
public class DefaultFstConverter implements FstConverter {

    public static final String[] FST_NAME_ARRAY = new String[]{"org.nustaq.serialization.FSTObjectInput",
            "org.nustaq.serialization.FSTObjectOutput"};

    private final FstFactory factory = new FstFactory(this);

    public final ConstructorFactory constructorFactory = new ConstructorFactory(MapUtil.empty());

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        if (targetType.isInterface()) {
            T instance = constructorFactory.get(TypeToken.get(targetType)).construct();
            if (instance != null && targetType != instance.getClass()) {
                // 若不存在序列化器就重新赋予转换目标类
                targetType = (Class<T>) instance.getClass();
            }
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(source);
        FSTObjectInput objectInput = factory.getObjectInput(byteArrayInputStream);
        try {
            return (T) objectInput.readObject(targetType);
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), byte[].class.getName(), targetType.getName(), e);
        }
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FSTObjectOutput objectOutput = factory.getObjectOutput(byteArrayOutputStream);
        try {
            objectOutput.writeObject(source, source.getClass());
            objectOutput.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), source.getClass().getName(), byte[].class.getName(), e);
        }
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Fst相关类型是否存在
            for (String fstName : FST_NAME_ARRAY) {
                Class.forName(fstName);
            }
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
