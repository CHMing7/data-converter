package com.chm.converter.fastjson2.reader;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.FieldReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.schema.JSONSchema;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.utils.MapUtil;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-05
 **/
public class Fastjson2FieldReader<T> implements FieldReader<T> {

    private static final Map<FieldInfo, ObjectReader> FIELD_INFO_OBJECT_READER_MAP = MapUtil.newConcurrentHashMap();

    private final FieldInfo fieldInfo;

    private final JSONSchema jsonSchema;

    public Fastjson2FieldReader(FieldInfo fieldInfo, JSONSchema jsonSchema) {
        this.fieldInfo = fieldInfo;
        this.jsonSchema = jsonSchema;
    }

    @Override
    public Type getFieldType() {
        return fieldInfo.getFieldType();
    }

    @Override
    public JSONSchema getSchema() {
        return this.jsonSchema;
    }

    @Override
    public String getFieldName() {
        return fieldInfo.getName();
    }

    @Override
    public void accept(Object object, Object value) {
        fieldInfo.set(object, value);
    }

    @Override
    public void readFieldValue(JSONReader jsonReader, Object object) {
        ObjectReader reader = getObjectReader(jsonReader);
        Object o;
        if (jsonReader.isJSONB()) {
            o = reader.readJSONBObject(jsonReader, getFeatures());
        } else {
            o = reader.readObject(jsonReader, getFeatures());
        }
        accept(object, o);
    }

    @Override
    public ObjectReader getObjectReader(JSONReader jsonReader) {
        return MapUtil.computeIfAbsent(FIELD_INFO_OBJECT_READER_MAP, fieldInfo, info -> {
            ObjectReader objectReader = jsonReader.getObjectReader(fieldInfo.getFieldType());
            if (objectReader instanceof WithFormat) {
                objectReader = (ObjectReader) ((WithFormat) objectReader).withDatePattern(fieldInfo.getFormat());
            }
            return objectReader;
        });
    }

    @Override
    public int compareTo(FieldReader o) {
        if (o instanceof Fastjson2FieldReader) {
            Fastjson2FieldReader fieldWriter = (Fastjson2FieldReader) o;
            return this.fieldInfo.compareTo(fieldWriter.fieldInfo);
        }
        return FieldReader.super.compareTo(o);
    }
}
