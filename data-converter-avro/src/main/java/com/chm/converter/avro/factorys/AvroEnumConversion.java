package com.chm.converter.avro.factorys;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.chm.converter.core.codecs.EnumCodec;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-14
 **/
public class AvroEnumConversion<E extends Enum<E>> extends Conversion<E> {

    private final Class<E> classOfT;

    private final EnumCodec<E> enumCodec;

    private final LogicalType logicalType;

    public AvroEnumConversion(Class<E> classOfT, Class<? extends Converter> converterClass) {
        this.classOfT = classOfT;
        JavaBeanInfo<E> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(classOfT, converterClass);
        Map<String, String> aliasMap = javaBeanInfo.getFieldNameAliasMap();
        this.enumCodec = new EnumCodec<>(classOfT, aliasMap);
        this.logicalType = new LogicalType(classOfT.getName());
    }

    @Override
    public Class<E> getConvertedType() {
        return classOfT;
    }

    @Override
    public String getLogicalTypeName() {
        return logicalType.getName();
    }


    @Override
    public E fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
        return enumCodec.decode(value.toString());
    }

    @Override
    public CharSequence toCharSequence(E value, Schema schema, LogicalType type) {
        return enumCodec.encode(value);
    }

    @Override
    public Schema getRecommendedSchema() {
        return logicalType.addToSchema(Schema.create(Schema.Type.STRING));
    }
}