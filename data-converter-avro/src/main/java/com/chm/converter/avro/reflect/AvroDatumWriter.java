package com.chm.converter.avro.reflect;

import com.chm.converter.avro.factorys.CoreCodecConversion;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codecs.RuntimeTypeCodec;
import com.chm.converter.core.utils.ListUtil;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;

import java.io.IOException;
import java.util.List;

/**
 * @author CHMing
 * @date 2023-03-05
 **/
public class AvroDatumWriter<D> extends ReflectDatumWriter<D> {

    public AvroDatumWriter(Schema root, ReflectData reflectData) {
        super(root, reflectData);
    }

    @Override
    protected <T> Object convert(Schema schema, LogicalType logicalType, Conversion<T> conversion, Object datum) {
        if (conversion instanceof CoreCodecConversion &&
                ((CoreCodecConversion<T>) conversion).getCodec() instanceof RuntimeTypeCodec) {
            Codec runtimeCodec = ((RuntimeTypeCodec<?, ?>) ((CoreCodecConversion<T>) conversion).getCodec()).getRuntimeCodec(datum);
            return runtimeCodec.encode(datum);
        }
        return super.convert(schema, logicalType, conversion, datum);
    }

    @Override
    protected void writeWithoutConversion(Schema schema, Object datum, Encoder out) throws IOException {
        LogicalType logicalType = schema.getLogicalType();
        if (schema.getType() == Schema.Type.UNION) {
            List<Schema> schemaList = ListUtil.list(true);
            for (Schema type : schema.getTypes()) {
                LogicalType logical = type.getLogicalType();
                if (datum != null && logical != null) {
                    Conversion<?> conversion = getData().getConversionByClass(datum.getClass(), logical);
                    if(conversion == null){
                        // 泛型时 ConvertedType() 为 Object.class
                        conversion = getData().getConversionByClass(Object.class, logical);
                    }
                    if (conversion instanceof CoreCodecConversion &&
                            ((CoreCodecConversion<?>) conversion).getCodec() instanceof RuntimeTypeCodec) {
                        // 获取实时Schema
                        Conversion<?> runtimeConversion = getData().getConversionByClass(datum.getClass());
                        Schema runtimeSchema = runtimeConversion != null ? runtimeConversion.getRecommendedSchema() : getData().induce(datum);
                        schemaList.add(runtimeSchema);
                        continue;
                    }
                }
                schemaList.add(type);
            }
            super.writeWithoutConversion(Schema.createUnion(schemaList), datum, out);
            return;
        }
        if (datum != null && logicalType != null) {
            Conversion<?> conversion = getData().getConversionByClass(datum.getClass(), logicalType);
            if (conversion instanceof CoreCodecConversion &&
                    ((CoreCodecConversion<?>) conversion).getCodec() instanceof RuntimeTypeCodec) {
                // 获取实时Schema
                Conversion<?> runtimeConversion = getData().getConversionByClass(datum.getClass());
                Schema runtimeSchema = runtimeConversion != null ? runtimeConversion.getRecommendedSchema() : getData().induce(datum);
                super.writeWithoutConversion(runtimeSchema, datum, out);
                return;
            }
        }
        super.writeWithoutConversion(schema, datum, out);
    }
}
