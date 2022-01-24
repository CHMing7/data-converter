package com.chm.converter.fst.serializers;

import com.chm.converter.core.universal.UniversalInterface;
import org.nustaq.serialization.FSTBasicObjectSerializer;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-30
 **/
public abstract class FstSerializer extends FSTBasicObjectSerializer implements UniversalInterface {

    /**
     * 实例化
     *
     * @param objectClass
     * @param in
     * @param serializationInfo
     * @param referencee
     * @param streamPosition
     * @return
     * @throws Exception
     */
    @Override
    public abstract Object instantiate(Class objectClass, FSTObjectInput in, FSTClazzInfo serializationInfo, FSTClazzInfo.FSTFieldInfo referencee, int streamPosition) throws Exception;
}
