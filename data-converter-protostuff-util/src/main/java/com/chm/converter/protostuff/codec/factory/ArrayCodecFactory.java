package com.chm.converter.protostuff.codec.factory;

import com.chm.converter.core.reflect.ConverterTypes;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.protostuff.codec.ProtostuffCodec;
import com.chm.converter.protostuff.codec.RuntimeTypeCodec;
import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.ProtostuffException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 数组类编解码
 *
 * @author caihongming
 * @version v1.0
 * @date 2021-11-30
 **/
public class ArrayCodecFactory implements UniversalFactory<ProtostuffCodec> {

    public static final String FIELD_NAME_VALUE = "v";

    @Override
    public ProtostuffCodec create(UniversalGenerate<ProtostuffCodec> generate, TypeToken<?> typeToken) {
        Type type = typeToken.getType();
        if (!(type instanceof GenericArrayType || type instanceof Class && ((Class<?>) type).isArray())) {
            return null;
        }

        Type componentType = ConverterTypes.getArrayComponentType(type);
        ProtostuffCodec componentTypeCodec = new RuntimeTypeCodec(generate, generate.get(componentType), componentType);
        return new ArrayCodec(typeToken, componentTypeCodec, TypeToken.get(componentType));
    }

    public static class ArrayCodec<E> extends ProtostuffCodec<Object> {

        private final ProtostuffCodec<E> componentTypeCodec;

        private final TypeToken<E> componentType;

        protected ArrayCodec(TypeToken<Object> typeToken, ProtostuffCodec<E> componentTypeCodec, TypeToken<E> componentType) {
            super(typeToken);
            this.componentTypeCodec = componentTypeCodec;
            this.componentType = componentType;
        }

        @Override
        public String getFieldName(int number) {
            return number == 1 ? FIELD_NAME_VALUE : null;
        }

        @Override
        public int getFieldNumber(String name) {
            return name.length() == 1 && name.charAt(0) == 'v' ? 1 : 0;
        }

        @Override
        public void writeTo(Output output, Object array) throws IOException {
            for (int i = 0, length = Array.getLength(array); i < length; i++) {
                E value = (E) Array.get(array, i);
                if (value != null) {
                    output.writeObject(1, value, componentTypeCodec, true);
                }
            }
        }

        @Override
        public Object mergeFrom(Input input) throws IOException {
            List<E> list = new ArrayList<>();
            for (int number = input.readFieldNumber(this); ; number = input.readFieldNumber(this)) {
                switch (number) {
                    case 0:
                        int size = list.size();
                        Object array = Array.newInstance(componentType.getRawType(), size);
                        for (int i = 0; i < size; i++) {
                            Array.set(array, i, list.get(i));
                        }
                        return array;
                    case 1:
                        E instance = input.mergeObject(null, componentTypeCodec);
                        list.add(instance);
                        break;
                    default:
                        throw new ProtostuffException("The collection was incorrectly " +
                                "serialized.");
                }
            }
        }

        @Override
        public ArrayCodec<E> newInstance() {
            return new ArrayCodec<>(this.typeToken, this.componentTypeCodec, this.componentType);
        }
    }
}
