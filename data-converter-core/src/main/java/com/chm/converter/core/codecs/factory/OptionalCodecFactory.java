package com.chm.converter.core.codecs.factory;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codecs.IdentityCodec;
import com.chm.converter.core.exception.CodecException;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-19
 **/
public class OptionalCodecFactory implements UniversalFactory<Codec> {

    @Override
    public Codec create(UniversalGenerate<Codec> generate, TypeToken<?> typeToken) {
        if (typeToken.getRawType() != Optional.class) {
            return null;
        }
        Codec delegate;
        Type type = typeToken.getType();
        if (type instanceof ParameterizedType) {
            Type innerType = ((ParameterizedType) type).getActualTypeArguments()[0];
            delegate = generate.get(TypeToken.get(innerType));
        } else if (type instanceof Class) {
            delegate = generate.get(Object.class);
        } else {
            throw new CodecException("Unexpected type type:" + type.getClass());
        }
        return getOptionalCodec(delegate);
    }

    private Codec getOptionalCodec(Codec delegate) {
        return IdentityCodec.<Optional>create(delegate.getEncodeType(), (o, dw) -> {
            // optional should not be null
            if (o == null) {
                dw.writeNull();
                return;
            }
            if (!o.isPresent()) {
                dw.writeNull();
                return;
            }
            delegate.writeData(o.get(), dw);
        }, dr -> Optional.ofNullable(delegate.readData(dr)));
    }
}
