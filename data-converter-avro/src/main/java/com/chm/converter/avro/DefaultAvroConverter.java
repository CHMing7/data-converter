package com.chm.converter.avro;

import com.chm.converter.avro.factorys.DefaultDateConversionFactory;
import com.chm.converter.avro.factorys.Java8TimeConversionFactory;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.StringUtil;
import org.apache.avro.Conversion;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;

/**
 * 默认avro数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-16
 **/
public class DefaultAvroConverter implements AvroConverter {

    public static final String AVRO_NAME = "org.apache.avro.io.BinaryData";

    private static final EncoderFactory ENCODER_FACTORY = EncoderFactory.get();

    private static final DecoderFactory DECODER_FACTORY = DecoderFactory.get();

    protected ReflectData reflectData = new ReflectData.AllowNull();

    {
        // java8Time Conversion
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(Instant.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(LocalDate.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(LocalDateTime.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(LocalTime.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(OffsetDateTime.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(OffsetTime.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(ZonedDateTime.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(MonthDay.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(YearMonth.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(Year.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(ZoneOffset.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(ZoneOffset.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(ZoneOffset.class));
        reflectData.addLogicalTypeConversion(Java8TimeConversionFactory.create(ZoneOffset.class));

        // DefaultDate Conversion
        reflectData.addLogicalTypeConversion(DefaultDateConversionFactory.create(java.sql.Date.class));
        reflectData.addLogicalTypeConversion(DefaultDateConversionFactory.create(Timestamp.class));
        reflectData.addLogicalTypeConversion(DefaultDateConversionFactory.create(Date.class));
    }

    public ReflectData getReflectData() {
        return reflectData;
    }

    public void setReflectData(ReflectData reflectData) {
        this.reflectData = reflectData;
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        Schema schema = reflectData.getSchema(targetType);
        try {
            return deserializer(source, schema);
        } catch (IOException e) {

            throw new ConvertException(StringUtil.format("bytes data cannot be avro deserialized to type: {}", targetType.getName()), e);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        Schema schema = reflectData.getSchema(targetType);
        try {
            return deserializer(source, schema);
        } catch (IOException e) {
            throw new ConvertException(StringUtil.format("bytes data cannot be avro deserialized to type: {}", targetType.getTypeName()), e);
        }
    }

    private <T> T deserializer(byte[] source, Schema schema) throws IOException {
        if (source == null) {
            return null;
        }
        InputStream in = new ByteArrayInputStream(source);
        BinaryDecoder decoder = DECODER_FACTORY.binaryDecoder(in, null);
        ReflectDatumReader<T> reader = new ReflectDatumReader<>(schema, schema, reflectData);
        return reader.read(null, decoder);
    }

    @Override
    public byte[] encode(Object source) {
        if (source == null) {
            return new byte[0];
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BinaryEncoder encoder = ENCODER_FACTORY.binaryEncoder(os, null);
        // 获取Schema
        Conversion<?> conversion = reflectData.getConversionByClass(source.getClass());
        Schema schema = conversion != null ? conversion.getRecommendedSchema() : reflectData.induce(source);
        ReflectDatumWriter dd = new ReflectDatumWriter(schema, reflectData);
        try {
            dd.write(source, encoder);
            encoder.flush();
            return os.toByteArray();
        } catch (IOException e) {
            throw new ConvertException(StringUtil.format("data cannot be serialized to avro bytes, data type: {}", source.getClass()), e);
        }
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Avro相关类型是否存在
            Class.forName(AVRO_NAME);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
