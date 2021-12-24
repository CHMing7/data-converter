package com.chm.converter.protostuff.codec.factory;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.protostuff.codec.BaseProtostuffCodec;
import com.chm.converter.protostuff.codec.ProtostuffCodec;
import com.chm.converter.protostuff.codec.ProtostuffConstants;
import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.ProtostuffException;

import java.io.IOException;
import java.util.Map;

/**
 * 枚举类型编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-18
 **/
public class EnumFactory implements UniversalFactory<ProtostuffCodec> {

    private final Converter<?> converter;

    public EnumFactory(Converter<?> converter) {
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

    public static final class EnumCodec<T extends Enum<T>> extends BaseProtostuffCodec<T> {

        private final Class<T> classOfT;

        private final com.chm.converter.codec.EnumCodec<T> enumCodec;

        private final JavaBeanInfo<T> javaBeanInfo;

        private final Converter<?> converter;

        public EnumCodec(Class<T> classOfT, Converter<?> converter) {
            super(classOfT, "enumCodec");
            this.classOfT = classOfT;
            Class<? extends Converter> converterClass = converter != null ? converter.getClass() : null;
            this.javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz, converterClass);
            Map<String, String> aliasMap = javaBeanInfo.getFieldNameAliasMap();
            this.enumCodec = new com.chm.converter.codec.EnumCodec<>(classOfT, aliasMap);
            this.converter = converter;
        }

        @Override
        public T newMessage() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void writeTo(Output output, T message) throws IOException {
            String encode = enumCodec.encode(message);
            if (encode != null) {
                output.writeString(classId(), encode, false);
            }
        }

        @Override
        public T mergeFrom(Input input) throws IOException {
            if (classId() != input.readFieldNumber(this)) {
                throw new ProtostuffException("Corrupt input.");
            }
            T t = enumCodec.decode(input.readString());

            if (0 != input.readFieldNumber(this)) {
                throw new ProtostuffException("Corrupt input.");
            }
            return t;
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_ENUM;
        }
    }
}
