/**
 * Autogenerated by Thrift Compiler (0.13.0)
 * <p>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package com.chm.converter.test.thrift;


@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2022-01-07")
public enum Enum1 implements org.apache.thrift.TEnum {
    code1(0);

    private final int value;

    private Enum1(int value) {
        this.value = value;
    }

    /**
     * Get the integer value of this enum value, as defined in the Thrift IDL.
     */
    public int getValue() {
        return value;
    }

    /**
     * Find a the enum type by its integer value, as defined in the Thrift IDL.
     *
     * @return null if the value is not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static Enum1 findByValue(int value) {
        switch (value) {
            case 0:
                return code1;
            default:
                return null;
        }
    }
}
