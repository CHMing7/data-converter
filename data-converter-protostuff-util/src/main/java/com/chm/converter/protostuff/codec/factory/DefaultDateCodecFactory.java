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
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 默认实践类编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-15
 **/
public class DefaultDateCodecFactory implements UniversalFactory<ProtostuffCodec> {

    private final Converter<?> converter;

    public DefaultDateCodecFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public ProtostuffCodec create(UniversalGenerate<ProtostuffCodec> generate, TypeToken<?> typeToken) {
        if (TimeConstant.DEFAULT_DATE_SET.contains(typeToken.getRawType())) {
            return new DefaultDateCodec(typeToken.getRawType(), (String) null, converter);
        }
        return null;
    }

    public static final class DefaultDateCodec<T extends Date> extends BaseProtostuffCodec<T> {

        private final com.chm.converter.core.codecs.DefaultDateCodec<T> defaultDateCodec;

        public DefaultDateCodec(Class<T> clazz, String datePattern, Converter<?> converter) {
            super(clazz, "defaultDate");
            this.defaultDateCodec = new com.chm.converter.core.codecs.DefaultDateCodec<>(clazz, datePattern, converter);
        }

        public DefaultDateCodec(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
            super(clazz, "defaultDate");
            this.defaultDateCodec = new com.chm.converter.core.codecs.DefaultDateCodec<>(clazz, dateFormatter, converter);
        }

        public DefaultDateCodec<T> withDateType(Class<T> dateType) {
            return new DefaultDateCodec<>(dateType, this.defaultDateCodec.getDateFormatter(), this.defaultDateCodec.getConverter());
        }

        public DefaultDateCodec<T> withDatePattern(String datePattern) {
            return new DefaultDateCodec<>(this.defaultDateCodec.getDateType(), datePattern, this.defaultDateCodec.getConverter());
        }

        public DefaultDateCodec<T> withDateFormatter(DateTimeFormatter dateFormatter) {
            return new DefaultDateCodec<>(this.defaultDateCodec.getDateType(), dateFormatter, this.defaultDateCodec.getConverter());
        }

        @Override
        public T newMessage() {
            Class<T> dateType = this.defaultDateCodec.getDateType();
            Date date = new Date();
            if (dateType == Date.class) {
                return (T) date;
            } else if (dateType == Timestamp.class) {
                return (T) new Timestamp(date.getTime());
            } else if (dateType == java.sql.Date.class) {
                return (T) new java.sql.Date(date.getTime());
            } else {
                // This must never happen: dateType is guarded in the primary constructor
                throw new AssertionError();
            }
        }

        @Override
        public void writeTo(Output output, T message) throws IOException {
            defaultDateCodec.write(message, encode -> {
                if (encode != null) {
                    output.writeString(classId(), encode, false);
                }
            });
        }

        @Override
        public T mergeFrom(Input input) throws IOException {
            if (classId() != input.readFieldNumber(this)) {
                throw new ProtostuffException("Corrupt input.");
            }
            T t = defaultDateCodec.decode(input.readString());

            if (0 != input.readFieldNumber(this)) {
                throw new ProtostuffException("Corrupt input.");
            }
            return t;
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_DEFAULT_DATE;
        }
    }
}
