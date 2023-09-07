package com.chm.converter.core.codecs;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.MapUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-01-10
 **/
public class JavaBeanCodec<T> implements Codec<T, T> {

    private final JavaBeanInfo<T> javaBeanInfo;

    private final UniversalGenerate<Codec> codecGenerate;

    private final Map<FieldInfo, Codec> fieldInfoCodecMap;

    public JavaBeanCodec(TypeToken<T> typeToken, UniversalGenerate<Codec> codecGenerate, Converter<?> converter) {
        this.javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(typeToken, converter != null ? converter.getClass() : null);
        this.codecGenerate = codecGenerate;
        this.fieldInfoCodecMap = MapUtil.newConcurrentHashMap();
    }

    @Override
    public T encode(T t) {
        return t;
    }

    @Override
    public TypeToken<T> getEncodeType() {
        return javaBeanInfo.getType();
    }

    @Override
    public void writeData(T t, DataWriter dw) throws IOException {
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        dw.writeBeanBegin(t);
        for (int i = 0; i < sortedFieldList.size(); i++) {
            FieldInfo fieldInfo = sortedFieldList.get(i);
            if (!fieldInfo.isSerialize()) {
                dw.writeFieldNull(i, fieldInfo);
                continue;
            }
            Object o = fieldInfo.get(t);
            if (o == null) {
                dw.writeFieldNull(i, fieldInfo);
                continue;
            }
            dw.writeFieldBegin(i, fieldInfo);
            Codec fieldCodec = getFieldCodec(fieldInfo);
            fieldCodec.write(o, dw);
            dw.writeFieldEnd(i, fieldInfo);
        }
        dw.writeBeanEnd(t);
    }

    @Override
    public T decode(T t) {
        return t;
    }

    @Override
    public TypeToken<T> getDecodeType() {
        return javaBeanInfo.getType();
    }

    @Override
    public T readData(DataReader dr) throws IOException {
        T t = javaBeanInfo.getObjectConstructor().construct();
        while (true) {
            FieldInfo fieldInfo = dr.readFieldBegin(javaBeanInfo);
            if (fieldInfo == null || !fieldInfo.isDeserialize()) {
                dr.skipAny();
                continue;
            }
            if (fieldInfo.isStop()) {
                break;
            }
            Codec<?, ?> fieldCodec = getFieldCodec(fieldInfo);
            fieldInfo.set(t, fieldCodec.read(dr));
            dr.readFieldEnd(javaBeanInfo);
        }
        return t;
    }

    private Codec<?, ?> getFieldCodec(FieldInfo fieldInfo) {
        return MapUtil.computeIfAbsent(fieldInfoCodecMap, fieldInfo, info -> {
            Codec<?, ?> codec = codecGenerate.get(fieldInfo.getTypeToken());
            if (codec instanceof WithFormat) {
                codec = (Codec<?, ?>) ((WithFormat) codec).withDatePattern(fieldInfo.getFormat());
            }
            return codec;
        });
    }
}