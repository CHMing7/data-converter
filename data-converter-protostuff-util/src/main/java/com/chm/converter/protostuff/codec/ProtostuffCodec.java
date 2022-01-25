package com.chm.converter.protostuff.codec;

import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.creator.ObjectConstructor;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalInterface;
import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Schema;

import java.io.IOException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-11
 **/
public abstract class ProtostuffCodec<T> implements UniversalInterface, Schema<T> {

    protected final Class<T> clazz;

    protected final ObjectConstructor<T> constructor;

    protected ProtostuffCodec(Class<T> clazz) {
        this.clazz = clazz;
        this.constructor = ConstructorFactory.INSTANCE.get(TypeToken.get(clazz));
    }

    @Override
    public boolean isInitialized(T message) {
        return true;
    }

    @Override
    public String messageName() {
        return clazz.getSimpleName();
    }

    @Override
    public String messageFullName() {
        return clazz.getName();
    }

    @Override
    public Class<? super T> typeClass() {
        return clazz;
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
}
