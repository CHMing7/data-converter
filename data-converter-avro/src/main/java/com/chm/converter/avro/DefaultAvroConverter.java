package com.chm.converter.avro;

import com.chm.converter.core.Converter;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ListUtil;
import com.google.auto.service.AutoService;
import org.apache.avro.Conversion;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.AvroAlias;
import org.apache.avro.reflect.AvroDefault;
import org.apache.avro.reflect.AvroDoc;
import org.apache.avro.reflect.AvroEncode;
import org.apache.avro.reflect.AvroIgnore;
import org.apache.avro.reflect.AvroMeta;
import org.apache.avro.reflect.AvroName;
import org.apache.avro.reflect.AvroSchema;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 默认avro数据转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-16
 **/
@AutoService(Converter.class)
public class DefaultAvroConverter implements AvroConverter {

    public static final List<Class<? extends Annotation>> AVRO_ANNOTATION_LIST = ListUtil.of(AvroAlias.class,
            AvroDefault.class, AvroDoc.class, AvroEncode.class, AvroIgnore.class, AvroMeta.class, AvroName.class,
            AvroSchema.class);

    public static final String AVRO_NAME = "org.apache.avro.io.BinaryData";

    private static final EncoderFactory ENCODER_FACTORY = EncoderFactory.get();

    private static final DecoderFactory DECODER_FACTORY = DecoderFactory.get();

    protected ReflectData reflectData = new AvroReflectData(this, DefaultAvroConverter::checkExistAvroAnnotation);

    public static boolean checkExistAvroAnnotation(Class<?> cls) {
        return JavaBeanInfo.checkExistAnnotation(cls, AVRO_ANNOTATION_LIST);
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
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), byte[].class.getName(), targetType.getName(), e);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        Schema schema = reflectData.getSchema(targetType);
        try {
            return deserializer(source, schema);
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), byte[].class.getName(), targetType.getTypeName(), e);
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
        } catch (Exception e) {
            throw new ConvertException(getConverterName(), source.getClass().getName(), byte[].class.getName(), e);
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
