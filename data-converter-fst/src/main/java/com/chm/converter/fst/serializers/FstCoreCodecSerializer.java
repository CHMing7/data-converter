package com.chm.converter.fst.serializers;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.utils.MapUtil;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.FSTObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import static org.nustaq.serialization.FSTObjectOutput.NULL;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-08-10
 **/
public class FstCoreCodecSerializer extends FstSerializer {

    private static final Map<Converter, Map<FieldInfo, Codec>> CONVERTER_INFO_CODEC_MAP = MapUtil.newConcurrentHashMap();

    private final Converter<?> converter;

    private final Class<? extends Converter> converterClass;

    private final Codec codec;

    private final FSTObjectSerializer encodeSerializer;

    public FstCoreCodecSerializer(Converter<?> converter, Codec codec, FSTObjectSerializer encodeSerializer) {
        this.converter = converter;
        this.converterClass = converter != null ? converter.getClass() : null;
        this.codec = codec;
        this.encodeSerializer = encodeSerializer;
    }

    @Override
    public void writeObject(FSTObjectOutput out, Object toWrite, FSTClazzInfo clzInfo, FSTClazzInfo.FSTFieldInfo referencedBy, int streamPosition) throws IOException {
        if (toWrite == null) {
            out.getCodec().writeTag(NULL, null, 0, toWrite, out);
            return;
        }
        Codec codec = getFieldSerializer(referencedBy);
        Object encode = codec.encode(toWrite);
        if (encode != null && encode.getClass() != toWrite.getClass()) {
            encodeSerializer.writeObject(out, encode, clzInfo, referencedBy, streamPosition);
        }
    }

    @Override
    public Object instantiate(Class objectClass, FSTObjectInput in, FSTClazzInfo serializationInfo, FSTClazzInfo.FSTFieldInfo referencee, int streamPosition) throws Exception {
        Object o = encodeSerializer.instantiate(objectClass, in, serializationInfo, referencee, streamPosition);
        if (o == null) {
            return null;
        }
        Codec codec = getFieldSerializer(referencee);
        return codec.decode(o);
    }

    private Codec getFieldSerializer(FSTClazzInfo.FSTFieldInfo referencedB) {
        Field field = referencedB.getField();
        if (field == null) {
            return this.codec;
        }
        Class fromClass = field.getDeclaringClass();
        Map<String, FieldInfo> fieldNameFieldInfoMap = ClassInfoStorage.INSTANCE.getFieldNameFieldInfoMap(fromClass, converterClass);
        FieldInfo fieldInfo = fieldNameFieldInfoMap.get(field.getName());
        Map<FieldInfo, Codec> fieldInfoCodecMap = MapUtil.computeIfAbsent(CONVERTER_INFO_CODEC_MAP, converter, c -> MapUtil.newConcurrentHashMap());
        return MapUtil.computeIfAbsent(fieldInfoCodecMap, fieldInfo, info -> {
            if (this.codec instanceof WithFormat) {
                return (Codec) ((WithFormat) this.codec).withDatePattern(fieldInfo.getFormat());
            }
            return this.codec;
        });
    }
}
