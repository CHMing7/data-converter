package com.chm.converter.hessian.factory;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;
import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.hessian.UseDeserializer;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * java8时间序列化
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-18
 **/
public class HessianJava8TimeConverterFactory extends AbstractSerializerFactory {

    private final Converter<?> converter;

    public HessianJava8TimeConverterFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        if (TimeConstant.TEMPORAL_ACCESSOR_SET.contains(cl)) {
            return new HessianJava8TimeConverter(cl, (String) null, converter);
        }
        return null;
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        if (TimeConstant.TEMPORAL_ACCESSOR_SET.contains(cl)) {
            return new HessianJava8TimeConverter(cl, (String) null, converter);
        }
        return null;
    }

    public static class HessianJava8TimeConverter<T extends TemporalAccessor> extends AbstractDeserializer implements Serializer, UseDeserializer {

        private final Java8TimeCodec<T> java8TimeCodec;

        public HessianJava8TimeConverter(Class<T> clazz, String datePattern, Converter<?> converter) {
            this.java8TimeCodec = new Java8TimeCodec<>(clazz, datePattern, converter);
        }

        public HessianJava8TimeConverter(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
            this.java8TimeCodec = new Java8TimeCodec<>(clazz, dateFormatter, converter);
        }

        public HessianJava8TimeConverter<T> withClass(Class<T> clazz) {
            return new HessianJava8TimeConverter<>(clazz, this.java8TimeCodec.getDateFormatter(), this.java8TimeCodec.getConverter());
        }

        public HessianJava8TimeConverter<T> withDatePattern(String datePattern) {
            return new HessianJava8TimeConverter<>(this.java8TimeCodec.getClazz(), datePattern, this.java8TimeCodec.getConverter());
        }

        public HessianJava8TimeConverter<T> withDateFormatter(DateTimeFormatter dateFormatter) {
            return new HessianJava8TimeConverter<>(this.java8TimeCodec.getClazz(), dateFormatter, this.java8TimeCodec.getConverter());
        }

        @Override
        public Class getType() {
            return this.java8TimeCodec.getClazz();
        }

        @Override
        public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
            if (obj == null) {
                out.writeNull();
            } else {
                if (out.addRef(obj)) {
                    return;
                }
                out.writeString(this.java8TimeCodec.encode((T) obj));
            }
        }

        @Override
        public Object readObject(AbstractHessianInput in) throws IOException {
            String value = in.readString();

            Object object = this.java8TimeCodec.decode(value);

            in.addRef(object);

            return object;
        }
    }
}
