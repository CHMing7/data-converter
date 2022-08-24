package com.chm.converter.protostuff.codec.factory;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.UniversalCodecAdapterCreator;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.protostuff.codec.ProtostuffCodec;
import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-02-18
 **/
public class CoreCodecFactory implements UniversalFactory<ProtostuffCodec> {

    private final UniversalGenerate<Codec> generate;

    public CoreCodecFactory(UniversalGenerate<Codec> generate) {
        this.generate = generate;
    }

    @Override
    public ProtostuffCodec create(UniversalGenerate<ProtostuffCodec> generate, TypeToken<?> typeToken) {
        return UniversalCodecAdapterCreator.create(this.generate, typeToken, (type, codec) -> {
            ProtostuffCodec encodeCodec = generate.get(codec.getEncodeType());
            return new CoreCodec(typeToken.getRawType(), codec, encodeCodec);
        });
    }

    public static class CoreCodec extends ProtostuffCodec implements WithFormat {

        private final Codec codec;

        private final ProtostuffCodec encodeCodec;

        protected CoreCodec(Class clazz, Codec codec, ProtostuffCodec encodeCodec) {
            super(clazz);
            this.encodeCodec = encodeCodec;
            this.codec = codec;
        }

        @Override
        public String getFieldName(int number) {
            return encodeCodec.getFieldName(number);
        }

        @Override
        public int getFieldNumber(String name) {
            return encodeCodec.getFieldNumber(name);
        }

        @Override
        public void writeTo(Output output, Object message) throws IOException {
            Object encode = codec.encode(message);
            if (encode != null && encode.getClass() != message.getClass()) {
                encodeCodec.writeTo(output, encode);
            }
        }

        @Override
        public Object mergeFrom(Input input) throws IOException {
            Object o = encodeCodec.mergeFrom(input);
            return codec.decode(o);
        }

        @Override
        public CoreCodec newInstance() {
            return new CoreCodec(this.clazz, codec, this.encodeCodec.newInstance());
        }

        @Override
        public CoreCodec withFieldNumber(int fieldNumber) {
            CoreCodec newInstance = newInstance();
            newInstance.encodeCodec.setFieldNumber(fieldNumber);
            newInstance.encodeCodec.setField(true);
            newInstance.setFieldNumber(fieldNumber);
            newInstance.setField(true);
            return newInstance;
        }

        @Override
        public CoreCodec withDatePattern(String datePattern) {
            if (codec instanceof WithFormat) {
                Codec withCodec = (Codec) ((WithFormat) codec).withDatePattern(datePattern);
                return new CoreCodec(this.clazz, withCodec, this.encodeCodec);
            }
            return new CoreCodec(this.clazz, this.codec, this.encodeCodec);
        }

        @Override
        public CoreCodec withDateFormatter(DateTimeFormatter dateFormatter) {
            if (codec instanceof WithFormat) {
                Codec withCodec = (Codec) ((WithFormat) codec).withDateFormatter(dateFormatter);
                return new CoreCodec(this.clazz, withCodec, this.encodeCodec);
            }
            return new CoreCodec(this.clazz, this.codec, this.encodeCodec);
        }
    }
}
