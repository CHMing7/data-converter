package com.chm.converter.spearal;

import cn.hutool.core.collection.ListUtil;
import com.chm.converter.codec.DataCodecGenerate;
import com.chm.converter.codec.DefaultDateCodec;
import com.chm.converter.codec.Java8TimeCodec;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.spearal.coders.CodecProvider;
import com.chm.converter.spearal.impl.introspector.IntrospectorImpl;
import org.spearal.DefaultSpearalFactory;
import org.spearal.SpearalDecoder;
import org.spearal.SpearalEncoder;
import org.spearal.SpearalFactory;
import org.spearal.annotation.Exclude;
import org.spearal.annotation.Include;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-29
 **/
public class DefaultSpearalConverter implements SpearalConverter {

    public static final List<Class<? extends Annotation>> SPEARAL_ANNOTATION_LIST = ListUtil.of(Include.class, Exclude.class);

    public static final String SPEARAL_NAME = "org.spearal.SpearalFactory";

    SpearalFactory factory = new DefaultSpearalFactory();

    private final DataCodecGenerate dataCodec = new DataCodecGenerate(null, this, false);

    {
        // Java8 Time Codec
        dataCodec.put(Instant.class, new Java8TimeCodec<>(Instant.class, this));
        dataCodec.put(LocalDate.class, new Java8TimeCodec<>(LocalDate.class, this));
        dataCodec.put(LocalDateTime.class, new Java8TimeCodec<>(LocalDateTime.class, this));
        dataCodec.put(LocalTime.class, new Java8TimeCodec<>(LocalTime.class, this));
        dataCodec.put(OffsetDateTime.class, new Java8TimeCodec<>(OffsetDateTime.class, this));
        dataCodec.put(OffsetTime.class, new Java8TimeCodec<>(OffsetTime.class, this));
        dataCodec.put(ZonedDateTime.class, new Java8TimeCodec<>(ZonedDateTime.class, this));
        dataCodec.put(MonthDay.class, new Java8TimeCodec<>(MonthDay.class, this));
        dataCodec.put(YearMonth.class, new Java8TimeCodec<>(YearMonth.class, this));
        dataCodec.put(Year.class, new Java8TimeCodec<>(Year.class, this));
        dataCodec.put(ZoneOffset.class, new Java8TimeCodec<>(ZoneOffset.class, this));

        // Default Date Codec
        dataCodec.put(java.sql.Date.class, new DefaultDateCodec<>(java.sql.Date.class, this));
        dataCodec.put(Timestamp.class, new DefaultDateCodec<>(Timestamp.class, this));
        dataCodec.put(Date.class, new DefaultDateCodec<>(Date.class, this));
        factory.getContext().configure(new CodecProvider(dataCodec));
        factory.getContext().configure(new IntrospectorImpl(this, DefaultSpearalConverter::checkExistSpearalAnnotation));
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
