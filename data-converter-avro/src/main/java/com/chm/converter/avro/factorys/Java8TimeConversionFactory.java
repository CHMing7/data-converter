package com.chm.converter.avro.factorys;

import com.chm.converter.codec.Java8TimeCodec;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.temporal.TemporalAccessor;

/**
 * JAVA8时间类转换工厂类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-17
 **/
public class Java8TimeConversionFactory {

    public static <T extends TemporalAccessor> Java8TimeConversion<T> create(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        return new Java8TimeConversion<>(clazz, clazz.getSimpleName());
    }

    public static class Java8TimeConversion<T extends TemporalAccessor> extends Conversion<T> {

        private final Class<T> clazz;

        private final Java8TimeCodec<T> java8TimeCodec;

        private final LogicalType logicalType;

        public Java8TimeConversion(Class<T> clazz, String logicalTypeName) {
            this.clazz = clazz;
            this.java8TimeCodec = new Java8TimeCodec<>(clazz);
            this.logicalType = new LogicalType(logicalTypeName);
        }

        @Override
        public Class<T> getConvertedType() {
            return clazz;
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
    }
}
