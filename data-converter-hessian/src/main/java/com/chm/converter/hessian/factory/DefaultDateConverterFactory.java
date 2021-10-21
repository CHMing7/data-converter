package com.chm.converter.hessian.factory;

import com.caucho.hessian.io.*;
import com.chm.converter.codec.DefaultDateCodec;
import com.chm.converter.core.Converter;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.hessian.UseDeserializer;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-10-18
 **/
public class DefaultDateConverterFactory extends AbstractSerializerFactory {

    private final Converter<?> converter;

    public DefaultDateConverterFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        if (TimeConstant.DEFAULT_DATE_SET.contains(cl)) {
            return new DefaultDateConverter(cl, (String) null, converter);
        }
        return null;
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        if (TimeConstant.DEFAULT_DATE_SET.contains(cl)) {
            return new DefaultDateConverter(cl, (String) null, converter);
        }
        return null;
    }

    public static class DefaultDateConverter<T extends Date> extends AbstractDeserializer implements Serializer, UseDeserializer {

        private final DefaultDateCodec<T> defaultDateCodec;

        public DefaultDateConverter(Class<T> clazz, String datePattern, Converter<?> converter) {
            this.defaultDateCodec = new DefaultDateCodec<>(clazz, datePattern, converter);
        }

        public DefaultDateConverter(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
            this.defaultDateCodec = new DefaultDateCodec<>(clazz, dateFormatter, converter);
        }

        public DefaultDateConverter<T> withDateType(Class<T> dateType) {
            return new DefaultDateConverter<>(dateType, this.defaultDateCodec.getDateFormatter(), this.defaultDateCodec.getConverter());
        }

        public DefaultDateConverter<T> withDatePattern(String datePattern) {
            return new DefaultDateConverter<>(this.defaultDateCodec.getDateType(), datePattern, this.defaultDateCodec.getConverter());
        }

        public DefaultDateConverter<T> withDateFormatter(DateTimeFormatter dateFormatter) {
            return new DefaultDateConverter<>(this.defaultDateCodec.getDateType(), dateFormatter, this.defaultDateCodec.getConverter());
        }

        @Override
        public Class getType() {
            return this.defaultDateCodec.getDateType();
        }

        @Override
        public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
            if (obj == null) {
                out.writeNull();
            } else {
                if (out.addRef(obj)) {
                    return;
                }
                out.writeString(defaultDateCodec.encode((T) obj));
            }
        }

        @Override
        public Object readObject(AbstractHessianInput in) throws IOException {
            String value = in.readString();

            Object object = defaultDateCodec.decode(value);

            in.addRef(object);

            return object;
        }


    }
}
