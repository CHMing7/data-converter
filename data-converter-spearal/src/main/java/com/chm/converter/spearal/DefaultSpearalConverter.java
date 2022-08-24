package com.chm.converter.spearal;

import com.chm.converter.core.Converter;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.spearal.coders.CodecProvider;
import com.chm.converter.spearal.impl.introspector.IntrospectorImpl;
import com.google.auto.service.AutoService;
import org.spearal.DefaultSpearalFactory;
import org.spearal.SpearalDecoder;
import org.spearal.SpearalEncoder;
import org.spearal.SpearalFactory;
import org.spearal.annotation.Exclude;
import org.spearal.annotation.Include;
import org.spearal.configuration.Securizer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-29
 **/
@AutoService(Converter.class)
public class DefaultSpearalConverter implements SpearalConverter {

    public static final List<Class<? extends Annotation>> SPEARAL_ANNOTATION_LIST = ListUtil.of(Include.class, Exclude.class);

    public static final String SPEARAL_NAME = "org.spearal.SpearalFactory";

    SpearalFactory factory = new DefaultSpearalFactory();

    private final DataCodecGenerate dataCodec = DataCodecGenerate.getDataCodecGenerate(this);

    {
        factory.getContext().configure(new CodecProvider(dataCodec));
        factory.getContext().configure(new IntrospectorImpl(this, DefaultSpearalConverter::checkExistSpearalAnnotation));
        factory.getContext().configure(new Securizer() {
            @Override
            public void checkDecodable(Type type) throws SecurityException {
            }

            @Override
            public void checkEncodable(Class<?> cls) throws SecurityException {
            }
        });
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        ByteArrayInputStream bais = new ByteArrayInputStream(source);
        SpearalDecoder decoder = factory.newDecoder(bais);
        try {
            return decoder.readAny(targetType);
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), byte[].class.getName(), targetType.getName(), e);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        ByteArrayInputStream bais = new ByteArrayInputStream(source);
        SpearalDecoder decoder = factory.newDecoder(bais);
        try {
            return decoder.readAny(targetType);
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), byte[].class.getName(), targetType.getTypeName(), e);
        }
    }

    @Override
    public byte[] encode(Object source) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SpearalEncoder encoder = factory.newEncoder(baos);
        try {
            encoder.writeAny(source);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), source.getClass().getName(), byte[].class.getName(), e);
        }
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Spearal相关类型是否存在
            Class.forName(SPEARAL_NAME);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean checkExistSpearalAnnotation(Class<?> cls) {
        return JavaBeanInfo.checkExistAnnotation(cls, SPEARAL_ANNOTATION_LIST);
    }
}
