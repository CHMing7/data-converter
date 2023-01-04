package com.chm.converter.fst.serialization;

import com.chm.converter.core.Converter;
import com.chm.converter.fst.DefaultFstConverter;
import com.chm.converter.fst.serializers.FstObjectInput;
import com.chm.converter.fst.serializers.FstObjectOutput;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTClazzInfoRegistry;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.FSTObjectSerializer;
import org.nustaq.serialization.FSTSerializerRegistryDelegate;
import org.nustaq.serialization.util.FSTUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-02
 **/
public class FstConfiguration extends FSTConfiguration {

    protected final FstClazzInfoRegistry serializationInfoRegistry;

    private final Converter<?> converter;

    public FstConfiguration(Converter<?> converter) {
        super(null);
        this.serializationInfoRegistry = new FstClazzInfoRegistry(converter, DefaultFstConverter::checkExistFstAnnotation);
        this.converter = converter;
        initDefaultFstConfigurationInternal(this);
    }

    @Override
    public void registerSerializer(Class clazz, FSTObjectSerializer ser, boolean alsoForAllSubclasses) {
        serializationInfoRegistry.getSerializerRegistry().putSerializer(clazz, ser, alsoForAllSubclasses);
    }

    @Override
    public void setSerializerRegistryDelegate(FSTSerializerRegistryDelegate del) {
        serializationInfoRegistry.setSerializerRegistryDelegate(del);
    }

    @Override
    public FSTClazzInfoRegistry getCLInfoRegistry() {
        return serializationInfoRegistry;
    }

    @Override
    public FSTClazzInfo getClassInfo(Class type) {
        return serializationInfoRegistry.getCLInfo(type, this);
    }

    @Override
    public FSTObjectInput getObjectInput(InputStream in) {
        FSTObjectInput fstObjectInput = new FstObjectInput(this.converter, getIn());
        try {
            fstObjectInput.resetForReuse(in);
            return fstObjectInput;
        } catch (IOException e) {
            FSTUtil.rethrow(e);
        }
        return null;
    }

    @Override
    public FSTObjectOutput getObjectOutput(OutputStream out) {
        FSTObjectOutput fstObjectOutput = new FstObjectOutput(this.converter, getOut());
        fstObjectOutput.resetForReUse(out);
        return fstObjectOutput;
    }

    @Override
    public boolean isStructMode() {
        return serializationInfoRegistry.isStructMode();
    }

    @Override
    public void setStructMode(boolean ignoreSerialInterfaces) {
        serializationInfoRegistry.setStructMode(ignoreSerialInterfaces);
    }
}
