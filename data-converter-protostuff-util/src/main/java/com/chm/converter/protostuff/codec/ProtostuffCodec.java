package com.chm.converter.protostuff.codec;

import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.creator.ObjectConstructor;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalInterface;
import com.chm.converter.protostuff.codec.factory.ArrayCodecFactory;
import com.chm.converter.protostuff.codec.factory.CollectionCodecFactory;
import com.chm.converter.protostuff.codec.factory.JavaBeanCodecFactory;
import com.chm.converter.protostuff.codec.factory.MapCodecFactory;
import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Schema;

import java.io.IOException;

/**
 * @author caihongming
 * @version v1.0
 * @date 2021-11-11
 **/
public abstract class ProtostuffCodec<T> implements UniversalInterface, Schema<T> {

    protected final TypeToken<T> typeToken;

    protected final ObjectConstructor<T> constructor;

    protected int fieldNumber = -1;

    protected boolean isField = false;

    protected ProtostuffCodec(TypeToken<T> typeToken) {
        this.typeToken = typeToken;
        this.constructor = ConstructorFactory.INSTANCE.get(typeToken);
    }

    @Override
    public boolean isInitialized(T message) {
        return true;
    }

    @Override
    public String messageName() {
        return typeToken.getRawType().getSimpleName();
    }

    @Override
    public String messageFullName() {
        return typeToken.getRawType().getName();
    }

    @Override
    public Class<? super T> typeClass() {
        return typeToken.getRawType();
    }

    /**
     * Writes the value to the {@code output}.
     *
     * @param output
     * @param message
     * @throws IOException
     */
    @Override
    public abstract void writeTo(Output output, T message) throws IOException;

    /**
     * Reads the value into the {@code message}.
     *
     * @param input
     * @return
     * @throws IOException
     */
    public abstract T mergeFrom(Input input) throws IOException;

    /**
     * Serializes a message/object to the {@link Output output}.
     */
    @Override
    public void mergeFrom(Input input, T message) throws IOException {
    }

    @Override
    public T newMessage() {
        return constructor.construct();
    }

    public abstract ProtostuffCodec<T> newInstance();

    public ProtostuffCodec<T> withFieldNumber(int fieldNumber) {
        ProtostuffCodec<T> newInstance = newInstance();
        newInstance.setFieldNumber(fieldNumber);
        newInstance.setField(true);
        return newInstance;
    }

    public int getFieldNumber() {
        return fieldNumber;
    }

    public void setFieldNumber(int fieldNumber) {
        this.fieldNumber = fieldNumber;
    }

    public boolean isField() {
        return isField;
    }

    public void setField(boolean field) {
        isField = field;
    }

    public static void write(ProtostuffCodec codec, Output output, Object o, int fieldNumber) throws IOException {
        if (codec instanceof RuntimeTypeCodec) {
            codec = ((RuntimeTypeCodec<?>) codec).getRuntimeCodec(o);
        }
        if (codec instanceof JavaBeanCodecFactory.JavaBeanCodec ||
                codec instanceof CollectionCodecFactory.CollectionCodec ||
                codec instanceof MapCodecFactory.MapCodec ||
                codec instanceof ArrayCodecFactory.ArrayCodec) {
            output.writeObject(fieldNumber, o, codec, false);
        } else {
            codec.writeTo(output, o);
        }
    }

    public static Object merge(ProtostuffCodec codec, Input input) throws IOException {
        if (codec instanceof RuntimeTypeCodec) {
            codec = ((RuntimeTypeCodec<?>) codec).getDelegate();
        }
        if (codec instanceof JavaBeanCodecFactory.JavaBeanCodec ||
                codec instanceof CollectionCodecFactory.CollectionCodec ||
                codec instanceof MapCodecFactory.MapCodec ||
                codec instanceof ArrayCodecFactory.ArrayCodec) {
            return input.mergeObject(null, codec);
        } else {
            return codec.mergeFrom(input);
        }
    }
}
