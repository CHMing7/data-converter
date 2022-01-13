package com.chm.converter.protostuff.codec;

import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.utils.MapUtil;
import io.protostuff.ByteString;
import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.ProtostuffException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-11
 **/
public class ProtostuffCodecs {

    // protostuff format
    int ID_OBJECT = 16, ID_ARRAY_MAPPED = 17, ID_CLASS = 18,
            ID_CLASS_MAPPED = 19, ID_CLASS_ARRAY = 20,
            ID_CLASS_ARRAY_MAPPED = 21,

    ID_ENUM_SET = 22, ID_ENUM_MAP = 23, ID_ENUM = 24,
            ID_COLLECTION = 25, ID_MAP = 26,

    ID_POLYMORPHIC_COLLECTION = 28, ID_POLYMORPHIC_MAP = 29,
            ID_DELEGATE = 30,

    ID_ARRAY_DELEGATE = 32, ID_ARRAY_SCALAR = 33, ID_ARRAY_ENUM = 34,
            ID_ARRAY_POJO = 35,

    ID_THROWABLE = 52,

    // pojo fields limited to 126 if not explicitly using @Tag
    // annotations
    ID_POJO = 127;

    public static final int ID_SCHEMAS_ARRAY_LEN = 1, ID_SCHEMAS_ARRAY_DATA = 2, ID_SCHEMAS_ARRAY_NULLCOUNT = 3;

    public static final int ID_ARRAY_LEN = 3;
    public static final int ID_ARRAY_DIMENSION = 2;
    public static final int ID_ARRAY_VALUE = 1;

    public static final int ID_MAP_ENTRY = 1;
    public static final int ID_MAP_KEY = 1;
    public static final int ID_MAP_VALUE = 2;

    public static final int ID_ENUM_VALUE = 1;

    public static final String STR_BOOL = "a", STR_BYTE = "b", STR_CHAR = "c",
            STR_SHORT = "d", STR_INT32 = "e", STR_INT64 = "f", STR_FLOAT = "g",
            STR_DOUBLE = "h", STR_STRING = "i", STR_BYTES = "j",
            STR_BYTE_ARRAY = "k", STR_BIGDECIMAL = "l", STR_BIGINTEGER = "m",
            STR_DATE = "n", STR_ARRAY = "o", STR_OBJECT = "p",
            STR_ARRAY_MAPPED = "q", STR_CLASS = "r", STR_CLASS_MAPPED = "s",
            STR_CLASS_ARRAY = "t", STR_CLASS_ARRAY_MAPPED = "u",

    STR_ENUM_SET = "v", STR_ENUM_MAP = "w", STR_ENUM = "x",
            STR_COLLECTION = "y", STR_MAP = "z",

    STR_POLYMORPHIC_COLLECTION = "B", STR_POLYMOPRHIC_MAP = "C",
            STR_DELEGATE = "D",

    STR_ARRAY_DELEGATE = "F", STR_ARRAY_SCALAR = "G",
            STR_ARRAY_ENUM = "H", STR_ARRAY_POJO = "I",

    STR_THROWABLE = "Z",

    // pojo fields limited to 126 if not explicitly using @Tag
    // annotations
    STR_POJO = "_";

    private static final Map<Integer, FieldWriteTo<?>> WRITE_MAP = MapUtil.newHashMap();

    private static final Map<Integer, FieldMergeFrom<?>> MERGE_FROM_MAP = MapUtil.newHashMap();

