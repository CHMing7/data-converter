package com.chm.converter.jsonb.fastjson2;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.JsonbCoder;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.annotation.JSONBuilder;
import com.alibaba.fastjson2.annotation.JSONCreator;
import com.alibaba.fastjson2.annotation.JSONField;
import com.alibaba.fastjson2.annotation.JSONType;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import com.chm.converter.core.Converter;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.exception.ConvertException;
import com.chm.converter.core.utils.ArrayUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.fastjson2.reader.Fastjson2ObjectReaderProvider;
import com.chm.converter.fastjson2.writer.Fastjson2ObjectWriterProvider;
import com.chm.converter.jsonb.JsonbConverter;
import com.google.auto.service.AutoService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 使用Fastjson2-jsonb实现的数据转换器实现类
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-07-06
 **/
@AutoService(Converter.class)
public class Fastjson2JsonbConverter implements JsonbConverter {

    public static final List<Class<? extends Annotation>> FASTJSON_ANNOTATION_LIST = ListUtil.of(JSONCreator.class,
            JSONField.class,
            JSONBuilder.class,
            JSONType.class);

    public static final String FAST_JSON2_NAME = "com.alibaba.fastjson2.JSONB";

    private JSONWriter.Feature[] writerFeatureArray = new JSONWriter.Feature[0];

    private JSONReader.Feature[] readerFeatureArray = new JSONReader.Feature[0];

    private final ObjectWriterProvider writerProvider;

    private final ObjectReaderProvider readerProvider;

    public Fastjson2JsonbConverter() {
        writerProvider = new Fastjson2ObjectWriterProvider(this, Fastjson2JsonbConverter::checkExistFastjson2Annotation);
        readerProvider = new Fastjson2ObjectReaderProvider(this, Fastjson2JsonbConverter::checkExistFastjson2Annotation);
    }

    /**
     * 设置FastJson的序列化特性对象
     *
     * @param writerFeature FastJson的序列化特性对象，{@link JSONWriter.Feature}枚举实例
     */
    public void addWriterFeature(JSONWriter.Feature writerFeature) {
        this.writerFeatureArray = ArrayUtil.append(this.writerFeatureArray, writerFeature);
    }

    /**
     * 设置FastJson的序列化特性对象
     *
     * @param readerFeature FastJson的反序列化特性对象，{@link JSONReader.Feature}枚举实例
     */
    public void addReaderFeature(JSONReader.Feature readerFeature) {
        this.readerFeatureArray = ArrayUtil.append(this.readerFeatureArray, readerFeature);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        return privateConvertToJavaObject(source, targetType);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        return privateConvertToJavaObject(source, targetType);
    }

    public <T> T convertToJavaObject(byte[] source, TypeReference<T> typeReference) {
        return privateConvertToJavaObject(source, typeReference.getType());
    }

    private <T> T privateConvertToJavaObject(byte[] source, Type targetType) {
        if (ArrayUtil.isEmpty(source)) {
            return null;
        }
        JSONReader.Context readContext = new JSONReader.Context(readerProvider);
        try (JSONReader reader = JsonbCoder.ofJsonbReader(readContext, source)) {
            readContext.config(this.readerFeatureArray);
            ObjectReader<T> objectReader = readContext.getProvider().getObjectReader(targetType);

            T object = objectReader.readJSONBObject(reader, 0);
            reader.handleResolveTasks(object);
            return object;
        }
    }

    private byte[] parseToBytes(Object obj) {
        JSONWriter.Context writeContext = new JSONWriter.Context(writerProvider, this.writerFeatureArray);
        try (JSONWriter writer = JsonbCoder.ofJsonbWriter(writeContext)) {
            if (obj == null) {
                writer.writeNull();
            } else {
                Class<?> valueClass = obj.getClass();
                ObjectWriter<?> objectWriter = writer.getObjectWriter(valueClass, valueClass);
                objectWriter.write(writer, obj, null, null, 0);
            }
            return writer.getBytes();
        } catch (NullPointerException | NumberFormatException ex) {
            throw new JSONException("toJSONString error", ex);
        }
    }

    @Override
    public byte[] encode(Object source) {
        if (source == null) {
            return new byte[0];
        }
        try {
            return parseToBytes(source);
        } catch (Throwable th) {
            throw new ConvertException(getConverterName(), source.getClass().getName(), String.class.getName(), th);
        }
    }

    @Override
    public boolean checkCanBeLoad() {
        try {
            // 检测Fastjson相关类型是否存在
            Class.forName(FAST_JSON2_NAME);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean checkExistFastjson2Annotation(Class<?> cls) {
        return JavaBeanInfo.checkExistAnnotation(cls, FASTJSON_ANNOTATION_LIST);
    }
}