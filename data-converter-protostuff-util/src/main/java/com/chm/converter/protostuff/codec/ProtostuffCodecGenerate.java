package com.chm.converter.protostuff.codec;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.protostuff.codec.factory.ArrayCodecFactory;
import com.chm.converter.protostuff.codec.factory.CollectionCodecFactory;
import com.chm.converter.protostuff.codec.factory.CoreCodecFactory;
import com.chm.converter.protostuff.codec.factory.DefaultDateCodecFactory;
import com.chm.converter.protostuff.codec.factory.EnumCodecFactory;
import com.chm.converter.protostuff.codec.factory.Java8TimeCodecFactory;
import com.chm.converter.protostuff.codec.factory.JavaBeanCodecFactory;
import com.chm.converter.protostuff.codec.factory.MapCodecFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-12
 **/
public class ProtostuffCodecGenerate extends UniversalGenerate<ProtostuffCodec> {

    private static ProtostuffCodecGenerate DEFAULT;

    public ProtostuffCodecGenerate() {
    }

    public ProtostuffCodecGenerate(List<UniversalFactory<ProtostuffCodec>> protostuffCodecFactories) {
        super(protostuffCodecFactories);
    }

    public static ProtostuffCodecGenerate getDefault() {
        if (DEFAULT == null) {
            DEFAULT = newDefault();
        }
        return DEFAULT;
    }

    public static ProtostuffCodecGenerate newDefault() {
        return newDefault(null);
    }

    public static ProtostuffCodecGenerate newDefault(Converter<?> converter) {
        List<UniversalFactory<ProtostuffCodec>> factories = new ArrayList<>();
        factories.add(new JavaBeanCodecFactory(converter));
        factories.add(new CoreCodecFactory(DataCodecGenerate.newDefault(converter)));
        factories.add(new EnumCodecFactory(converter));
        factories.add(new MapCodecFactory());
        factories.add(new CollectionCodecFactory());
        factories.add(new ArrayCodecFactory());
        factories.add(new DefaultDateCodecFactory(converter));
        factories.add(new Java8TimeCodecFactory(converter));
        // factories.add(new ObjectCodecFactory());
        factories.add(ProtostuffCodecs.CHAR_FACTORY);
        factories.add(ProtostuffCodecs.SHORT_FACTORY);
        factories.add(ProtostuffCodecs.BYTE_FACTORY);
        factories.add(ProtostuffCodecs.INT_FACTORY);
        factories.add(ProtostuffCodecs.LONG_FACTORY);
        factories.add(ProtostuffCodecs.FLOAT_FACTORY);
        factories.add(ProtostuffCodecs.DOUBLE_FACTORY);
        factories.add(ProtostuffCodecs.BOOL_FACTORY);
        factories.add(ProtostuffCodecs.STRING_FACTORY);
        factories.add(ProtostuffCodecs.BYTE_STRING_FACTORY);
        factories.add(ProtostuffCodecs.BYTES_FACTORY);
        factories.add(ProtostuffCodecs.BIG_DECIMAL_FACTORY);
        factories.add(ProtostuffCodecs.BIG_INTEGER_FACTORY);
        return new ProtostuffCodecGenerate(factories);
    }
}
