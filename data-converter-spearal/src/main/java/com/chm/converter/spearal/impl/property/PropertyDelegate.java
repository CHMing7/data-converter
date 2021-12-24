package com.chm.converter.spearal.impl.property;

import com.chm.converter.codec.Codec;
import com.chm.converter.codec.DataCodecGenerate;
import com.chm.converter.codec.DefaultDateCodec;
import com.chm.converter.codec.Java8TimeCodec;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.spearal.coders.CodecProvider;
import org.spearal.SpearalContext;
import org.spearal.configuration.CoderProvider;
import org.spearal.configuration.PropertyFactory.Property;
import org.spearal.impl.ExtendedSpearalDecoder;
import org.spearal.impl.ExtendedSpearalEncoder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-23
 **/
public class PropertyDelegate implements Property {

    private final FieldInfo fieldInfo;

    private final Property property;

    private Codec codec;

    public PropertyDelegate(FieldInfo fieldInfo, Property property) {
        this.fieldInfo = fieldInfo;
        this.property = property;
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public Class<?> getType() {
        return property.getType();
    }

    @Override
    public Type getGenericType() {
        return property.getGenericType();
    }

    @Override
    public boolean hasField() {
        return property.hasField();
    }

    @Override
    public Field getField() {
        return property.getField();
    }

    @Override
    public boolean hasGetter() {
        return property.hasGetter();
    }

    @Override
    public Method getGetter() {
        return property.getGetter();
    }

    @Override
    public boolean hasSetter() {
        return property.hasSetter();
    }

    @Override
    public Method getSetter() {
        return property.getSetter();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return property.getDeclaringClass();
    }

    @Override
    public Object init(ExtendedSpearalDecoder decoder, Object holder) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        return property.init(decoder, holder);
    }

    @Override
    public Object get(Object holder) throws IllegalAccessException, InvocationTargetException {
        return property.get(holder);
    }

    @Override
    public void set(Object holder, Object value) throws IllegalAccessException, InvocationTargetException {
        property.set(holder, value);
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return property.isAnnotationPresent(annotationClass);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return property.getAnnotation(annotationClass);
    }

    @Override
    public boolean isReadOnly() {
        return property.isReadOnly();
    }

    @Override
    public void write(ExtendedSpearalEncoder encoder, Object holder) throws IOException, IllegalAccessException, InvocationTargetException {
        Codec codec = getCodec(encoder.getContext());
        Object o = fieldInfo.get(holder);
        encoder.writeAny(fieldInfo.isSerialize() ? codec != null ? codec.encode(o) : o : null);
    }

    @Override
    public void read(ExtendedSpearalDecoder decoder, Object holder, int parameterizedType) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Codec codec = getCodec(decoder.getContext());
        Object o = decoder.readAny(parameterizedType);
        fieldInfo.set(holder, fieldInfo.isDeserialize() ? codec != null ? codec.decode(o) : o : null);
    }

    private Codec getCodec(SpearalContext context) {
        if (codec != null) {
            return codec;
        }
        CoderProvider.Coder coder = context.getCoder(fieldInfo.getFieldClass());
        if (coder instanceof CodecProvider) {
            DataCodecGenerate dataCodec = ((CodecProvider) coder).getDataCodec();
            codec = dataCodec.get(fieldInfo.getFieldClass());
            if (codec instanceof DefaultDateCodec) {
                codec = ((DefaultDateCodec<?>) codec).withDatePattern(fieldInfo.getFormat());
            }
            if (codec instanceof Java8TimeCodec) {
                codec = ((Java8TimeCodec<?>) codec).withDatePattern(fieldInfo.getFormat());
            }
            return codec;
        }
        return null;
    }
}
