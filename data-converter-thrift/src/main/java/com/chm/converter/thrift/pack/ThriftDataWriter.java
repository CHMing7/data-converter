package com.chm.converter.thrift.pack;

import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.exception.CodecException;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.utils.IoUtil;
import com.chm.converter.thrift.ThriftClassInfoStorage;
import com.chm.converter.thrift.utils.Thrift;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TType;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

/**
 * @author caihongming
 * @date 2022-01-06
 **/
public class ThriftDataWriter implements DataWriter {

    protected final OutputStream outputStream;

    protected final TProtocol oprot;

    protected final DataCodecGenerate dataCodecGenerate;

    public ThriftDataWriter(OutputStream out, DataCodecGenerate generate) {
        this.outputStream = out;
        TTransport transport;
        try {
            transport = new TIOStreamTransport(out);
        } catch (TTransportException e) {
            throw new CodecException(e);
        }
        this.oprot = new TBinaryProtocol(transport);
        this.dataCodecGenerate = generate;
    }

    @Override
    public <T> T getOutputTarget() {
        return (T) outputStream;
    }

    @Override
    public DataWriter writeFieldBegin(int fieldNumber, FieldInfo fieldInfo) throws IOException {
        Codec codec = dataCodecGenerate.get(fieldInfo.getTypeToken());
        byte type = (byte) fieldInfo.getExpandProperty(ThriftClassInfoStorage.THRIFT_TYPE_KEY,
                () -> ThriftClassInfoStorage.getType(codec.getEncodeType().getRawType()));
        try {
            oprot.writeByte(type);
            oprot.writeI16((short) (fieldNumber + 1));
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public DataWriter writeFieldEnd(FieldInfo fieldInfo) throws IOException {
        return this;
    }

    @Override
    public DataWriter writeFieldEnd(int fieldNumber, FieldInfo fieldInfo) throws IOException {
        return this;
    }

    @Override
    public DataWriter writeFieldNull(int fieldNumber, FieldInfo fieldInfo) throws IOException {
        return this;
    }

    @Override
    public DataWriter writeNull() throws IOException {
        return this;
    }

    @Override
    public DataWriter writeBoolean(boolean value) throws IOException {
        try {
            oprot.writeBool(value);
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public DataWriter writeByte(byte value) throws IOException {
        try {
            oprot.writeByte(value);
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public DataWriter writeShort(short value) throws IOException {
        try {
            oprot.writeI16(value);
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public DataWriter writeInt(int value) throws IOException {
        try {
            oprot.writeI32(value);
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public DataWriter writeLong(long value) throws IOException {
        try {
            oprot.writeI64(value);
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public DataWriter writeBigInteger(BigInteger value) throws IOException {
        byte[] bytes = value.toByteArray();
        return writeByteArray(bytes);
    }

    @Override
    public DataWriter writeFloat(float value) throws IOException {
        try {
            oprot.writeDouble(value);
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public DataWriter writeDouble(double value) throws IOException {
        try {
            oprot.writeDouble(value);
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public DataWriter writeBigDecimal(BigDecimal value) throws IOException {
        String s = value.toString();
        return writeString(s);
    }

    @Override
    public DataWriter writeChar(char value) throws IOException {
        try {
            oprot.writeString(String.valueOf(value));
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public DataWriter writeString(String value) throws IOException {
        try {
            oprot.writeString(value);
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public DataWriter writeByteArray(byte[] value) throws IOException {
        try {
            oprot.writeBinary(ByteBuffer.wrap(value));
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public DataWriter writeByteArray(byte[] value, int offset, int len) throws IOException {
        try {
            oprot.writeBinary(ByteBuffer.wrap(value, offset, len));
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public DataWriter writeByteArray(InputStream value, int valueLength) throws IOException {
        byte[] bytes = IoUtil.readBytes(value, valueLength);
        return writeByteArray(bytes);
    }

    @Override
    public <T> DataWriter writeArray(T[] value) throws IOException {
        if (value == null) {
            writeNull();
            return this;
        }
        Class<? extends Object[]> valueClass = value.getClass();
        final byte type = ThriftClassInfoStorage.getType(valueClass.getComponentType());
        final int size = Array.getLength(value);
        try {
            oprot.writeByte(type);
            oprot.writeI32(size);
            for (int i = 0; i < size; i++) {
                writeAny(Array.get(value, i));
            }
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public <E> DataWriter writeCollection(Collection<E> value) throws IOException {
        if (value == null) {
            writeNull();
            return this;
        }

        Class<?> elementType = null;
        for (E e : value) {
            if (elementType == null) {
                elementType = e.getClass();
            } else if (!elementType.equals(e.getClass())) {
                elementType = Object.class;
                break;
            }
        }

        final byte type = ThriftClassInfoStorage.getType(elementType);
        final int size = value.size();
        try {
            oprot.writeByte(type);
            oprot.writeI32(size);
            for (E e : value) {
                writeAny(e);
            }
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public <K, V> DataWriter writeMap(Map<K, V> value) throws IOException {
        if (value == null) {
            writeNull();
            return this;
        }
        Class<?> keyClass = null;
        Class<?> valueClass = null;
        for (Map.Entry<K, V> entry : value.entrySet()) {
            if (keyClass == null) {
                keyClass = entry.getKey().getClass();
            } else if (!keyClass.equals(entry.getKey().getClass()) && keyClass != Object.class) {
                keyClass = Object.class;
            }
            if (valueClass == null) {
                valueClass = entry.getValue().getClass();
            } else if (!valueClass.equals(entry.getValue().getClass()) && valueClass != Object.class) {
                valueClass = Object.class;
            }
        }
        final byte keyType = ThriftClassInfoStorage.getType(keyClass);
        final byte valueType = ThriftClassInfoStorage.getType(valueClass);
        final int size = value.size();
        try {
            oprot.writeByte(keyType);
            oprot.writeByte(valueType);
            oprot.writeI32(size);
            for (Map.Entry<K, V> entry : value.entrySet()) {
                writeAny(entry.getKey());
                writeAny(entry.getValue());
            }
        } catch (TException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public <E extends Enum<E>> DataWriter writeEnum(Enum<E> value) throws IOException {
        Codec enumCodec = dataCodecGenerate.get(value.getDeclaringClass());
        enumCodec.write(value, this);
        return this;
    }

    @Override
    public <T> DataWriter writeClass(Class<T> value) throws IOException {
        Codec classCodec = dataCodecGenerate.get(Class.class);
        classCodec.write(value, this);
        return this;
    }

    @Override
    public <T> DataWriter writeBeanBegin(T value) throws IOException {
        return this;
    }

    @Override
    public DataWriter writeBean(Object value) throws IOException {
        if (value == null) {
            writeNull();
            return this;
        }
        if (Thrift.isSupported(value.getClass())) {
            try {
                TBase base = (TBase) value;
                base.write(oprot);
                return this;
            } catch (TException e) {
                throw new CodecException(e);
            }
        }
        Codec codec = dataCodecGenerate.get(value.getClass());
        codec.write(value, this);
        return this;
    }

    @Override
    public <T> DataWriter writeBeanEnd(T value) throws IOException {
        writeByte(TType.STOP);
        return this;
    }

    @Override
    public void close() throws IOException {
        outputStream.flush();
        outputStream.close();
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }
}
