package com.chm.converter.core.codecs;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Array;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-12
 **/
public class ArrayCodec implements Codec {

    private final Codec componentTypeCodec;

    private final TypeToken componentType;

    private final Class<?> componentEncodeClass;

    private final Class<?> componentDecodeClass;

    public ArrayCodec(Codec componentTypeCodec, TypeToken componentType) {
        this.componentTypeCodec = componentTypeCodec;
        this.componentType = componentType;
        this.componentEncodeClass = ClassUtil.getClassByType(componentTypeCodec.getEncodeType().getRawType());
        this.componentDecodeClass = ClassUtil.getClassByType(componentTypeCodec.getDecodeType().getRawType());
    }

    @Override
    public Object encode(Object array) {
        int size = Array.getLength(array);
        Object resultArray = Array.newInstance(componentEncodeClass, size);
        for (int i = 0; i < size; i++) {
            Object value = Array.get(array, i);
            Array.set(resultArray, i, componentTypeCodec.encode(value));
        }
        return resultArray;
    }

    @Override
    public TypeToken getEncodeType() {
        return TypeToken.get(Array.newInstance(componentEncodeClass, 0).getClass());
    }

    @Override
    public void writeData(Object array, DataWriter dw) throws IOException {
        dw.writeArray((Object[]) array);
    }

    @Override
    public Object decode(Object array) {
        int size = Array.getLength(array);
        Object resultArray = Array.newInstance(componentDecodeClass, size);
        for (int i = 0; i < size; i++) {
            Object value = Array.get(array, i);
            Array.set(resultArray, i, componentTypeCodec.decode(value));
        }
        return resultArray;
    }

    @Override
    public TypeToken getDecodeType() {
        return TypeToken.get(Array.newInstance(componentDecodeClass, 0).getClass());
    }

    @Override
    public Object[] readData(DataReader dr) throws IOException {
        return dr.readArray(componentType);
    }
}