package com.chm.converter.fst.serializers;

import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.FSTObjectSerializer;

import java.io.IOException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-02
 **/
public class FstSerializerDelegate extends FstSerializer {

    private final FSTObjectSerializer fstObjectSerializer;

    public FstSerializerDelegate(FSTObjectSerializer fstObjectSerializer) {
        this.fstObjectSerializer = fstObjectSerializer;
    }

    @Override
    public void writeObject(FSTObjectOutput out, Object toWrite, FSTClazzInfo clzInfo, FSTClazzInfo.FSTFieldInfo referencedBy, int streamPosition) throws IOException {
        fstObjectSerializer.writeObject(out, toWrite, clzInfo, referencedBy, streamPosition);
    }

    @Override
    public void readObject(FSTObjectInput in, Object toRead, FSTClazzInfo clzInfo, FSTClazzInfo.FSTFieldInfo referencedBy) throws Exception {
        fstObjectSerializer.readObject(in, toRead, clzInfo, referencedBy);
    }

    @Override
    public boolean willHandleClass(Class cl) {
        return fstObjectSerializer.willHandleClass(cl);
    }

    @Override
    public boolean alwaysCopy() {
        return fstObjectSerializer.alwaysCopy();
    }

    @Override
    public Object instantiate(Class objectClass, FSTObjectInput fstObjectInput, FSTClazzInfo serializationInfo, FSTClazzInfo.FSTFieldInfo referencee, int streamPosition) throws Exception {
        return fstObjectSerializer.instantiate(objectClass, fstObjectInput, serializationInfo, referencee, streamPosition);
    }
}
