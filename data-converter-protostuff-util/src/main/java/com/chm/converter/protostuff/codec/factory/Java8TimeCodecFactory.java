package com.chm.converter.protostuff.codec.factory;

import com.chm.converter.core.Converter;
import com.chm.converter.core.constant.TimeConstant;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * java8时间类编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-15
 **/
public class Java8TimeCodecFactory implements UniversalFactory<ProtostuffCodec> {

    private final Converter<?> converter;

    public Java8TimeCodecFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public ProtostuffCodec create(UniversalGenerate<ProtostuffCodec> generate, TypeToken<?> typeToken) {
        if (TimeConstant.TEMPORAL_ACCESSOR_SET.contains(typeToken.getRawType())) {
            return new Java8TimeCodec(typeToken.getRawType(), (String) null, converter);
        }
        return null;
    }

    public static class Java8TimeCodec<T extends TemporalAccessor> extends BaseProtostuffCodec<T> {

        private final com.chm.converter.core.codecs.Java8TimeCodec<T> java8TimeCodec;

        public Java8TimeCodec(Class<T> clazz, String datePattern, Converter<?> converter) {
            super(clazz, "java8Time");
            this.java8TimeCodec = new com.chm.converter.core.codecs.Java8TimeCodec<>(clazz, datePattern, converter);
        }

        public Java8TimeCodec(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
            super(clazz, "java8Time");
            this.java8TimeCodec = new com.chm.converter.core.codecs.Java8TimeCodec<>(clazz, dateFormatter, converter);
        }

        public Java8TimeCodec<T> withClass(Class<T> clazz) {
            return new Java8TimeCodec<>(clazz, this.java8TimeCodec.getDateFormatter(), this.java8TimeCodec.getConverter());
        }

        public Java8TimeCodec<T> withDatePattern(String datePattern) {
            return new Java8TimeCodec<>(this.java8TimeCodec.getClazz(), datePattern, this.java8TimeCodec.getConverter());
        }

        public Java8TimeCodec<T> withDateFormatter(DateTimeFormatter dateFormatter) {
            return new Java8TimeCodec<>(this.java8TimeCodec.getClazz(), dateFormatter, this.java8TimeCodec.getConverter());
        }

        @Override
        public T newMessage() {
            TimeConstant.TemporalCreate<T> temporalCreate = (TimeConstant.TemporalCreate<T>) TimeConstant.CLASS_TEMPORAL_CREATE_MAP.get(this.java8TimeCodec.getClazz());
            if (temporalCreate != null) {
                return temporalCreate.create();
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public void writeTo(Output output, T message) throws IOException {
            String encode = java8TimeCodec.encode(message);
            if (encode != null) {
                output.writeString(classId(), encode, false);
            }
        }

        @Override
        public T mergeFrom(Input input) throws IOException {
            if (classId() != input.readFieldNumber(this)) {
                throw new ProtostuffException("Corrupt input.");
            }
            T t = java8TimeCodec.decode(input.readString());

            if (0 != input.readFieldNumber(this)) {
                throw new ProtostuffException("Corrupt input.");
            }
            return t;
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_JAVA8_TIME;
        }
    }
}
