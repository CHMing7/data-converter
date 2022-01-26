package com.chm.converter.fst.serializers;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.EnumCodec;
import com.chm.converter.core.utils.MapUtil;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.IOException;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-26
 **/
public class FstObjectOutput extends FSTObjectOutput {

    private final Converter<?> converter;

    private final Map<Class, EnumCodec> enumCodecMap = MapUtil.newConcurrentHashMap();

    public FstObjectOutput(Converter<?> converter, FSTObjectOutput out) {
        super(out.getConf());
        this.converter = converter;
    }

    @Override
    protected FSTClazzInfo writeEnum(FSTClazzInfo.FSTFieldInfo referencee, Object toWrite) throws IOException {
        if (!getCodec().writeTag(ENUM, toWrite, 0, toWrite, this)) {
            boolean isEnumClass = toWrite.getClass().isEnum();
            EnumCodec enumCodec = MapUtil.computeIfAbsent(enumCodecMap, toWrite.getClass(),
                    enumClass -> new EnumCodec<>(enumClass, this.converter));
            if (!isEnumClass) {
                // anonymous enum subclass
                Class c = toWrite.getClass().getSuperclass();
                if (c == null) {
                    throw new RuntimeException("Can't handle this enum: " + toWrite.getClass());
                }
                getCodec().writeClass(c);
            } else {
                FSTClazzInfo fstClazzInfo = getFstClazzInfo(referencee, toWrite.getClass());
                getCodec().writeClass(fstClazzInfo);
                enumCodec.write(toWrite, o -> this.getCodec().writeStringUTF((String) o));
                return fstClazzInfo;
            }
            enumCodec.write(toWrite, o -> this.getCodec().writeStringUTF((String) o));
        }
        return null;
    }
}
