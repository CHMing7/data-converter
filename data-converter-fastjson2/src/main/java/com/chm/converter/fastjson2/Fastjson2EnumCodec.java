package com.chm.converter.fastjson2;

import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.util.Fnv;
import com.alibaba.fastjson2.util.TypeUtils;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.EnumCodec;
import com.chm.converter.core.utils.StringUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-26
 **/
public class Fastjson2EnumCodec<E extends Enum<E>> implements ObjectWriter<E>, ObjectReader<E> {

    private final EnumCodec<E> enumCodec;

    private final byte[] typeNameJsonb;

    private final long typeNameHash;

    private final E[] enums;

    private final long[] enumNameHashCodes;

    public Fastjson2EnumCodec(Class<E> classOfT, Converter<?> converter) {
        this.enumCodec = new EnumCodec<>(classOfT, converter);
        String typeName = TypeUtils.getTypeName(classOfT);
        this.typeNameJsonb = JSONB.toBytes(typeName);
        this.typeNameHash = Fnv.hashCode64(typeName);
        E[] ordinalEnums = classOfT.getEnumConstants();
        Map<Long, E> enumMap = new HashMap();
        for (E ordinalEnum : ordinalEnums) {
            String name = ordinalEnum.name();
            long hash = Fnv.hashCode64(name);
            enumMap.put(hash, ordinalEnum);
            long hashLCase = Fnv.hashCode64LCase(name);
            enumMap.putIfAbsent(hashLCase, ordinalEnum);
        }

        long[] enumNameHashCodes = new long[enumMap.size()];
        {
            int i = 0;
            for (Long h : enumMap.keySet()) {
                enumNameHashCodes[i++] = h;
            }
            Arrays.sort(enumNameHashCodes);
        }

        E[] enums = (E[]) Array.newInstance(classOfT, enumNameHashCodes.length);
        for (int i = 0; i < enumNameHashCodes.length; ++i) {
            long hash = enumNameHashCodes[i];
            E e = enumMap.get(hash);
            enums[i] = e;
        }
        this.enums = enums;
        this.enumNameHashCodes = enumNameHashCodes;
    }

    @Override
    public long getFeatures() {
        return ObjectWriter.super.getFeatures();
    }

    @Override
    public void writeJSONB(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (jsonWriter.isWriteTypeInfo(object, fieldType, features)) {
            jsonWriter.writeTypeName(typeNameJsonb, typeNameHash);
        }

        jsonWriter.writeString(this.enumCodec.encode((E) object));
    }

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }
        jsonWriter.writeString(this.enumCodec.encode((E) object));
    }


    @Override
    public E readJSONBObject(JSONReader jsonReader, long features) {
        String str = jsonReader.readString();
        if (StringUtil.isBlank(str)) {
            return null;
        }

        return this.enumCodec.decode(str);
    }


    @Override
    public E readObject(JSONReader jsonReader, long features) {
        String str = jsonReader.readString();
        if (StringUtil.isBlank(str)) {
            return null;
        }

        return this.enumCodec.decode(str);
    }
}
