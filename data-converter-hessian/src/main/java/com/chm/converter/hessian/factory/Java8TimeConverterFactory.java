package com.chm.converter.hessian.factory;

import com.caucho.hessian.io.*;
import com.chm.converter.codec.Java8TimeCodec;
import com.chm.converter.core.constant.TimeConstant;

import java.io.IOException;
import java.time.temporal.TemporalAccessor;

/**
 * java8时间序列化
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-18
 **/
public class Java8TimeConverterFactory extends AbstractSerializerFactory {

    private static final Java8TimeConverterFactory INSTANCE = new Java8TimeConverterFactory();

    /**
     * Return the singleton instance.
     */
    public static Java8TimeConverterFactory get() {
        return INSTANCE;
    }

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        if (TimeConstant.TEMPORAL_ACCESSOR_SET.contains(cl)) {
            return new Java8TimeSerializer(cl);
        }
        return null;
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        if (TimeConstant.TEMPORAL_ACCESSOR_SET.contains(cl)) {
            return new Java8TimeDeserializer(cl);
        }
        return null;
    }

    public static class Java8TimeSerializer<T extends TemporalAccessor> extends AbstractSerializer {

        private final Java8TimeCodec<T> java8TimeCodec;

        public Java8TimeSerializer(Class<T> clazz) {
            this.java8TimeCodec = new Java8TimeCodec<>(clazz);
        }

        @Override
        public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
            if (obj == null) {
                out.writeNull();
            } else {
                if (out.addRef(obj)) {
                    return;
                }
                Class cl = obj.getClass();

                int ref = out.writeObjectBegin(cl.getName());

                if (ref < -1) {
                    out.writeString("value");
                    out.writeString(java8TimeCodec.encode((T) obj));
                    out.writeMapEnd();
                } else {
                    if (ref == -1) {
                        out.writeInt(1);
                        out.writeString("value");
                        out.writeObjectBegin(cl.getName());
                    }

                    out.writeString(java8TimeCodec.encode((T) obj));
                }
            }
        }
    }

    public static class Java8TimeDeserializer<T extends TemporalAccessor> extends AbstractDeserializer {

        private final Class<T> clazz;

        private final Java8TimeCodec<T> java8TimeCodec;

        public Java8TimeDeserializer(Class<T> clazz) {
            this.clazz = clazz;
            this.java8TimeCodec = new Java8TimeCodec<>(clazz);
        }

        @Override
        public Class getType() {
            return clazz;
        }

        @Override
        public Object readMap(AbstractHessianInput in)
                throws IOException {
            String value = null;

            while (!in.isEnd()) {
                String key = in.readString();

                if (key.equals("value")) {
                    value = in.readString();
                } else {
                    in.readObject();
                }
            }

            in.readMapEnd();

            Object object = java8TimeCodec.decode(value);

            in.addRef(object);

            return object;
        }

        @Override
        public Object readObject(AbstractHessianInput in, Object[] fields)
                throws IOException {
            String[] fieldNames = (String[]) fields;

            String value = null;

            for (int i = 0; i < fieldNames.length; i++) {
                if ("value".equals(fieldNames[i])) {
                    value = in.readString();
                } else {
                    in.readObject();
                }
            }

            Object object = java8TimeCodec.decode(value);

            in.addRef(object);

            return object;
        }
    }
}
