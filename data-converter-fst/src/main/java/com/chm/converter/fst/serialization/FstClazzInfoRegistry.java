package com.chm.converter.fst.serialization;

import org.nustaq.serialization.FSTClazzInfoRegistry;
import org.nustaq.serialization.FSTSerializerRegistry;
import org.nustaq.serialization.FSTSerializerRegistryDelegate;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-02
 **/
public class FstClazzInfoRegistry extends FSTClazzInfoRegistry {

    protected FstSerializerRegistry serializerRegistry = new FstSerializerRegistry();

    @Override
    public FSTSerializerRegistry getSerializerRegistry() {
        return serializerRegistry;
    }

    @Override
    public void setSerializerRegistryDelegate(FSTSerializerRegistryDelegate delegate) {
        serializerRegistry.setDelegate(delegate);
    }

    @Override
    public FSTSerializerRegistryDelegate getSerializerRegistryDelegate() {
        return serializerRegistry.getDelegate();
    }

}
