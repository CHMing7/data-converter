package com.chm.converter.core.codecs;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.creator.ObjectConstructor;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-07
 **/
public class CollectionCodec<T extends Collection> implements Codec<T, T> {

    protected final Class<T> clazz;

    protected final ObjectConstructor<T> constructor;
    protected final Type elementType;
    private final Codec elementCodec;

    public CollectionCodec(Class<T> clazz, Codec elementCodec, Type elementType) {
        this.clazz = clazz;
        this.constructor = ConstructorFactory.INSTANCE.get(TypeToken.get(clazz));
        this.elementCodec = elementCodec;
        this.elementType = elementType;
    }

    @Override
    public T encode(T t) {
        T result = constructor.construct();
        for (Object o : t) {
            result.add(elementCodec.encode(o));
        }
        return result;
    }

    @Override
    public TypeToken getEncodeType() {
        return TypeToken.getParameterized(clazz, elementCodec.getEncodeType().getType());
    }

    @Override
    public void writeData(T t, DataWriter dw) throws IOException {
        dw.writeCollection(t);
    }

    @Override
    public T decode(T t) {
        T result = constructor.construct();
        for (Object o : t) {
            result.add(elementCodec.decode(o));
        }
        return result;
    }

    @Override
    public TypeToken<T> getDecodeType() {
        return TypeToken.getParameterized(clazz, elementCodec.getDecodeType().getType());
    }

    @Override
    public T readData(DataReader dr) throws IOException {
        return (T) dr.readCollection(getEncodeType());
    }
}
