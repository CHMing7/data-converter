package com.chm.converter.fst.serializers;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.codecs.EnumCodec;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.core.utils.MapUtil;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-26
 **/
public class FstObjectInput extends FSTObjectInput {

    private final Converter<?> converter;

    private final Class<? extends Converter> converterClass;

    private final Map<Class, EnumCodec> enumCodecMap = MapUtil.newConcurrentHashMap();

    public FstObjectInput(Converter<?> converter, FSTObjectInput in) {
        super(in.getConf());
        this.converter = converter;
        this.converterClass = converter != null ? converter.getClass() : null;
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

    @Override
    protected void readObjectFields(FSTClazzInfo.FSTFieldInfo referencee, FSTClazzInfo serializationInfo, FSTClazzInfo.FSTFieldInfo[] fieldInfo, Object newObj, int startIndex, int version) throws Exception {
        super.readObjectFields(referencee, serializationInfo, fieldInfo, newObj, startIndex, version);
        for (FSTClazzInfo.FSTFieldInfo fstFieldInfo : fieldInfo) {
            FieldInfo info = getFieldInfo(fstFieldInfo);
            if (info != null && !info.isDeserialize()) {
                // fst中，不序列化的属性写入 null 或者 默认值
                info.set(newObj, ClassUtil.getDefaultValue(info.getFieldClass()));
            }
        }
    }

    private FieldInfo getFieldInfo(FSTClazzInfo.FSTFieldInfo referencee) {
        Field field = referencee.getField();
        if (field == null) {
            return null;
        }

        Map<String, FieldInfo> fieldNameFieldInfoMap = ClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(field.getDeclaringClass(), converterClass);
        return fieldNameFieldInfoMap.get(field.getName());
    }

}
