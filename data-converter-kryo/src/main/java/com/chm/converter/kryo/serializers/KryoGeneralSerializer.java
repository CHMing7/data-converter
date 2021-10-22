package com.chm.converter.kryo.serializers;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.utils.MapUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-10-22
 **/
public class KryoGeneralSerializer<T> extends Serializer<T> {

    private final JavaBeanInfo<T> javaBeanInfo;

    private final Map<FieldInfo, Serializer> fieldInfoSerializerMap;

    public KryoGeneralSerializer(Class<T> clazz, Class<? extends Converter> converterClass) {
        this.javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz, converterClass);
        this.fieldInfoSerializerMap = new ConcurrentHashMap<>();
    }

    @Override
    public void write(Kryo kryo, Output output, T object) {
        if (object == null) {
            output.write(Kryo.NULL);
            return;
        }
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        for (FieldInfo fieldInfo : sortedFieldList) {
            Serializer serializer = getFieldSerializer(kryo, fieldInfo);
            Object fieldValue = fieldInfo.get(object);
            kryo.writeObjectOrNull(output, fieldValue, serializer);
        }
    }

    @Override
    public T read(Kryo kryo, Input input, Class<T> type) {
        T construct = this.javaBeanInfo.getObjectConstructor().construct();
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        for (FieldInfo fieldInfo : sortedFieldList) {
            Serializer serializer = getFieldSerializer(kryo, fieldInfo);
            Object fieldValue = kryo.readObjectOrNull(input, fieldInfo.getFieldClass(), serializer);
            fieldInfo.set(construct, fieldValue);
        }
        return construct;
    }

    private Serializer<?> getFieldSerializer(Kryo kryo, FieldInfo fieldInfo) {
        return MapUtil.computeIfAbsent(fieldInfoSerializerMap, fieldInfo, info -> {
            Serializer<?> serializer = kryo.getSerializer(info.getFieldClass());
            if (serializer instanceof KryoJava8TimeSerializer) {
                return ((KryoJava8TimeSerializer<?>) serializer).withDatePattern(fieldInfo.getFormat());
            }
            if (serializer instanceof KryoDefaultDateSerializer) {
                return ((KryoDefaultDateSerializer<?>) serializer).withDatePattern(fieldInfo.getFormat());
            }
            return serializer;
        });
    }
}
