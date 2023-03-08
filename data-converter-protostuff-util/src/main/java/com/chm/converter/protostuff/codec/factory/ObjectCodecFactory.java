package com.chm.converter.protostuff.codec.factory;

import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.protostuff.codec.ProtostuffCodec;
import com.chm.converter.protostuff.codec.RuntimeTypeCodec;
import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;
import java.lang.reflect.TypeVariable;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-02-18
 **/
public class ObjectCodecFactory implements UniversalFactory<ProtostuffCodec> {

    public static final String FIELD_NAME_VALUE = "o";

    @Override
    public ProtostuffCodec create(UniversalGenerate<ProtostuffCodec> generate, TypeToken<?> typeToken) {
        if (typeToken.getRawType() == Object.class &&
                typeToken.getType() instanceof TypeVariable) {
            return new RuntimeTypeCodec<>(generate, new ObjectCodec(generate), typeToken.getType());
        } else if (typeToken.getRawType() == Object.class) {
            return new ObjectCodec(generate);
        }
        return null;
    }

    public static class ObjectCodec extends ProtostuffCodec<Object> {

        private final UniversalGenerate<ProtostuffCodec> generate;

        public ObjectCodec(UniversalGenerate<ProtostuffCodec> generate) {
            super(TypeToken.get(Object.class));
            this.generate = generate;
        }

        @Override
        public String getFieldName(int number) {
            return number == 1 ? FIELD_NAME_VALUE : null;
        }

        @Override
        public int getFieldNumber(String name) {
            return name.length() == 1 && name.charAt(0) == 'v' ? 1 : 0;
        }

        @Override
        public void writeTo(Output output, Object message) throws IOException {
            if (message == null) {
                return;
            }
            ProtostuffCodec codec = generate.get(message.getClass());
            ProtostuffCodec classCodec = generate.get(Class.class);
            if (codec instanceof ObjectCodec) {
                return;
            }
            classCodec.writeTo(output, message.getClass());
            codec.writeTo(output, message);
        }

        @Override
        public Object mergeFrom(Input input) throws IOException {
            ProtostuffCodec<Class> classCodec = generate.get(Class.class);
            Class<Object> cls = classCodec.mergeFrom(input);
            ProtostuffCodec codec = generate.get(cls);
            return codec.mergeFrom(input);
        }

        @Override
        public ObjectCodec newInstance() {
            return new ObjectCodec(generate);
        }
    }
}
