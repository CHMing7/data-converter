package com.chm.converter.core.value;

/**
 * 值类型
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-12-04
 **/
public enum ValueType {

    /**
     * null型
     */
    NULL(false),

    /**
     * Boolean类型
     */
    BOOLEAN(false),

    /**
     * Integer类型
     */
    INTEGER(true),

    /**
     * Float类型
     */
    FLOAT(true),

    /**
     * 字符串类型
     */
    STRING(false),

    /**
     * Binary类型
     */
    BINARY(false),

    /**
     * 数组类型
     */
    ARRAY(false),

    /**
     * Map类型
     */
    MAP(false),

    /**
     * Collection 类型
     */
    COLLECTION(false),

    /**
     * 枚举类型
     */
    ENUM(false),

    /**
     * Class类型
     */
    CLASS(false);

    /**
     * 是否为数值
     */
    private final boolean numberType;


    ValueType(boolean numberType) {
        this.numberType = numberType;
    }

    public boolean isNullType() {
        return this == NULL;
    }

    public boolean isBooleanType() {
        return this == BOOLEAN;
    }

    public boolean isNumberType() {
        return numberType;
    }

    public boolean isIntegerType() {
        return this == INTEGER;
    }

    public boolean isFloatType() {
        return this == FLOAT;
    }

    public boolean isStringType() {
        return this == STRING;
    }

    public boolean isBinaryType() {
        return this == BINARY;
    }

    public boolean isArrayType() {
        return this == ARRAY;
    }

    public boolean isMapType() {
        return this == MAP;
    }

    public boolean isCollectionType() {
        return this == COLLECTION;
    }

    public boolean isEnumType() {
        return this == ENUM;
    }

    public boolean isClassType() {
        return this == CLASS;
    }
}
