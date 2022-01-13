package com.chm.converter.thrift;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.core.utils.MapUtil;
import org.apache.thrift.protocol.TType;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-07
 **/
public class ThriftClassInfoStorage implements ClassInfoStorage {

    public static final String THRIFT_TYPE_KEY = "thriftType";

    public static final Map<Class<?>, Map<Class<? extends Converter>, Boolean>> INIT_TABLE = MapUtil.newHashMap();

    public static final ClassInfoStorage INSTANCE = new ThriftClassInfoStorage();

    @Override
    public <T> void initClassInfo(Class<T> clazz, Class<? extends Converter> converterClass) {
        ClassInfoStorage.super.initClassInfo(clazz, converterClass);
        JavaBeanInfo<T> javaBeanInfo = ClassInfoStorage.get(BEAN_INFO_MAP, clazz, converterClass);
        byte thriftType = getType(javaBeanInfo.getClazz());
        javaBeanInfo.putExpandProperty("thriftType", thriftType);
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        for (FieldInfo fieldInfo : sortedFieldList) {
            byte fieldThriftType = getType(fieldInfo.getFieldClass());
            fieldInfo.putExpandProperty("thriftType", fieldThriftType);
        }
        ClassInfoStorage.put(INIT_TABLE, clazz, converterClass, true);
    }

    @Override
    public boolean isInit(Class<?> clazz, Class<? extends Converter> converterClass) {
        return Boolean.TRUE.equals(ClassInfoStorage.get(INIT_TABLE, clazz, converterClass));
    }

    public static byte getType(Class<?> clz) {
        if (ClassUtil.isBoolean(clz)) {
            return TType.BOOL;
        }
        if (clz == byte.class || clz == Byte.class) {
            return TType.BYTE;
        }
        if (clz == float.class || clz == Float.class
                || clz == double.class || clz == Double.class) {
            return TType.DOUBLE;
        }
        if (clz == short.class || clz == Short.class) {
            return TType.I16;
        }
        if (clz == int.class || clz == Integer.class) {
            return TType.I32;
        }
        if (clz == long.class || clz == Long.class) {
            return TType.I64;
        }
        if (CharSequence.class.isAssignableFrom(clz)) {
            return TType.STRING;
        }
        if (clz == byte[].class || clz == Byte[].class || ByteBuffer.class.isAssignableFrom(clz)) {
            return TType.STRING;
        }
        if (Map.class.isAssignableFrom(clz)) {
            return TType.MAP;
        }
        if (clz.isArray()) {
            return TType.LIST;
        }
        if (clz.isEnum()) {
            return TType.ENUM;
        }
        if (Set.class.isAssignableFrom(clz)) {
            return TType.SET;
        }
        if (List.class.isAssignableFrom(clz)) {
            return TType.LIST;
        }
        return TType.STRUCT;
    }

    public static Class<?> getType(byte b) {
        switch (b) {
            case TType.BOOL:
                return boolean.class;

            case TType.BYTE:
                return byte.class;

            case TType.DOUBLE:
                return double.class;

            case TType.I16:
                return short.class;

            case TType.I32:
                return int.class;

            case TType.I64:
                return long.class;

            case TType.STRING:
                return CharSequence.class;

            case TType.MAP:
                return Map.class;

            case TType.LIST:
                return List.class;

            case TType.SET:
                return Set.class;

            case TType.ENUM:
                return Enum.class;

            default:
                return Object.class;
        }
    }
}
