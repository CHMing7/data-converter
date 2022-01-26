package com.chm.converter.hessian.factory;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;
import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.DefaultDateCodec;
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
public class HessianDefaultDateConverterFactory extends AbstractSerializerFactory {

    private final Converter<?> converter;

    public HessianDefaultDateConverterFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        if (TimeConstant.DEFAULT_DATE_SET.contains(cl)) {
            return new HessianDefaultDateConverter(cl, (String) null, converter);
        }
        return null;
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        if (TimeConstant.DEFAULT_DATE_SET.contains(cl)) {
            return new HessianDefaultDateConverter(cl, (String) null, converter);
        }
        return null;
    }

    public static class HessianDefaultDateConverter<T extends Date> extends AbstractDeserializer implements Serializer, UseDeserializer {

        private final DefaultDateCodec<T> defaultDateCodec;

        public HessianDefaultDateConverter(Class<T> clazz, String datePattern, Converter<?> converter) {
            this.defaultDateCodec = new DefaultDateCodec<>(clazz, datePattern, converter);
        }

        public HessianDefaultDateConverter(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
            this.defaultDateCodec = new DefaultDateCodec<>(clazz, dateFormatter, converter);
        }

        public HessianDefaultDateConverter<T> withDateType(Class<T> dateType) {
            return new HessianDefaultDateConverter<>(dateType, this.defaultDateCodec.getDateFormatter(), this.defaultDateCodec.getConverter());
        }

        public HessianDefaultDateConverter<T> withDatePattern(String datePattern) {
            return new HessianDefaultDateConverter<>(this.defaultDateCodec.getDateType(), datePattern, this.defaultDateCodec.getConverter());
        }

        public HessianDefaultDateConverter<T> withDateFormatter(DateTimeFormatter dateFormatter) {
            return new HessianDefaultDateConverter<>(this.defaultDateCodec.getDateType(), dateFormatter, this.defaultDateCodec.getConverter());
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
