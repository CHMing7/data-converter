package com.chm.converter.protostuff.codec.factory;

import com.chm.converter.core.Converter;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.protostuff.codec.BaseProtostuffCodec;
import com.chm.converter.protostuff.codec.ProtostuffCodec;
import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;

/**
 * 枚举类型编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-18
 **/
public class EnumCodecFactory implements UniversalFactory<ProtostuffCodec> {

    private final Converter<?> converter;

    public EnumCodecFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public ProtostuffCodec create(UniversalGenerate<ProtostuffCodec> generate, TypeToken<?> typeToken) {
        Class<?> rawType = typeToken.getRawType();
        if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
            return null;
        }
        if (!rawType.isEnum()) {
            // handle anonymous subclasses
            rawType = rawType.getSuperclass();
        }
        return new EnumCodec(rawType, converter);
    }

    public static final class EnumCodec<E extends Enum<E>> extends BaseProtostuffCodec<E> {

        private final com.chm.converter.core.codecs.EnumCodec<E> enumCodec;

        public EnumCodec(Class<E> classOfT, Converter<?> converter) {
            super(classOfT, "enumCodec");
            this.enumCodec = new com.chm.converter.core.codecs.EnumCodec<>(classOfT, converter);
        }

        @Override
        public E newMessage() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void writeTo(Output output, E message) throws IOException {
            enumCodec.write(message, encode -> {
                if (encode != null) {
                    output.writeString(this.fieldNumber, encode, false);
                }
            });
        }

        @Override
        public E mergeFrom(Input input) throws IOException {
            if (this.fieldNumber == -1) {
                input.readFieldNumber(this);
            }
            return this.enumCodec.read(input::readString);
        }

        @Override
        public EnumCodec<E> newInstance() {
            return new EnumCodec<>(this.clazz, this.enumCodec.getConverter());
        }
    }
}
