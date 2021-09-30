package com.chm.converter.spearal.coders;

import com.chm.converter.codec.Codec;
import com.chm.converter.codec.DataCodec;
import org.spearal.SpearalContext;
import org.spearal.configuration.CoderProvider;
import org.spearal.configuration.ConverterProvider;
import org.spearal.impl.ExtendedSpearalEncoder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-29
 **/
public class CodecProvider implements CoderProvider, CoderProvider.Coder, ConverterProvider, ConverterProvider.Converter {

    public static final int ID_BOOLEAN = 1;
    public static final int ID_BYTE = 2;
    public static final int ID_SHORT = 3;
    public static final int ID_INT = 4;
    public static final int ID_LONG = 5;
    public static final int ID_BIGINTEGER = 6;
    public static final int ID_FLOAT = 7;
    public static final int ID_DOUBLE = 8;
    public static final int ID_BIGDECIMAL = 9;
    public static final int ID_CHAR = 10;
    public static final int ID_STRING = 11;
    public static final int ID_BYTE_ARRAY = 12;
    public static final int ID_ARRAY = 13;
    public static final int ID_COLLECTION = 14;
    public static final int ID_MAP = 15;
    public static final int ID_ENUM = 16;
    public static final int ID_CLASS = 17;
    public static final int ID_BEAN = 18;

    private DataCodec dataCodec;

    public CodecProvider(DataCodec dataCodec) {
        this.dataCodec = dataCodec;
    }

    public DataCodec getDataCodec() {
        return dataCodec;
    }

    public void setDataCodec(DataCodec dataCodec) {
        this.dataCodec = dataCodec;
    }

    @Override
    public Coder getCoder(Class<?> valueClass) {
        return dataCodec != null && dataCodec.containsByType(valueClass) ? this : null;
    }

    @Override
    public void encode(ExtendedSpearalEncoder encoder, Object value) throws IOException {
        Codec codec = dataCodec.getCodec(value.getClass());
        Object encode = codec.encode(value);
        if (encode == null) {
            encoder.writeNull();
            return;
        }
        Class<?> cls = encode.getClass();
        int classId = getClassId(cls);
        switch (classId) {
            case ID_BOOLEAN:
                encoder.writeBoolean((Boolean) encode);
                return;
            case ID_BYTE:
                encoder.writeByte((Byte) encode);
                return;
            case ID_SHORT:
                encoder.writeShort((Short) encode);
                return;
            case ID_INT:
                encoder.writeInt((Integer) encode);
                return;
            case ID_LONG:
                encoder.writeLong((Long) encode);
                return;
            case ID_BIGINTEGER:
                encoder.writeBigInteger((BigInteger) encode);
                return;
            case ID_FLOAT:
                encoder.writeFloat((Float) encode);
                return;
            case ID_DOUBLE:
                encoder.writeDouble((Double) encode);
                return;
            case ID_BIGDECIMAL:
                encoder.writeBigDecimal((BigDecimal) encode);
                return;
            case ID_CHAR:
                encoder.writeChar((Character) encode);
                return;
            case ID_STRING:
                encoder.writeString((String) encode);
                return;
            case ID_BYTE_ARRAY:
                encoder.writeByteArray((byte[]) encode);
                return;
            case ID_ARRAY:
                encoder.writeArray(encode);
                return;
            case ID_COLLECTION:
                encoder.writeCollection((Collection<?>) encode);
                return;
            case ID_MAP:
                encoder.writeMap((Map<?, ?>) encode);
                return;
            case ID_ENUM:
                encoder.writeEnum((Enum<?>) encode);
                return;
            case ID_CLASS:
                encoder.writeClass((Class<?>) encode);
                return;
            default:
                break;
        }
        // 转换前后类型相同则直接写入bean
        if (classId == ID_BEAN && cls == value.getClass()) {
            encoder.writeBean(encode);
        }
        encoder.writeAny(encode);
    }

    @Override
    public Converter<?> getConverter(Class<?> valueClass, Type targetType) {
        return dataCodec != null && dataCodec.containsByType(targetType) ? this : null;
    }

    @Override
    public Object convert(SpearalContext context, Object value, Type targetType) {
        Codec codec = dataCodec.getCodec(targetType);
        return codec.decode(value);
    }

    public static int getClassId(Class<?> cls) {
        if (cls == boolean.class || cls == Boolean.class) {
            return ID_BOOLEAN;
        }
        if (cls == byte.class || cls == Byte.class) {
            return ID_BYTE;
        }
        if (cls == short.class || cls == Short.class) {
            return ID_SHORT;
        }
        if (cls == int.class || cls == Integer.class) {
            return ID_INT;
        }
        if (cls == long.class || cls == Long.class) {
            return ID_LONG;
        }
        if (cls == BigInteger.class) {
            return ID_BIGINTEGER;
        }
        if (cls == float.class || cls == Float.class) {
            return ID_FLOAT;
        }
        if (cls == double.class || cls == Double.class) {
            return ID_DOUBLE;
        }
        if (cls == BigDecimal.class) {
            return ID_BIGDECIMAL;
        }
        if (cls == char.class || cls == Character.class) {
            return ID_CHAR;
        }
        if (cls == String.class) {
            return ID_STRING;
        }
        if (cls == byte[].class) {
            return ID_BYTE_ARRAY;
        }
        if (cls.isArray()) {
            return ID_ARRAY;
        }
        if (Collection.class.isAssignableFrom(cls)) {
            return ID_COLLECTION;
        }
        if (Map.class.isAssignableFrom(cls)) {
            return ID_MAP;
        }
        if (cls.isEnum()) {
            return ID_ENUM;
        }
        if (cls == Class.class) {
            return ID_CLASS;
        }
        return ID_BEAN;
    }
}
