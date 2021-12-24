package com.chm.converter.fst.serialization;

import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTClazzInfoRegistry;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectSerializer;
import org.nustaq.serialization.FSTSerializerRegistryDelegate;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-02
 **/
public class FstConfiguration extends FSTConfiguration {

    protected FstClazzInfoRegistry serializationInfoRegistry = new FstClazzInfoRegistry();

    public FstConfiguration() {
        super(null);
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
    public void setStructMode(boolean ignoreSerialInterfaces) {
        serializationInfoRegistry.setStructMode(ignoreSerialInterfaces);
    }

    @Override
    public boolean isStructMode() {
        return serializationInfoRegistry.isStructMode();
    }
}
