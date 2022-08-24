package com.chm.converter.kryo.serializers;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.utils.ClassUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-08-24
 **/
public class JavaBeanSerializer<T> extends FieldSerializer<T> {

    private final Class<? extends Converter> converterClass;

    public JavaBeanSerializer(Converter<?> converter, FieldSerializer<T> fieldSerializer) {
        super(fieldSerializer.getKryo(), fieldSerializer.getType(), fieldSerializer.getGenerics());
        this.converterClass = converter != null ? converter.getClass() : null;
    }

    @Override
    public void write(Kryo kryo, Output output, T object) {
        Class<?> clazz = object.getClass();
        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz, converterClass);
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        for (FieldInfo fieldInfo : sortedFieldList) {
            if (!fieldInfo.isSerialize()) {
                fieldInfo.set(object, ClassUtil.getDefaultValue(fieldInfo.getFieldClass()));
            }
        }

        super.write(kryo, output, object);
    }

    @Override
    public T read(Kryo kryo, Input input, Class<T> type) {
        T read = super.read(kryo, input, type);
        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(type, converterClass);
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        for (FieldInfo fieldInfo : sortedFieldList) {
            if (!fieldInfo.isDeserialize()) {
                fieldInfo.set(read, ClassUtil.getDefaultValue(fieldInfo.getFieldClass()));
            }
        }
        return read;
    }
}
