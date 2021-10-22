package com.chm.converter.avro.factorys;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.utils.StringUtil;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;

import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-10-14
 **/
public class AvroGeneralConversion<T> extends Conversion<T> {

    private final Class<T> clazz;

    private final LogicalType logicalType;

    private final JavaBeanInfo<T> javaBeanInfo;

    private final Schema schema;

    public AvroGeneralConversion(Class<T> clazz, Schema schema, Class<? extends Converter> converterClass, GenericData data) {
        this.clazz = clazz;
        this.logicalType = new LogicalType(clazz.getName());
        this.javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz, converterClass);
        this.schema = createSchema(schema, data);
    }

    private Schema createSchema(Schema schema, GenericData data) {
        Schema record = Schema.createRecord(schema.getName(), schema.getDoc(), schema.getNamespace(), schema.isError());
        List<FieldInfo> fieldInfoList = javaBeanInfo.getSortedFieldList();
        List<Schema.Field> fieldList = new ArrayList<>();
        for (FieldInfo fieldInfo : fieldInfoList) {
            String logicalTypeName = StringUtil.format("{}.{}", javaBeanInfo.getClazz().getName(), fieldInfo.getName());
            Schema.Field field = schema.getField(fieldInfo.getFieldName());
            LogicalType logicalType = new LogicalType(logicalTypeName);
            Conversion<?> fieldConversion = data.getConversionByClass(fieldInfo.getFieldClass());
            Schema fieldSchema;
            if (fieldConversion instanceof AvroJava8TimeConversion &&
                    data.getConversionByClass(fieldInfo.getFieldClass(), logicalType) == null) {
                AvroJava8TimeConversion java8TimeConversion = new AvroJava8TimeConversion.AvroJava8TimeConversionBuilder<>()
                        .java8TimeConversion((AvroJava8TimeConversion<TemporalAccessor>) fieldConversion)
                        .logicalTypeName(logicalTypeName).datePattern(fieldInfo.getFormat()).build();
                data.addLogicalTypeConversion(java8TimeConversion);
                fieldSchema = makeNullable(java8TimeConversion.getRecommendedSchema());
            } else if (fieldConversion instanceof AvroDefaultDateConversion
                    && data.getConversionByClass(fieldInfo.getFieldClass(), logicalType) == null) {
                AvroDefaultDateConversion dateDefaultDateConversion = new AvroDefaultDateConversion.AvroDefaultDateConversionBuilder<>()
                        .defaultDateConversion((AvroDefaultDateConversion<Date>) fieldConversion)
                        .logicalTypeName(logicalTypeName).datePattern(fieldInfo.getFormat()).build();
                data.addLogicalTypeConversion(dateDefaultDateConversion);
                fieldSchema = makeNullable(dateDefaultDateConversion.getRecommendedSchema());
            } else {
                fieldSchema = field.schema();
            }
            Schema.Field recordField = new Schema.Field(fieldInfo.getName(), fieldSchema, field.doc(), field.defaultVal());
            fieldList.add(recordField);
        }
        record.setFields(fieldList);
        return logicalType.addToSchema(record);
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

    @Override
    public Class<T> getConvertedType() {
        return clazz;
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
            if (field == null) {
                continue;
            }
            if (!fieldInfo.isDeserialize()) {
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
            if (!fieldInfo.isSerialize()) {
                continue;
            }
            record.put(i, fieldInfo.get(value));
        }
        return record;
    }

    @Override
    public Schema getRecommendedSchema() {
        return schema;
    }

}
