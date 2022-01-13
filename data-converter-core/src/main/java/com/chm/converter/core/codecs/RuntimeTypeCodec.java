package com.chm.converter.core.codecs;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalGenerate;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * 运行时动态类型
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-01-11
 **/
public class RuntimeTypeCodec<D, E> implements Codec<D, E> {

    private final UniversalGenerate<Codec> generate;

    private final Codec<D, E> delegate;

    private final Type type;

    public RuntimeTypeCodec(UniversalGenerate<Codec> generate, Codec<D, E> delegate, Type type) {
        this.generate = generate;
        this.delegate = delegate;
        this.type = type;
    }

    @Override
    public E encode(D d) {
        Codec<D, E> chosen = getRuntimeTypeCodec(d);
        return chosen.encode(d);
    }

    @Override
    public TypeToken<E> getEncodeType() {
        return delegate.getEncodeType();
    }

    @Override
    public void writeData(E e, DataWriter dw) throws IOException {
        Codec<D, E> chosen = getRuntimeTypeCodec(e);
        chosen.writeData(e, dw);
    }

    @Override
    public D decode(E e) {
        Codec<D, E> chosen = getRuntimeTypeCodec(e);
        return chosen.decode(e);
    }

    @Override
    public TypeToken<D> getDecodeType() {
        return delegate.getDecodeType();
    }

    @Override
    public E readData(DataReader dr) throws IOException {
        return delegate.readData(dr);
    }

    private Codec<D, E> getRuntimeTypeCodec(Object o) {
        Codec<D, E> chosen = delegate;
        Type runtimeType = getRuntimeTypeIfMoreSpecific(type, o);
        if (runtimeType != type) {
            Codec runtimeTypeAdapter = generate.get(TypeToken.get(runtimeType));
            if (!(runtimeTypeAdapter instanceof JavaBeanCodec)) {
                // The user registered a type adapter for the runtime type, so we will use that
                chosen = runtimeTypeAdapter;
            } else if (!(delegate instanceof JavaBeanCodec)) {
                // The user registered a type adapter for Base class, so we prefer it over the
                // reflective type adapter for the runtime type
                chosen = delegate;
            } else {
                // Use the type adapter for runtime type
                chosen = runtimeTypeAdapter;
            }
        }
        return chosen;
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

}
