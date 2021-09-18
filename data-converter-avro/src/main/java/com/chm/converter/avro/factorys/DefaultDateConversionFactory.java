package com.chm.converter.avro.factorys;

import com.chm.converter.codec.DefaultDateCodec;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.util.Date;

/**
 * 默认时间类转换工厂类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-17
 **/
public class DefaultDateConversionFactory {

    public static <T extends Date> DefaultDateConversion<T> create(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        return new DefaultDateConversion<>(clazz, clazz.getSimpleName());
    }

    public static class DefaultDateConversion<T extends Date> extends Conversion<T> {

        private final Class<T> clazz;

        private final DefaultDateCodec<T> defaultDateCodec;

        private final LogicalType logicalType;

        public DefaultDateConversion(Class<T> clazz, String logicalTypeName) {
            this.clazz = clazz;
            this.defaultDateCodec = new DefaultDateCodec<>(clazz);
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
    }
}
