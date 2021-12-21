package com.chm.converter.fst.serialization;

import com.chm.converter.fst.serializers.FstSerializerDelegate;
import org.nustaq.serialization.FSTObjectSerializer;
import org.nustaq.serialization.FSTSerializerRegistry;
import org.nustaq.serialization.FSTSerializerRegistryDelegate;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-02
 **/
public class FstSerializerRegistry extends FSTSerializerRegistry {

    protected final FSTSerializerRegistry serializerRegistry = new FSTSerializerRegistry();

    @Override
    public void setDelegate(FSTSerializerRegistryDelegate delegate) {
        super.setDelegate(delegate);
        if (delegate instanceof FstSerializerRegistryDelegate) {
            ((FstSerializerRegistryDelegate) delegate).setSerializerRegistry(this);
            ((FstSerializerRegistryDelegate) delegate).setSkipDelegateSerializerRegistry(serializerRegistry);
        }
    }

    @Override
    public void putSerializer(Class cl, FSTObjectSerializer ser, boolean includeSubclasses) {
        super.putSerializer(cl, ser, includeSubclasses);
        serializerRegistry.putSerializer(cl, ser, includeSubclasses);
        FSTSerializerRegistryDelegate delegate;
        if ((delegate = getDelegate()) instanceof FstSerializerRegistryDelegate) {
            FstSerializerDelegate serDelegate = ser instanceof FstSerializerDelegate ?
                    (FstSerializerDelegate) ser : new FstSerializerDelegate(ser);
            ((FstSerializerRegistryDelegate) delegate).getGenerate().put(cl, serDelegate, includeSubclasses);
        }
    }
}