    static {
        // writeTo
        WRITE_MAP.put(ProtostuffConstants.ID_BOOL, (FieldWriteTo<Boolean>) (fieldNumber, output, message) -> output.writeBool(fieldNumber, message, false));
        WRITE_MAP.put(ProtostuffConstants.ID_BYTE, (FieldWriteTo<Byte>) (fieldNumber, output, message) -> output.writeUInt32(fieldNumber, message, false));
        WRITE_MAP.put(ProtostuffConstants.ID_CHAR, (FieldWriteTo<Character>) (fieldNumber, output, message) -> output.writeUInt32(fieldNumber, message, false));
        WRITE_MAP.put(ProtostuffConstants.ID_SHORT, (FieldWriteTo<Short>) (fieldNumber, output, message) -> output.writeUInt32(fieldNumber, message, false));
        WRITE_MAP.put(ProtostuffConstants.ID_INT, (FieldWriteTo<Integer>) (fieldNumber, output, message) -> output.writeInt32(fieldNumber, message, false));
        WRITE_MAP.put(ProtostuffConstants.ID_LONG, (FieldWriteTo<Long>) (fieldNumber, output, message) -> output.writeInt64(fieldNumber, message, false));
        WRITE_MAP.put(ProtostuffConstants.ID_FLOAT, (FieldWriteTo<Float>) (fieldNumber, output, message) -> output.writeFloat(fieldNumber, message, false));
        WRITE_MAP.put(ProtostuffConstants.ID_DOUBLE, (FieldWriteTo<Double>) (fieldNumber, output, message) -> output.writeDouble(fieldNumber, message, false));
        WRITE_MAP.put(ProtostuffConstants.ID_STRING, (FieldWriteTo<String>) (fieldNumber, output, message) -> output.writeString(fieldNumber, message, false));
        WRITE_MAP.put(ProtostuffConstants.ID_BYTES, (FieldWriteTo<ByteString>) (fieldNumber, output, message) -> output.writeBytes(fieldNumber, message, false));
        WRITE_MAP.put(ProtostuffConstants.ID_BYTE_ARRAY, (FieldWriteTo<byte[]>) (fieldNumber, output, message) -> output.writeByteArray(fieldNumber, message, false));
        WRITE_MAP.put(ProtostuffConstants.ID_BIG_DECIMAL, (FieldWriteTo<BigDecimal>) (fieldNumber, output, message) -> output.writeString(fieldNumber, message.toString(), false));
        WRITE_MAP.put(ProtostuffConstants.ID_BIG_INTEGER, (FieldWriteTo<BigInteger>) (fieldNumber, output, message) -> output.writeByteArray(fieldNumber, message.toByteArray(), false));

        //mergeFrom
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_BOOL, Input::readBool);
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_BYTE, Input::readUInt32);
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_CHAR, Input::readUInt32);
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_SHORT, Input::readUInt32);
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_INT, Input::readInt32);
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_LONG, Input::readInt64);
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_FLOAT, Input::readFloat);
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_DOUBLE, Input::readDouble);
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_STRING, Input::readString);
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_BYTES, Input::readBytes);
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_BYTE_ARRAY, Input::readByteArray);
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_BIG_DECIMAL, input -> new BigDecimal(input.readString()));
        MERGE_FROM_MAP.put(ProtostuffConstants.ID_BIG_INTEGER, input -> new BigInteger(input.readByteArray()));
    }

    public static abstract class Base<T> extends BaseProtostuffCodec<T> {

        public Base(Class<T> clazz) {
            super(clazz, clazz.getSimpleName());
        }

        @Override
        public void writeTo(Output output, T message) throws IOException {
            if (message != null) {
                FieldWriteTo writeTo = WRITE_MAP.get(classId());
                if (writeTo != null) {
                    writeTo.apply(classId(), output, message);
                }
            }
        }

        @Override
        public T mergeFrom(Input input) throws IOException {
            if (classId() != input.readFieldNumber(this)) {
                throw new ProtostuffException("Corrupt input.");
            }
            FieldMergeFrom mergeFrom = MERGE_FROM_MAP.get(classId());

            T t = mergeFrom != null ? (T) mergeFrom.apply(input) : null;

            if (0 != input.readFieldNumber(this)) {
                throw new ProtostuffException("Corrupt input.");
            }
            return t;
        }
    }

    @FunctionalInterface
    interface FieldWriteTo<T> {

        void apply(int fieldNumber, Output output, T message) throws IOException;
    }

    @FunctionalInterface
    interface FieldMergeFrom<T> {

        T apply(Input input) throws IOException;
    }


    public static final class CharCodec extends Base<Character> {

        public CharCodec() {
            super(Character.class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_CHAR;
        }

        @Override
        public Character newMessage() {
            return 0;
        }
    }

    private static final CharCodec CHAR = new CharCodec();

    public static final UniversalFactory<ProtostuffCodec> CHAR_FACTORY = UniversalFactory.newFactory(char.class, Character.class, CHAR);


    public static final class ShortCodec extends Base<Short> {

        public ShortCodec() {
            super(Short.class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_SHORT;
        }

        @Override
        public Short newMessage() {
            return 0;
        }
    }

    private static final ShortCodec SHORT = new ShortCodec();

    public static final UniversalFactory<ProtostuffCodec> SHORT_FACTORY = UniversalFactory.newFactory(short.class, Short.class, SHORT);


    public static final class ByteCodec extends Base<Byte> {

        public ByteCodec() {
            super(Byte.class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_BYTE;
        }

        @Override
        public Byte newMessage() {
            return 0;
        }
    }

    private static final ByteCodec BYTE = new ByteCodec();

    public static final UniversalFactory<ProtostuffCodec> BYTE_FACTORY = UniversalFactory.newFactory(byte.class, Byte.class, BYTE);


    public static final class IntCodec extends Base<Integer> {

        public IntCodec() {
            super(Integer.class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_INT;
        }

        @Override
        public Integer newMessage() {
            return 0;
        }
    }

    private static final IntCodec INT = new IntCodec();

    public static final UniversalFactory<ProtostuffCodec> INT_FACTORY = UniversalFactory.newFactory(int.class, Integer.class, INT);


    public static final class LongCodec extends Base<Long> {

        public LongCodec() {
            super(Long.class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_LONG;
        }

        @Override
        public Long newMessage() {
            return 0L;
        }
    }

    private static final LongCodec LONG = new LongCodec();

    public static final UniversalFactory<ProtostuffCodec> LONG_FACTORY = UniversalFactory.newFactory(long.class, Long.class, LONG);


    public static final class FloatCodec extends Base<Float> {

        public FloatCodec() {
            super(Float.class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_FLOAT;
        }

        @Override
        public Float newMessage() {
            return 0F;
        }
    }

    private static final FloatCodec FLOAT = new FloatCodec();

    public static final UniversalFactory<ProtostuffCodec> FLOAT_FACTORY = UniversalFactory.newFactory(float.class, Float.class, FLOAT);


    public static final class DoubleCodec extends Base<Double> {

        public DoubleCodec() {
            super(Double.class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_DOUBLE;
        }

        @Override
        public Double newMessage() {
            return .0;
        }
    }

    private static final DoubleCodec DOUBLE = new DoubleCodec();

    public static final UniversalFactory<ProtostuffCodec> DOUBLE_FACTORY = UniversalFactory.newFactory(double.class, Double.class, DOUBLE);


    public static final class BoolCodec extends Base<Boolean> {

        public BoolCodec() {
            super(Boolean.class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_BOOL;
        }

        @Override
        public Boolean newMessage() {
            return false;
        }
    }

    private static final BoolCodec BOOL = new BoolCodec();

    public static final UniversalFactory<ProtostuffCodec> BOOL_FACTORY = UniversalFactory.newFactory(boolean.class, Boolean.class, BOOL);


    public static final class StringCodec extends Base<String> {

        public StringCodec() {
            super(String.class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_STRING;
        }

        @Override
        public String newMessage() {
            return "";
        }
    }

    private static final StringCodec STRING = new StringCodec();

    public static final UniversalFactory<ProtostuffCodec> STRING_FACTORY = UniversalFactory.newFactory(String.class, STRING);


    public static final class ByteStringCodec extends Base<ByteString> {

        public ByteStringCodec() {
            super(ByteString.class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_BYTES;
        }

        @Override
        public ByteString newMessage() {
            return ByteString.EMPTY;
        }
    }

    private static final ByteStringCodec BYTE_STRING = new ByteStringCodec();

    public static final UniversalFactory<ProtostuffCodec> BYTE_STRING_FACTORY = UniversalFactory.newFactory(ByteString.class, BYTE_STRING);


    public static final class BytesCodec extends Base<byte[]> {

        public BytesCodec() {
            super(byte[].class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_BYTE_ARRAY;
        }

        @Override
        public byte[] newMessage() {
            return new byte[0];
        }
    }

    private static final BytesCodec BYTES = new BytesCodec();

    public static final UniversalFactory<ProtostuffCodec> BYTES_FACTORY = UniversalFactory.newFactory(byte[].class, BYTES);


    public static final class BigDecimalCodec extends Base<BigDecimal> {

        public BigDecimalCodec() {
            super(BigDecimal.class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_BIG_DECIMAL;
        }

        @Override
        public BigDecimal newMessage() {
            return BigDecimal.valueOf(0);
        }
    }

    private static final BigDecimalCodec BIG_DECIMAL = new BigDecimalCodec();

    public static final UniversalFactory<ProtostuffCodec> BIG_DECIMAL_FACTORY = UniversalFactory.newFactory(BigDecimal.class, BIG_DECIMAL);


    public static final class BigIntegerCodec extends Base<BigInteger> {

        public BigIntegerCodec() {
            super(BigInteger.class);
        }

        @Override
        public int classId() {
            return ProtostuffConstants.ID_BIG_INTEGER;
        }

        @Override
        public BigInteger newMessage() {
            return BigInteger.valueOf(0);
        }
    }

    private static final BigIntegerCodec BIG_INTEGER = new BigIntegerCodec();

    public static final UniversalFactory<ProtostuffCodec> BIG_INTEGER_FACTORY = UniversalFactory.newFactory(BigInteger.class, BIG_INTEGER);

}
