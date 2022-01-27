package com.chm.converter.test.spearal;

import com.chm.converter.core.codecs.Java8TimeCodec;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.utils.ClassUtil;
import org.spearal.SpearalContext;
import org.spearal.configuration.CoderProvider;
import org.spearal.configuration.ConverterProvider;
import org.spearal.impl.ExtendedSpearalEncoder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-27
 **/
public class Java8TimeCoder<T extends TemporalAccessor> implements CoderProvider, CoderProvider.Coder, ConverterProvider, ConverterProvider.Converter {

    private final Java8TimeCodec<T> java8TimeCodec;

    public Java8TimeCoder(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public Java8TimeCoder(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public Java8TimeCoder(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public Java8TimeCoder(Class<T> clazz, com.chm.converter.core.Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter);
    }

    public Java8TimeCoder(Class<T> clazz, String datePattern, com.chm.converter.core.Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, datePattern, converter);
    }

    public Java8TimeCoder(Class<T> clazz, DateTimeFormatter dateFormatter, com.chm.converter.core.Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, dateFormatter, converter);
    }

    public Java8TimeCoder<T> withDatePattern(String datePattern) {
        return new Java8TimeCoder<>(this.java8TimeCodec.getClazz(), datePattern, this.java8TimeCodec.getConverter());
    }

    public Java8TimeCoder<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new Java8TimeCoder<>(this.java8TimeCodec.getClazz(), dateFormatter, this.java8TimeCodec.getConverter());
    }

    public Java8TimeCoder<T> withClass(Class<T> clazz) {
        return new Java8TimeCoder<>(clazz, this.java8TimeCodec.getDateFormatter(), this.java8TimeCodec.getConverter());
    }

    @Override
    public Coder getCoder(Class<?> valueClass) {
        Optional<Class<? extends TemporalAccessor>> first = TimeConstant.TEMPORAL_ACCESSOR_SET.stream()
                .filter(temporalAccessorClass -> temporalAccessorClass.isAssignableFrom(valueClass)).findFirst();
        return first.map(clazz -> new Java8TimeCoder<>(clazz, this.java8TimeCodec.getConverter())).orElse(null);
    }

    @Override
    public void encode(ExtendedSpearalEncoder encoder, Object value) throws IOException {
        this.java8TimeCodec.write((T) value, encoder::writeString);
    }

    @Override
    public Converter<?> getConverter(Class<?> valueClass, Type targetType) {
        Optional<Class<? extends TemporalAccessor>> first = TimeConstant.TEMPORAL_ACCESSOR_SET.stream()
                .filter(temporalAccessorClass -> temporalAccessorClass.isAssignableFrom(ClassUtil.getClassByType(targetType))).findFirst();
        return first.map(clazz -> new Java8TimeCoder<>(clazz, this.java8TimeCodec.getConverter())).orElse(null);
    }

    @Override
    public Object convert(SpearalContext context, Object value, Type targetType) {
        return this.java8TimeCodec.decode((String) value);
    }
}
