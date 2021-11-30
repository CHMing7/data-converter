package com.chm.converter.protostuff.codec;

import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.protostuff.codec.factory.JavaBeanCodecFactory;
import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-19
 **/
public class RuntimeTypeCodec<T> extends BaseProtostuffCodec<T> {

    private final UniversalGenerate<ProtostuffCodec> generate;

    private final ProtostuffCodec<T> delegate;

    private final Type type;


    public RuntimeTypeCodec(UniversalGenerate<ProtostuffCodec> generate, ProtostuffCodec<T> delegate, Type type) {
        super(delegate.clazz, delegate.clazz.getSimpleName());
        this.generate = generate;
        this.delegate = delegate;
        this.type = type;
    }

    @Override
    public T newMessage() {
        return delegate.newMessage();
    }

    @Override
    public void writeTo(Output output, T message) throws IOException {
        ProtostuffCodec<T> chosen = delegate;
        Type runtimeType = getRuntimeTypeIfMoreSpecific(type, message);
        if (runtimeType != type) {
            ProtostuffCodec runtimeTypeAdapter = generate.get(TypeToken.get(runtimeType));
            if (!(runtimeTypeAdapter instanceof JavaBeanCodecFactory.JavaBeanCodec)) {
                // The user registered a type adapter for the runtime type, so we will use that
                chosen = runtimeTypeAdapter;
            } else if (!(delegate instanceof JavaBeanCodecFactory.JavaBeanCodec)) {
                // The user registered a type adapter for Base class, so we prefer it over the
                // reflective type adapter for the runtime type
                chosen = delegate;
            } else {
                // Use the type adapter for runtime type
                chosen = runtimeTypeAdapter;
            }
        }
        chosen.writeTo(output, message);
    }

    @Override
    public T mergeFrom(Input input) throws IOException {
        return delegate.mergeFrom(input);
    }


    /**
     * 找到运行时具体类型
     */
    private Type getRuntimeTypeIfMoreSpecific(Type type, Object value) {
        if (value != null && (type == Object.class || type instanceof TypeVariable<?> || type instanceof Class<?>)) {
            type = value.getClass();
        }
        return type;
    }

    @Override
    public int classId() {
        return 0;
    }
}
