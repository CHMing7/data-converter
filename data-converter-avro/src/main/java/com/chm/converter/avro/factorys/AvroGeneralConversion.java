package com.chm.converter.avro.factorys;

import com.chm.converter.avro.AvroReflectData;
import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codecs.JavaBeanCodec;
import com.chm.converter.core.codecs.RuntimeTypeCodec;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.core.utils.StringUtil;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @date 2021-10-14
 **/
public class AvroGeneralConversion<T> extends Conversion<T> {

    private final Converter<?> converter;

    private final UniversalGenerate<Codec> generate;

    private final TypeToken<T> typeToken;

    private final LogicalType logicalType;

    private final JavaBeanInfo<T> javaBeanInfo;

    private Schema schema;

    private final Schema rawSchema;

    private final AvroReflectData data;

    public AvroGeneralConversion(Converter<?> converter, TypeToken<T> typeToken, Schema rawSchema, AvroReflectData data) {
        this(converter, null, typeToken, rawSchema, data);
    }

    public AvroGeneralConversion(Converter<?> converter, UniversalGenerate<Codec> generate, TypeToken<T> typeToken, Schema rawSchema, AvroReflectData data) {
        this.converter = converter;
        Class<? extends Converter> converterClass = converter != null ? converter.getClass() : null;
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);
        this.typeToken = typeToken;
        this.logicalType = new LogicalType(typeToken.toString());
        this.javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(typeToken, converterClass);
        this.rawSchema = rawSchema;
        this.data = data;
    }

    public void init() {
        this.schema = createSchema(this.rawSchema);
    }

    public static Schema makeNullable(Schema schema) {
        if (schema.getType() == Schema.Type.UNION) {
            // check to see if the union already contains NULL
            for (Schema subType : schema.getTypes()) {
                if (subType.getType() == Schema.Type.NULL) {
                    return schema;
                }
            }
            // add null as the first type in a new union
            List<Schema> withNull = new ArrayList<>();
            withNull.add(Schema.create(Schema.Type.NULL));
            withNull.addAll(schema.getTypes());
            return Schema.createUnion(withNull);
        } else {
            // create a union with null
            return Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.NULL), schema));
        }
    }

    private Schema createSchema(Schema rawSchema) {
        Schema record = Schema.createRecord(rawSchema.getName(), rawSchema.getDoc(), rawSchema.getNamespace(), rawSchema.isError());
        List<FieldInfo> fieldInfoList = javaBeanInfo.getSortedFieldList();
        List<Schema.Field> fieldList = new ArrayList<>();
        for (FieldInfo fieldInfo : fieldInfoList) {
            String logicalTypeName = StringUtil.format("{}.{}", javaBeanInfo.getType().toString(), fieldInfo.getName());
            Schema.Field field = rawSchema.getField(fieldInfo.getFieldName());
            LogicalType logicalType = new LogicalType(logicalTypeName);
            Schema fieldSchema;
            Conversion<?> fieldConversion = this.data.getConversionByClass(fieldInfo.getFieldClass(), logicalType);
            if (fieldConversion == null) {
                Codec codec = this.generate.get(fieldInfo.getTypeToken());
                if (codec != null && !(codec instanceof JavaBeanCodec)) {
                    Conversion<?> conversion = this.data.getConversionByClass(fieldInfo.getFieldClass());
                    if (conversion instanceof CoreCodecConversion || codec instanceof RuntimeTypeCodec) {
                        Schema encodeSchema = this.data.createSchema(codec.getEncodeType().getType(), MapUtil.newHashMap(true));
                        CoreCodecConversion<?> coreCodecConversion = new CoreCodecConversion<>(this.converter, fieldInfo.getTypeToken(),
                                codec, encodeSchema, logicalTypeName);
                        data.addLogicalTypeConversion(coreCodecConversion);
                        fieldSchema = makeNullable(coreCodecConversion.getRecommendedSchema());
                    } else {
                        fieldSchema = makeNullable(field.schema());
                    }
                } else if (codec != null) {
                    // codec instanceof JavaBeanCodec
                    fieldSchema = makeNullable(this.data.getSchema(fieldInfo.getFieldType()));
                } else {
                    Schema newFieldSchema = this.data.getSchema(fieldInfo.getFieldType());
                    if (field.schema().getType() != newFieldSchema.getType()) {
                        fieldSchema = makeNullable(newFieldSchema);
                    } else {
                        fieldSchema = makeNullable(field.schema());
                    }
                }
            } else {
                fieldSchema = fieldConversion.getRecommendedSchema();
            }

            Schema.Field recordField = new Schema.Field(fieldInfo.getName(), fieldSchema);
            fieldList.add(recordField);
        }
        record.setFields(fieldList);
        return this.logicalType.addToSchema(record);
    }

    @Override
    public Class<T> getConvertedType() {
        return (Class<T>) typeToken.getRawType();
    }

    @Override
    public String getLogicalTypeName() {
        return logicalType.getName();
    }

    @Override
    public T fromRecord(IndexedRecord value, Schema schema, LogicalType type) {
        T construct = javaBeanInfo.getObjectConstructor().construct();
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        for (FieldInfo fieldInfo : sortedFieldList) {
            Schema.Field field = schema.getField(fieldInfo.getName());
            if (field == null || !fieldInfo.isDeserialize()) {
                continue;
            }
            fieldInfo.set(construct, value.get(field.pos()));
        }
        return construct;
    }

    @Override
    public IndexedRecord toRecord(T value, Schema schema, LogicalType type) {
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        GenericData.Record record = new GenericData.Record(schema);
        for (int i = 0; i < sortedFieldList.size(); i++) {
            FieldInfo fieldInfo = sortedFieldList.get(i);
            Object o = fieldInfo.get(value);
            if (o == null || !fieldInfo.isSerialize()) {
                continue;
            }
            record.put(i, o);
        }
        return record;
    }

    @Override
    public Schema getRecommendedSchema() {
        return this.schema != null ? this.schema : this.rawSchema;
    }
}
