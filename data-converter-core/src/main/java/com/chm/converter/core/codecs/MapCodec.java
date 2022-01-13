package com.chm.converter.core.codecs;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.creator.ObjectConstructor;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-12
 **/
public class MapCodec<T extends Map> implements Codec<T, T> {

    protected final Type type;

    protected final ObjectConstructor<T> constructor;

    private final Codec kCodec;

    private final Codec vCodec;

    public MapCodec(TypeToken<T> type, Codec kCodec, Codec vCodec) {
        this.type = type.getType();
        this.constructor = ConstructorFactory.INSTANCE.get(type);
        this.kCodec = kCodec;
        this.vCodec = vCodec;
    }

    @Override
    public T encode(T t) {
        T result = constructor.construct();
        t.forEach((k, v) -> result.put(kCodec.encode(k), vCodec.encode(v)));
        return result;
    }

    @Override
    public TypeToken getEncodeType() {
        return TypeToken.getParameterized(ClassUtil.getClassByType(type), kCodec.getEncodeType().getType(), vCodec.getEncodeType().getType());
    }

    @Override
    public void writeData(T t, DataWriter dw) throws IOException {
        dw.writeMap(t);
    }

    @Override
    public T decode(T t) {
        T result = constructor.construct();
        t.forEach((k, v) -> result.put(kCodec.decode(k), vCodec.decode(v)));
        return result;
    }

    @Override
    public TypeToken getDecodeType() {
        return TypeToken.getParameterized(ClassUtil.getClassByType(type), kCodec.getDecodeType().getType(), vCodec.getDecodeType().getType());
    }

    @Override
    public T readData(DataReader dr) throws IOException {
        return (T) dr.readMap(getEncodeType());
    }
}
