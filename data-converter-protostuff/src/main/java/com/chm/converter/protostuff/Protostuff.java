package com.chm.converter.protostuff;

import com.chm.converter.core.Converter;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.protostuff.codec.ByteArrayInput;
import com.chm.converter.protostuff.codec.ProtostuffCodec;
import com.chm.converter.protostuff.codec.ProtostuffCodecGenerate;
import io.protostuff.Input;
import io.protostuff.LinkedBuffer;
import io.protostuff.Message;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.ProtostuffOutput;
import io.protostuff.Schema;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-18
 **/
public class Protostuff {

    private final ProtostuffCodecGenerate protostuffCodecGenerate;

    public Protostuff(Converter<?> converter) {
        this.protostuffCodecGenerate = ProtostuffCodecGenerate.newDefault(converter);
    }


    public static boolean isSupported(Type type) {
        if (type == null) {
            return false;
        }
        Class<?> cls = ClassUtil.getClassByType(type);
        if (Message.class.isAssignableFrom(cls)) {
            return true;
        }
        return false;
    }

    public byte[] serialize(Object value) throws IOException {
        if (value == null) {
            return new byte[0];
        }
        Class<?> cls = value.getClass();
        if (isSupported(cls)) {
            Message message = (Message) value;
            return ProtostuffIOUtil.toByteArray(value, message.cachedSchema(), LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        }
        ProtostuffOutput output = new ProtostuffOutput(LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        ProtostuffCodec protostuffCodec = protostuffCodecGenerate.get(cls);
        protostuffCodec.writeTo(output, value);

        return output.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] source, Type targetType) throws IOException {
        if (isSupported(targetType)) {
            Class<?> cls = ClassUtil.getClassByType(targetType);
            Message message = (Message) createMessageInstance(cls);
            Schema<T> schema = message.cachedSchema();
            T result = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(source, result, schema);
            return result;
        }
        ProtostuffCodec<T> protostuffCodec = protostuffCodecGenerate.get(targetType);
        Input input = new ByteArrayInput(new io.protostuff.ByteArrayInput(source, true));
        return protostuffCodec.mergeFrom(input);
    }


    private static <T> T createMessageInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException e) {
            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (Exception e1) {
                throw new RuntimeException(e);
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }


}
