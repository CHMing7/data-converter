package com.chm.converter.test.hessian;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;
import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.EnumCodec;

import java.io.IOException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-26
 **/
public class HessianEnumConverterFactory extends AbstractSerializerFactory {

    private final Converter<?> converter;

    public HessianEnumConverterFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        if (!Enum.class.isAssignableFrom(cl) || cl == Enum.class) {
            return null;
        }
        if (!cl.isEnum()) {
            cl = cl.getSuperclass();
        }
        return new HessianEnumConverter(cl, converter);
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        if (!Enum.class.isAssignableFrom(cl) || cl == Enum.class) {
            return null;
        }
        if (!cl.isEnum()) {
            cl = cl.getSuperclass();
        }
        return new HessianEnumConverter(cl, converter);
    }

    public static class HessianEnumConverter<E extends Enum<E>> extends AbstractDeserializer implements Serializer {

        private final EnumCodec<E> enumCodec;

        public HessianEnumConverter(Class<E> classOfT, Converter<?> converter) {
            this.enumCodec = new EnumCodec<>(classOfT, converter);
        }

        @Override
        public Class<?> getType() {
            return this.enumCodec.getEnumType();
        }

        @Override
        public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
            if (obj == null) {
                out.writeNull();
                return;
            }
            if (out.addRef(obj)) {
                return;
            }

            Class<?> cl = obj.getClass();

            if (!cl.isEnum() && cl.getSuperclass().isEnum()) {
                cl = cl.getSuperclass();
            }

            String name = this.enumCodec.encode((E) obj);

            int ref = out.writeObjectBegin(cl.getName());

            if (ref < -1) {
                out.writeString("name");
                out.writeString(name);
                out.writeMapEnd();
            } else {
                if (ref == -1) {
                    out.writeClassFieldLength(1);
                    out.writeString("name");
                    out.writeObjectBegin(cl.getName());
                }

                out.writeString(name);
            }
        }

        @Override
        public Object readMap(AbstractHessianInput in) throws IOException {
            String name = null;
            while (!in.isEnd()) {
                String key = in.readString();

                if ("name".equals(key)) {
                    name = in.readString();
                } else {
                    in.readObject();
                }
            }

            in.readMapEnd();

            Object obj = create(name);

            in.addRef(obj);

            return obj;
        }

        @Override
        public Object readObject(AbstractHessianInput in) throws IOException {
            return readMap(in);
        }

        @Override
        public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
            String[] fieldNames = (String[]) fields;
            String name = null;

            for (String fieldName : fieldNames) {
                if ("name".equals(fieldName)) {
                    name = in.readString();
                } else {
                    in.readObject();
                }
            }

            Object obj = create(name);

            in.addRef(obj);

            return obj;
        }

        private Object create(String name) throws IOException {
            if (name == null) {
                throw new IOException(this.enumCodec.getEnumType().getName() + " expects name.");
            }

            return this.enumCodec.decode(name);
        }
    }
}
