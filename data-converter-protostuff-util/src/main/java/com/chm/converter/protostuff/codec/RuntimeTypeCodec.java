package com.chm.converter.protostuff.codec;

import com.chm.converter.core.universal.UniversalGenerate;
import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * 运行时动态类型
 *
 * @author caihongming
 * @version v1.0
 * @date 2021-11-19
 **/
public class RuntimeTypeCodec<T> extends BaseProtostuffCodec<T> {

    private final UniversalGenerate<ProtostuffCodec> generate;

    private final ProtostuffCodec<T> delegate;

    private final Type type;

    public RuntimeTypeCodec(UniversalGenerate<ProtostuffCodec> generate, ProtostuffCodec<T> delegate, Type type) {
        super(delegate.typeToken, delegate.typeToken.getRawType().getName());
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
            chosen = generate.get(runtimeType);
        }
        chosen.writeTo(output, message);
    }

    @Override
    public T mergeFrom(Input input) throws IOException {
        return delegate.mergeFrom(input);
    }

    public ProtostuffCodec getRuntimeCodec(Object message) {
        Type runtimeType = getRuntimeTypeIfMoreSpecific(type, message);
        if (runtimeType != type) {
            return generate.get(runtimeType);
        }
        return null;
    }

    /**
     * 找到运行时具体类型
     */
    private Type getRuntimeTypeIfMoreSpecific(Type type, Object value) {
        if (value != null && (type instanceof TypeVariable<?> || type instanceof Class<?>)) {
            type = value.getClass();
        }
        return type;
    }

    public ProtostuffCodec<T> getDelegate() {
        return delegate;
    }

    @Override
    public RuntimeTypeCodec<T> newInstance() {
        return new RuntimeTypeCodec<>(this.generate, this.delegate, this.type);
    }
}
