package com.chm.converter.core.pack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

/**
 * 数据令牌
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-01-11
 **/
public interface DataToken {

    int NULL = -1;
    int FALSE = 0;
    int TRUE = 1;
    int BYTE = 2;
    int SHORT = 3;
    int INTEGER = 4;
    int LONG = 5;
    int BIG_INTEGER = 6;
    int FLOAT = 7;
    int DOUBLE = 8;
    int BIG_DECIMAL = 9;
    int CHAR = 10;
    int STRING = 11;
    int BYTE_ARRAY = 12;
    int ARRAY = 13;
    int COLLECTION = 14;
    int MAP = 15;
    int ENUM = 16;
    int CLASS = 17;
    int BEAN = 18;

    static int getToken(Class<?> cls) {
        if (cls == boolean.class || cls == Boolean.class) {
            return TRUE;
        }
        if (cls == byte.class || cls == Byte.class) {
            return BYTE;
        }
        if (cls == short.class || cls == Short.class) {
            return SHORT;
        }
        if (cls == int.class || cls == Integer.class) {
            return INTEGER;
        }
        if (cls == long.class || cls == Long.class) {
            return LONG;
        }
        if (cls == BigInteger.class) {
            return BIG_INTEGER;
        }
        if (cls == float.class || cls == Float.class) {
            return FLOAT;
        }
        if (cls == double.class || cls == Double.class) {
            return DOUBLE;
        }
        if (cls == BigDecimal.class) {
            return BIG_DECIMAL;
        }
        if (cls == char.class || cls == Character.class) {
            return CHAR;
        }
        if (cls == String.class) {
            return STRING;
        }
        if (cls == byte[].class) {
            return BYTE_ARRAY;
        }
        if (cls.isArray()) {
            return ARRAY;
        }
        if (Collection.class.isAssignableFrom(cls)) {
            return COLLECTION;
        }
        if (Map.class.isAssignableFrom(cls)) {
            return MAP;
        }
        if (cls.isEnum()) {
            return ENUM;
        }
        if (cls == Class.class) {
            return CLASS;
        }
        return BEAN;
    }
}
