package com.chm.converter.fst.serializers;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.codecs.EnumCodec;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.core.utils.MapUtil;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-26
 **/
public class FstObjectOutput extends FSTObjectOutput {

    private final Converter<?> converter;

    private final Class<? extends Converter> converterClass;

    private final Map<Class, EnumCodec> enumCodecMap = MapUtil.newConcurrentHashMap();

    public FstObjectOutput(Converter<?> converter, FSTObjectOutput out) {
        super(out.getConf());
        this.converter = converter;
        this.converterClass = converter != null ? converter.getClass() : null;
    }

    @Override
    protected void writeObjectFields(Object toWrite, FSTClazzInfo serializationInfo, FSTClazzInfo.FSTFieldInfo[] fieldInfo, int startIndex, int version) throws IOException {
        for (FSTClazzInfo.FSTFieldInfo fstFieldInfo : fieldInfo) {
            FieldInfo info = getFieldInfo(fstFieldInfo);
            if (info != null && !info.isSerialize()) {
                info.set(toWrite, ClassUtil.getDefaultValue(info.getFieldClass()));
            }
        }
        super.writeObjectFields(toWrite, serializationInfo, fieldInfo, startIndex, version);
    }

    private FieldInfo getFieldInfo(FSTClazzInfo.FSTFieldInfo referencee) {
        Field field = referencee.getField();
        if (field == null) {
            return null;
        }

        Map<String, FieldInfo> fieldNameFieldInfoMap = ClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(field.getDeclaringClass(), converterClass);
        return fieldNameFieldInfoMap.get(field.getName());
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
