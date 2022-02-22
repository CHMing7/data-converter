package com.chm.converter.protostuff.codec.factory;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codecs.JavaBeanCodec;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.protostuff.codec.ProtostuffCodec;
import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-02-18
 **/
public class CoreCodecFactory implements UniversalFactory<ProtostuffCodec> {

    private final DataCodecGenerate generate;

    public CoreCodecFactory(DataCodecGenerate generate) {
        this.generate = generate;
    }

    @Override
    public ProtostuffCodec create(UniversalGenerate<ProtostuffCodec> generate, TypeToken<?> typeToken) {
        if (typeToken.getRawType() == Object.class) {
            return null;
        }
        Codec codec = this.generate.get(typeToken);
        if (codec != null && !(codec instanceof JavaBeanCodec)) {
            return new CoreCodec(typeToken.getRawType(), generate, codec);
        }
        return null;
    }

    public static class CoreCodec extends ProtostuffCodec {

        private final UniversalGenerate<ProtostuffCodec> generate;

        private final ProtostuffCodec encodeCodec;

        private final Codec codec;

        protected CoreCodec(Class clazz, UniversalGenerate<ProtostuffCodec> generate, Codec codec) {
            super(clazz);
            this.encodeCodec = generate.get(codec.getEncodeType());
            this.generate = generate;
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
            return new CoreCodec(this.clazz, this.generate, codec);
        }
    }
}
