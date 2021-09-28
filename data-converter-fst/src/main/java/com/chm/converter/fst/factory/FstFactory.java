package com.chm.converter.fst.factory;

import com.chm.converter.fst.instantiators.CustomFstDefaultClassInstantiator;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

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

    private static final FstFactory DEFAULT = new FstFactory();

    private final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    public static FstFactory getDefaultFactory() {
        return DEFAULT;
    }

    public FstFactory() {
        conf.setInstantiator(new CustomFstDefaultClassInstantiator());
        conf.setForceSerializable(true);
    }

    public FSTObjectOutput getObjectOutput(OutputStream outputStream) {
        return conf.getObjectOutput(outputStream);
    }

    public FSTObjectInput getObjectInput(InputStream inputStream) {
        return conf.getObjectInput(inputStream);
    }

    public boolean checkExistSerializer(Class cls) {
        return conf.getCLInfoRegistry().getSerializerRegistry().getSerializer(cls) != null;
    }
}
