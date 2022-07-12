package com.chm.converter.test.spearal;

import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.chm.converter.core.constant.TimeConstant;
import com.chm.converter.core.utils.ClassUtil;
import org.spearal.SpearalContext;
import org.spearal.configuration.CoderProvider;
import org.spearal.configuration.ConverterProvider;
import org.spearal.impl.ExtendedSpearalEncoder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-27
 **/
public class DefaultDateCoder<T extends Date> implements CoderProvider, CoderProvider.Coder, ConverterProvider, ConverterProvider.Converter<T>, WithFormat {

    private final DefaultDateCodec<T> defaultDateCodec;

    public DefaultDateCoder(Class<T> dateType) {
        this(dateType, (DateTimeFormatter) null, null);
    }

    public DefaultDateCoder(Class<T> dateType, String datePattern) {
        this(dateType, datePattern, null);
    }

    public DefaultDateCoder(Class<T> dateType, DateTimeFormatter dateFormatter) {
        this(dateType, dateFormatter, null);
    }

    public DefaultDateCoder(Class<T> dateType, com.chm.converter.core.Converter<?> converter) {
        this(dateType, (DateTimeFormatter) null, converter);
    }

    public DefaultDateCoder(Class<T> dateType, String datePattern, com.chm.converter.core.Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, datePattern, converter);
    }

    public DefaultDateCoder(Class<T> dateType, DateTimeFormatter dateFormatter, com.chm.converter.core.Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, dateFormatter, converter);
    }

    public DefaultDateCoder<T> withDateType(Class<T> dateType) {
        return new DefaultDateCoder<>(dateType, this.defaultDateCodec.getDateFormatter(), this.defaultDateCodec.getConverter());
    }

    @Override
    public DefaultDateCoder<T> withDatePattern(String datePattern) {
        return new DefaultDateCoder<>(this.defaultDateCodec.getDateType(), datePattern, this.defaultDateCodec.getConverter());
    }

    @Override
    public DefaultDateCoder<T> withDateFormatter(DateTimeFormatter dateFormat) {
        return new DefaultDateCoder<>(this.defaultDateCodec.getDateType(), dateFormat, this.defaultDateCodec.getConverter());
    }

    @Override
    public Coder getCoder(Class<?> valueClass) {
        Optional<Class<? extends Date>> first = TimeConstant.DEFAULT_DATE_SET.stream()
                .filter(dateClass -> dateClass.isAssignableFrom(valueClass)).findFirst();
        return first.map(clazz -> new DefaultDateCoder(clazz, this.defaultDateCodec.getConverter())).orElse(null);
    }

    @Override
    public void encode(ExtendedSpearalEncoder encoder, Object value) throws IOException {
        this.defaultDateCodec.write((T) value, encoder::writeString);
    }

    @Override
    public Converter<?> getConverter(Class<?> valueClass, Type targetType) {
        Optional<Class<? extends Date>> first = TimeConstant.DEFAULT_DATE_SET.stream()
                .filter(dateClass -> dateClass.isAssignableFrom(ClassUtil.getClassByType(targetType))).findFirst();
        return first.map(clazz -> new DefaultDateCoder(clazz, this.defaultDateCodec.getConverter())).orElse(null);
    }

    @Override
    public T convert(SpearalContext context, Object value, Type targetType) {
        return this.defaultDateCodec.decode((String) value);
    }
}
