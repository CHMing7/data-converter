package com.chm.converter.hessian.factory.codec;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.hessian.UseDeserializer;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-08-19
 **/
public class HessianCoreCodec extends AbstractDeserializer implements Serializer, UseDeserializer, WithFormat {

    private final Codec codec;

    public HessianCoreCodec(Codec codec) {
        this.codec = codec;
    }

    @Override
    public Class getType() {
        return this.codec.getDecodeType().getRawType();
    }

    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
        } else {
            Object encode = this.codec.encode(obj);
            if (encode != obj && out.addRef(obj)) {
                return;
            }
            out.writeObject(encode);
        }
    }

    @Override
    public Object readObject(AbstractHessianInput in) throws IOException {
        Object value = in.readObject();

        Object object = this.codec.decode(value);

        in.addRef(object);

        return object;
    }

    @Override
    public HessianCoreCodec withDatePattern(String datePattern) {
        if (codec instanceof WithFormat) {
            Codec withCodec = (Codec) ((WithFormat) codec).withDatePattern(datePattern);
            return new HessianCoreCodec(withCodec);
        }
        return new HessianCoreCodec(this.codec);
    }

    @Override
    public HessianCoreCodec withDateFormatter(DateTimeFormatter dateFormatter) {
        if (codec instanceof WithFormat) {
            Codec withCodec = (Codec) ((WithFormat) codec).withDateFormatter(dateFormatter);
            return new HessianCoreCodec(withCodec);
        }
        return new HessianCoreCodec(this.codec);
    }
}
