package com.chm.converter.avro;

import com.chm.converter.avro.factorys.AvroGeneralConversion;
import com.chm.converter.avro.factorys.CoreCodecConversion;
import com.chm.converter.core.Converter;
import com.chm.converter.core.UseRawJudge;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codec.UniversalCodecAdapterCreator;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.core.utils.MapUtil;
import org.apache.avro.Conversion;
import org.apache.avro.JsonProperties;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @date 2021-10-15
 **/
public class AvroReflectData extends ReflectData {

    private final Converter<?> converter;

    private final UniversalGenerate<Codec> generate;

    private final UseRawJudge useRawJudge;

    public AvroReflectData(Converter<?> converter, UseRawJudge useRawJudge) {
        this.converter = converter;
        this.generate = DataCodecGenerate.getDataCodecGenerate(converter);
        this.useRawJudge = useRawJudge;
    }

    public AvroReflectData(Converter<?> converter, UniversalGenerate<Codec> generate, UseRawJudge useRawJudge) {
        this.converter = converter;
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);
        this.useRawJudge = useRawJudge;
    }

    @Override
    public Schema induce(Object datum) {
        Class<?> clazz = datum.getClass();
        TypeToken<?> typeToken = TypeToken.get(clazz);
        CoreCodecConversion<?> coreCodecConversion = getCodecSchema(typeToken, MapUtil.newHashMap(true));
        if (coreCodecConversion != null && coreCodecConversion.isPriorityUse()) {
            return coreCodecConversion.getRecommendedSchema();
        }
        return super.induce(datum);
    }

    @Override
    public Schema createSchema(Type type, Map<String, Schema> names) {
        TypeToken<?> typeToken = TypeToken.get(type);

        if (typeToken.getRawType() == Object.class) {
            return super.createSchema(Object.class, MapUtil.newHashMap());
        }

        String fullName = typeToken.toString();
        Schema cacheSchema = names.get(fullName);
        if (cacheSchema != null) {
            return cacheSchema;
        }

        Conversion<?> conversion = getConversionByType(type);
        if (conversion != null) {
            return conversion.getRecommendedSchema();
        }

        Schema rawSchema = super.createSchema(type, names);
        // 使用原始实现
        if (useRawJudge.useRawImpl(typeToken.getRawType())) {
            return rawSchema;
        }

        // 优先使用codec
        Conversion suitableSchema = getSuitableSchema(typeToken, names, rawSchema);
        if (suitableSchema != null) {
            return suitableSchema.getRecommendedSchema();
        }
        return rawSchema;
    }

    public Conversion<?> getConversionByType(Type type) {
        // type instanceof Class ? getConversionByClass((Class) type) :
        TypeToken<Object> typeToken = TypeToken.get(type);
        return getConversionByClass(typeToken.getRawType(), new LogicalType(typeToken.toString()));
    }

    private CoreCodecConversion<?> getCodecSchema(TypeToken<?> typeToken, Map<String, Schema> names) {
        return UniversalCodecAdapterCreator.create(this.generate, typeToken, (type, codec) -> {
            Schema encodeSchema = createSchema(codec.getEncodeType().getType(), names);
            CoreCodecConversion<?> coreCodecConversion = new CoreCodecConversion<>(this.converter, typeToken, codec, encodeSchema, typeToken.toString());
            this.addLogicalTypeConversion(coreCodecConversion);
            return coreCodecConversion;
        });
    }

    private Conversion<?> getSuitableSchema(TypeToken<?> typeToken, Map<String, Schema> names, Schema rawSchema) {
        return UniversalCodecAdapterCreator.createSuitable(this.generate, typeToken, (type, codec) -> {
            Schema encodeSchema = createSchema(codec.getEncodeType().getType(), names);
            CoreCodecConversion<?> coreCodecConversion = new CoreCodecConversion<>(this.converter, typeToken, codec, encodeSchema, typeToken.toString());
            this.addLogicalTypeConversion(coreCodecConversion);
            return coreCodecConversion;
        }, rawSchema.getType() == Schema.Type.RECORD, (type, codec) -> {
            AvroGeneralConversion<?> generalConversion = new AvroGeneralConversion<>(this.converter, typeToken, rawSchema, this);
            this.addLogicalTypeConversion(generalConversion);
            generalConversion.init();
            return generalConversion;
        });
    }

    @Override
    public Schema createFieldSchema(Field field, Map<String, Schema> names) {
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
        Conversion<?> conversionByClass = this.getConversionByType(type);
        if (conversionByClass != null) {
            return conversionByClass.getRecommendedSchema();
        }

        return super.getSchema(type);
    }

    @Override
    protected Collection getArrayAsCollection(Object datum) {
        return (datum instanceof Map) ? ((Map) datum).entrySet() :
                (datum instanceof Collection) ? (Collection) datum : ListUtil.toList((Object[]) datum);
    }

    @Override
    public int resolveUnion(Schema union, Object datum) {
        if (datum != null) {
            if (getConversionByClass(datum.getClass()) != null) {
                List<Schema> candidates = union.getTypes();
                for (int i = 0; i < candidates.size(); i += 1) {
                    LogicalType candidateType = candidates.get(i).getLogicalType();
                    if (candidateType != null) {
                        Conversion<?> conversion = getConversionByClass(datum.getClass(), candidateType);
                        if (conversion != null) {
                            return i;
                        }
                    }
                }
            }
        }
        if (datum == null || datum == JsonProperties.NULL_VALUE) {
            Integer i = union.getIndexNamed(Schema.Type.NULL.getName());
            if (i != null) {
                return i;
            }
        }
        Integer i = union.getIndexNamed(datum.getClass().getName());
        if (i != null) {
            return i;
        }
        return super.resolveUnion(union, datum);
    }
}
