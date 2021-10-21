package com.chm.converter.avro.factorys;

import com.chm.converter.codec.Java8TimeCodec;
import com.chm.converter.core.Converter;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * JAVA8时间类转换工厂类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-17
 **/
public class Java8TimeConversion<T extends TemporalAccessor> extends Conversion<T> {

    private final Java8TimeCodec<T> java8TimeCodec;

    private final LogicalType logicalType;

    public Java8TimeConversion(Class<T> clazz, Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter, clazz.getName());
    }

    public Java8TimeConversion(Class<T> clazz, String datePattern, Converter<?> converter, String logicalTypeName) {
        this.logicalType = new LogicalType(logicalTypeName);
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, datePattern, converter);
    }

    public Java8TimeConversion(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter, String logicalTypeName) {
        this.logicalType = new LogicalType(logicalTypeName);
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, dateFormatter, converter);
    }

    public Java8TimeConversion<T> withClass(Class<T> clazz) {
        return new Java8TimeConversion<>(clazz, this.java8TimeCodec.getDateFormatter(), this.java8TimeCodec.getConverter(), this.logicalType.getName());
    }

    public Java8TimeConversion<T> withDatePattern(String datePattern) {
        return new Java8TimeConversion<>(this.java8TimeCodec.getClazz(), datePattern, this.java8TimeCodec.getConverter(), this.logicalType.getName());
    }

    public Java8TimeConversion<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new Java8TimeConversion<>(this.java8TimeCodec.getClazz(), dateFormatter, this.java8TimeCodec.getConverter(), this.logicalType.getName());
    }

    public Java8TimeConversion<T> withLogicalTypeName(String logicalTypeName) {
        return new Java8TimeConversion<>(this.java8TimeCodec.getClazz(), this.java8TimeCodec.getDateFormatter(), this.java8TimeCodec.getConverter(), logicalTypeName);
    }

    @Override
    public Class<T> getConvertedType() {
        return this.java8TimeCodec.getClazz();
    }

    @Override
    public String getLogicalTypeName() {
        return logicalType.getName();
    }

    @Override
    public T fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
        return java8TimeCodec.decode(value.toString());
    }

    @Override
    public CharSequence toCharSequence(T value, Schema schema, LogicalType type) {
        return java8TimeCodec.encode(value);
    }

    @Override
    public Schema getRecommendedSchema() {
        return logicalType.addToSchema(Schema.create(Schema.Type.STRING));
    }

    public static final class Java8TimeConversionBuilder<T extends TemporalAccessor> {

        private Class<T> clazz;

        private DateTimeFormatter dateFormatter;

        private String datePattern;

        private Converter<?> converter;

        private String logicalTypeName;

        public Java8TimeConversionBuilder() {
        }

        public Java8TimeConversionBuilder<T> dateType(Class<T> dateType) {
            this.clazz = dateType;
            return this;
        }

        public Java8TimeConversionBuilder<T> dateFormatter(DateTimeFormatter dateFormatter) {
            this.dateFormatter = dateFormatter;
            this.datePattern = null;
            return this;
        }

        public Java8TimeConversionBuilder<T> datePattern(String datePattern) {
            this.dateFormatter = null;
            this.datePattern = datePattern;
            return this;
        }

        public Java8TimeConversionBuilder<T> converter(Converter<?> converter) {
            this.converter = converter;
            return this;
        }

        public Java8TimeConversionBuilder<T> logicalTypeName(String logicalTypeName) {
            this.logicalTypeName = logicalTypeName;
            return this;
        }

        public Java8TimeConversionBuilder<T> java8TimeConversion(Java8TimeConversion<T> java8TimeConversion) {
            if (java8TimeConversion.java8TimeCodec != null) {
                this.clazz = java8TimeConversion.java8TimeCodec.getClazz();
                this.dateFormatter = java8TimeConversion.java8TimeCodec.getDateFormatter();
                this.converter = java8TimeConversion.java8TimeCodec.getConverter();
            }
            if (java8TimeConversion.logicalType != null) {
                this.logicalTypeName = java8TimeConversion.logicalType.getName();
            }
            return this;
        }


        public Java8TimeConversion<T> build() {
            if (this.dateFormatter != null) {
                return new Java8TimeConversion<>(this.clazz, this.dateFormatter, this.converter, this.logicalTypeName);
            } else {
                return new Java8TimeConversion<>(this.clazz, this.datePattern, this.converter, this.logicalTypeName);
            }
        }
    }
}
