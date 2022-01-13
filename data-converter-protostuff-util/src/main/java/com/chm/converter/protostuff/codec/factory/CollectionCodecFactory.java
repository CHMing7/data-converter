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
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 集合类编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-30
 **/
public class CollectionCodecFactory implements UniversalFactory<ProtostuffCodec> {

    public static final String FIELD_NAME_VALUE = "v";

    @Override
    public ProtostuffCodec create(UniversalGenerate<ProtostuffCodec> generate, TypeToken<?> typeToken) {
        Class<?> rawTypeOfSrc = typeToken.getRawType();
        if (!Collection.class.isAssignableFrom(rawTypeOfSrc)) {
            return null;
        }
        Type type = typeToken.getType();
        Type elementType = ConverterTypes.getCollectionElementType(type, rawTypeOfSrc);
        ProtostuffCodec elementCodec = new RuntimeTypeCodec(generate, generate.get(elementType), elementType);
        return new CollectionCodec(typeToken.getRawType(), elementCodec);
    }

    public static class CollectionCodec<V> extends ProtostuffCodec<Collection<V>> {

        private final ProtostuffCodec<V> elementCodec;

        protected CollectionCodec(Class<Collection<V>> clazz, ProtostuffCodec<V> elementCodec) {
            super(clazz);
            this.elementCodec = elementCodec;
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
        public void writeTo(Output output, Collection<V> message) throws IOException {
            for (V value : message) {
                // null values not serialized.
                if (value != null) {
                    output.writeObject(1, value, elementCodec, true);
                }
            }
        }


        @Override
        public Collection<V> mergeFrom(Input input) throws IOException {
            Collection<V> collection = constructor.construct();
            for (int number = input.readFieldNumber(this); ; number = input.readFieldNumber(this)) {
                switch (number) {
                    case 0:
                        return collection;
                    case 1:
                        collection.add(input.mergeObject(null, elementCodec));
                        break;
                    default:
                        throw new ProtostuffException("The collection was incorrectly " +
                                "serialized.");
                }
            }
        }
    }
}
