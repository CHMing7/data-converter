package com.chm.converter.avro.factorys;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.WithFormat;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericFixed;

import java.nio.ByteBuffer;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-20
 **/
public class CoreCodecConversion<T> extends Conversion<T> implements WithFormat {

    private final Converter<?> converter;

    private final Class<T> classOfT;

    private final Codec codec;

    private final LogicalType logicalType;

    private final Schema schema;

    public CoreCodecConversion(Converter<?> converter, Class<T> classOfT, Codec codec, Schema encodeSchema, String logicalTypeName) {
        this.converter = converter;
        this.classOfT = classOfT;
        this.codec = codec;
        this.logicalType = new LogicalType(logicalTypeName);
        this.schema = logicalType.addToSchema(encodeSchema);
    }

    @Override
    public Class<T> getConvertedType() {
        return classOfT;
    }

    @Override
    public String getLogicalTypeName() {
        return logicalType.getName();
    }

    @Override
    public T fromBoolean(Boolean value, Schema schema, LogicalType type) {
        return (T) codec.decode(value);
    }

    @Override
    public T fromInt(Integer value, Schema schema, LogicalType type) {
        return (T) codec.decode(value);
    }

    @Override
    public T fromLong(Long value, Schema schema, LogicalType type) {
        return (T) codec.decode(value);
    }

    @Override
    public T fromFloat(Float value, Schema schema, LogicalType type) {
        return (T) codec.decode(value);
    }

    @Override
    public T fromDouble(Double value, Schema schema, LogicalType type) {
        return (T) codec.decode(value);
    }

    @Override
    public T fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
        return (T) codec.decode(value);
    }

    @Override
    public T fromEnumSymbol(GenericEnumSymbol value, Schema schema, LogicalType type) {
        return (T) codec.decode(value);
    }

    @Override
    public T fromFixed(GenericFixed value, Schema schema, LogicalType type) {
        return (T) codec.decode(value);
    }

    @Override
    public T fromBytes(ByteBuffer value, Schema schema, LogicalType type) {
        return (T) codec.decode(value);
    }

    @Override
    public T fromArray(Collection<?> value, Schema schema, LogicalType type) {
        return (T) codec.decode(value);
    }

    @Override
    public T fromMap(Map<?, ?> value, Schema schema, LogicalType type) {
        return (T) codec.decode(value);
    }

    @Override
    public Boolean toBoolean(T value, Schema schema, LogicalType type) {
        return (Boolean) codec.encode(value);
    }

    @Override
    public Integer toInt(T value, Schema schema, LogicalType type) {
        return (Integer) codec.encode(value);
    }

    @Override
    public Long toLong(T value, Schema schema, LogicalType type) {
        return (Long) codec.encode(value);
    }

    @Override
    public Float toFloat(T value, Schema schema, LogicalType type) {
        return (Float) codec.encode(value);
    }

    @Override
    public Double toDouble(T value, Schema schema, LogicalType type) {
        return (Double) codec.encode(value);
    }

    @Override
    public CharSequence toCharSequence(T value, Schema schema, LogicalType type) {
        return (CharSequence) codec.encode(value);
    }

    @Override
    public GenericEnumSymbol toEnumSymbol(T value, Schema schema, LogicalType type) {
        return (GenericEnumSymbol) codec.encode(value);
    }

    @Override
    public GenericFixed toFixed(T value, Schema schema, LogicalType type) {
        return (GenericFixed) codec.encode(value);
    }

    @Override
    public ByteBuffer toBytes(T value, Schema schema, LogicalType type) {
        return (ByteBuffer) codec.encode(value);
    }

    @Override
    public Collection<?> toArray(T value, Schema schema, LogicalType type) {
        return (Collection<?>) codec.encode(value);
    }

    @Override
    public Map<?, ?> toMap(T value, Schema schema, LogicalType type) {
        return (Map<?, ?>) codec.encode(value);
    }

    @Override
    public Schema getRecommendedSchema() {
        return schema;
    }

    @Override
    public CoreCodecConversion<T> withDatePattern(String datePattern) {
        if (codec instanceof WithFormat) {
            Codec withCodec = (Codec) ((WithFormat) codec).withDatePattern(datePattern);
            return new CoreCodecConversion<>(this.converter, this.classOfT, withCodec, this.schema, this.logicalType.getName());
        }
        return new CoreCodecConversion<>(this.converter, this.classOfT, this.codec, this.schema, this.logicalType.getName());
    }

    @Override
    public CoreCodecConversion<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        if (this.codec instanceof WithFormat) {
            Codec withCodec = (Codec) ((WithFormat) this.codec).withDateFormatter(dateFormatter);
            return new CoreCodecConversion<>(this.converter, this.classOfT, withCodec, this.schema, this.logicalType.getName());
        }
        return new CoreCodecConversion<>(this.converter, this.classOfT, this.codec, this.schema, this.logicalType.getName());
    }

    public CoreCodecConversion<T> withLogicalTypeName(String logicalTypeName) {
        return new CoreCodecConversion<>(this.converter, this.classOfT, this.codec, this.schema, logicalTypeName);
    }

    public boolean isPriorityUse() {
        return this.codec.isPriorityUse();
    }
}
