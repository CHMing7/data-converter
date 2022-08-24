package com.chm.converter.protostuff.codec;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.protostuff.codec.factory.ArrayCodecFactory;
import com.chm.converter.protostuff.codec.factory.CollectionCodecFactory;
import com.chm.converter.protostuff.codec.factory.CoreCodecFactory;
import com.chm.converter.protostuff.codec.factory.JavaBeanCodecFactory;
import com.chm.converter.protostuff.codec.factory.MapCodecFactory;
import com.chm.converter.protostuff.codec.factory.ObjectCodecFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-12
 **/
public class ProtostuffCodecGenerate extends UniversalGenerate<ProtostuffCodec> {

    private final Converter<?> converter;

    private ProtostuffCodecGenerate(List<UniversalFactory<ProtostuffCodec>> factories, Converter<?> converter) {
        super(factories);
        this.converter = converter;
    }

    public Converter<?> getConverter() {
        return this.converter;
    }

    public static ProtostuffCodecGenerate newDefault(Converter<?> converter) {
        List<UniversalFactory<ProtostuffCodec>> factories = new ArrayList<>();
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
        factories.add(new ArrayCodecFactory());
        factories.add(new CollectionCodecFactory());
        factories.add(new MapCodecFactory());
        factories.add(new CoreCodecFactory(DataCodecGenerate.getDataCodecGenerate(converter)));
        factories.add(new JavaBeanCodecFactory(converter));
        factories.add(new ObjectCodecFactory());
        return new ProtostuffCodecGenerate(factories, converter);
    }
}
