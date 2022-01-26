package com.chm.converter.fst.serializers;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.EnumCodec;
import com.chm.converter.core.utils.MapUtil;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;

import java.io.IOException;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-26
 **/
public class FstObjectInput extends FSTObjectInput {

    private final Converter<?> converter;

    private final Map<Class, EnumCodec> enumCodecMap = MapUtil.newConcurrentHashMap();

    public FstObjectInput(Converter<?> converter, FSTObjectInput in) {
        super(in.getConf());
        this.converter = converter;
    }

    @Override
    protected Object instantiateEnum(FSTClazzInfo.FSTFieldInfo referencee, int readPos) throws IOException, ClassNotFoundException {
        FSTClazzInfo clzSerInfo;
        clzSerInfo = readClass();
        Class c = clzSerInfo.getClazz();
        EnumCodec enumCodec = MapUtil.computeIfAbsent(enumCodecMap, c,
                enumClass -> new EnumCodec<>(enumClass, this.converter));
        Object res = enumCodec.read(getCodec()::readStringUTF);
        if (REGISTER_ENUMS_READ) {
            if (!referencee.isFlat()) {
                // should be unnecessary
                objects.registerObjectForRead(res, readPos);
            }
        }
        return res;
    }
}
