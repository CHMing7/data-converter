package com.chm.converter.avro.factorys;

import com.chm.converter.codec.DefaultDateCodec;
import com.chm.converter.core.Converter;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 默认时间类转换工厂类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-17
 **/
public class DefaultDateConversion<T extends Date> extends Conversion<T> {

    private final DefaultDateCodec<T> defaultDateCodec;

    private final LogicalType logicalType;


    public DefaultDateConversion(Class<T> dateType, Converter<?> converter) {
        this(dateType, (DateTimeFormatter) null, converter, dateType.getName());
    }

    public DefaultDateConversion(Class<T> dateType, String datePattern, Converter<?> converter, String logicalTypeName) {
        this.logicalType = new LogicalType(logicalTypeName);
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, datePattern, converter);
    }

    public DefaultDateConversion(Class<T> dateType, DateTimeFormatter dateFormat, Converter<?> converter, String logicalTypeName) {
        this.logicalType = new LogicalType(logicalTypeName);
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, dateFormat, converter);
    }

    public DefaultDateConversion<T> withDateType(Class<T> dateType) {
        return new DefaultDateConversion<>(dateType, this.defaultDateCodec.getDateFormatter(), this.defaultDateCodec.getConverter(), this.logicalType.getName());
    }

    public DefaultDateConversion<T> withDatePattern(String datePattern) {
        return new DefaultDateConversion<>(this.defaultDateCodec.getDateType(), datePattern, this.defaultDateCodec.getConverter(), this.logicalType.getName());
    }

    public DefaultDateConversion<T> withDateFormat(DateTimeFormatter dateFormat) {
        return new DefaultDateConversion<>(this.defaultDateCodec.getDateType(), dateFormat, this.defaultDateCodec.getConverter(), this.logicalType.getName());
    }

    public DefaultDateConversion<T> withLogicalTypeName(String logicalTypeName) {
        return new DefaultDateConversion<>(this.defaultDateCodec.getDateType(), this.defaultDateCodec.getDateFormatter(), this.defaultDateCodec.getConverter(), logicalTypeName);
    }

    @Override
    public Class<T> getConvertedType() {
        return defaultDateCodec.getDateType();
    }

    @Override
    public String getLogicalTypeName() {
        return logicalType.getName();
    }

    @Override
    public T fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
        return defaultDateCodec.decode(value.toString());
    }

    @Override
    public CharSequence toCharSequence(T value, Schema schema, LogicalType type) {
        return defaultDateCodec.encode(value);
    }

    @Override
    public Schema getRecommendedSchema() {
        return logicalType.addToSchema(Schema.create(Schema.Type.STRING));
    }


    public static final class DefaultDateConversionBuilder<T extends Date> {

        private Class<T> dateType;

        private DateTimeFormatter dateFormatter;

        private String datePattern;

        private Converter<?> converter;

        private String logicalTypeName;


        public DefaultDateConversionBuilder() {
        }

        public DefaultDateConversionBuilder<T> dateType(Class<T> dateType) {
            this.dateType = dateType;
            return this;
        }

        public DefaultDateConversionBuilder<T> dateFormatter(DateTimeFormatter dateFormatter) {
            this.dateFormatter = dateFormatter;
            this.datePattern = null;
            return this;
        }

        public DefaultDateConversionBuilder<T> datePattern(String datePattern) {
            this.dateFormatter = null;
            this.datePattern = datePattern;
            return this;
        }

        public DefaultDateConversionBuilder<T> converter(Converter<?> converter) {
            this.converter = converter;
            return this;
        }

        public DefaultDateConversionBuilder<T> logicalTypeName(String logicalTypeName) {
            this.logicalTypeName = logicalTypeName;
            return this;
        }

        public DefaultDateConversionBuilder<T> defaultDateConversion(DefaultDateConversion<T> defaultDateConversion) {
            if (defaultDateConversion.defaultDateCodec != null) {
                this.dateType = defaultDateConversion.defaultDateCodec.getDateType();
                this.dateFormatter = defaultDateConversion.defaultDateCodec.getDateFormatter();
                this.converter = defaultDateConversion.defaultDateCodec.getConverter();
            }
            if (defaultDateConversion.logicalType != null) {
                this.logicalTypeName = defaultDateConversion.logicalType.getName();
            }
            return this;
        }




        public DefaultDateConversion<T> build() {
            if (this.dateFormatter != null) {
                return new DefaultDateConversion<>(this.dateType, this.dateFormatter, this.converter, this.logicalTypeName);
            } else {
                return new DefaultDateConversion<>(this.dateType, this.datePattern, this.converter, this.logicalTypeName);
            }
        }
    }
}
