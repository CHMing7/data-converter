package com.chm.converter.protostuff.codec;

/**
 * 常用常量
 *
 * @author caihongming
 * @version v1.0
 * @date 2021-11-29
 **/
public interface ProtostuffConstants {

    /**
     * 1-15 is encoded as 1 byte on protobuf
     */
    int ID_BOOL = 1;

    int ID_BYTE = 2;
    int ID_CHAR = 3;
    int ID_SHORT = 4;
    int ID_INT = 5;
    int ID_LONG = 6;
    int ID_FLOAT = 7;
    int ID_DOUBLE = 8;
    int ID_STRING = 9;
    int ID_BYTES = 10;
    int ID_BYTE_ARRAY = 11;
    int ID_BIG_DECIMAL = 12;
    int ID_BIG_INTEGER = 13;
    int ID_DATE = 14;
    int ID_ARRAY = 15;

    int ID_ENUM = 24;

    int ID_JAVA8_TIME = 53;
    int ID_DEFAULT_DATE = 54;
    int ID_BEAN = 55;
}
