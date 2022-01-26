package com.chm.converter.avro;

import com.chm.converter.avro.factorys.AvroEnumConversion;
import com.chm.converter.avro.factorys.AvroGeneralConversion;
import com.chm.converter.core.Converter;
import com.chm.converter.core.UseOriginalJudge;
import com.chm.converter.core.utils.ListUtil;
import org.apache.avro.Conversion;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-10-15
 **/
public class AvroReflectData extends ReflectData {

    private final UseOriginalJudge useOriginalJudge;

    private final Converter<?> converter;

    public AvroReflectData(UseOriginalJudge useOriginalJudge, Converter<?> converter) {
        this.useOriginalJudge = useOriginalJudge;
        this.converter = converter;
    }

    @Override
    protected Schema createFieldSchema(Field field, Map<String, Schema> names) {
        Schema schema = super.createFieldSchema(field, names);
        if (field.getType().isPrimitive()) {
            // for primitive values, such as int, a null will result in a
            // NullPointerException at read time
            return schema;
        }
        return makeNullable(schema);
    }

    @Override
    public Schema getSchema(Type type) {
        Schema schema = super.getSchema(type);
        if (schema.getType() == Schema.Type.RECORD && type instanceof Class && !useOriginalJudge.useOriginalImpl((Class) type)) {
            Class clazz = (Class) type;
            Conversion conversionByClass = this.getConversionByClass(clazz);
            if (conversionByClass == null) {
                AvroGeneralConversion generalConversion = new AvroGeneralConversion(clazz, schema, converter, this);
                this.addLogicalTypeConversion(generalConversion);
                return generalConversion.getRecommendedSchema();
            }
            return conversionByClass.getRecommendedSchema();
        }
        return schema;
    }

    @Override
    protected Collection getArrayAsCollection(Object datum) {
        return (datum instanceof Map) ? ((Map) datum).entrySet() :
                (datum instanceof Collection) ? (Collection) datum : ListUtil.toList((Object[]) datum);
    }

    @Override
    public Schema induce(Object datum) {
        if (isEnum(datum)) {
            Class clazz = datum.getClass();
            Conversion conversionByClass = this.getConversionByClass(clazz);
            if (conversionByClass == null) {
                AvroEnumConversion generalConversion = new AvroEnumConversion(clazz, converter);
                this.addLogicalTypeConversion(generalConversion);
                return generalConversion.getRecommendedSchema();
            }
        }
        return super.induce(datum);
    }
}
