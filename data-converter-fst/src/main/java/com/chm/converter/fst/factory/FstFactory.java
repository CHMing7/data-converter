package com.chm.converter.fst.factory;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codec.UniversalCodecAdapterCreator;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.fst.instantiators.CustomFstDefaultClassInstantiator;
import com.chm.converter.fst.serialization.FstConfiguration;
import com.chm.converter.fst.serializers.FstCoreCodecSerializer;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.FSTObjectSerializer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Fst object input/output factory
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-27
 **/
public class FstFactory {

    private final FSTConfiguration conf;

    private final UniversalGenerate<Codec> generate;

    public FstFactory(Converter<?> converter) {
        this(converter, null);
    }

    public FstFactory(Converter<?> converter, UniversalGenerate<Codec> generate) {
        this.conf = new FstConfiguration(converter);
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);

        this.conf.setInstantiator(new CustomFstDefaultClassInstantiator());
        this.conf.setForceSerializable(true);
        this.conf.setForceSerializable(true);
        this.conf.setSerializerRegistryDelegate(cl ->
                UniversalCodecAdapterCreator.createPriorityUse(this.generate, cl, (t, codec) -> {
                    FSTObjectSerializer encodeSerializer = conf.getCLInfoRegistry().getSerializerRegistry().getSerializer(codec.getEncodeType().getRawType());
                    return new FstCoreCodecSerializer(converter, codec, encodeSerializer);
                }));
    }

    public FSTObjectOutput getObjectOutput(OutputStream outputStream) {
        return conf.getObjectOutput(outputStream);
    }

    public FSTObjectInput getObjectInput(InputStream inputStream) {
        return conf.getObjectInput(inputStream);
    }
}
