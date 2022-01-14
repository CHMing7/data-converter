package com.chm.converter.thrift.pack;

import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.exception.CodecException;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.reflect.ConverterTypes;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.thrift.ThriftClassInfoStorage;
import com.chm.converter.thrift.utils.Thrift;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TType;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-12
 **/
public class ThriftDataReader implements DataReader {

    protected final InputStream inputStream;

    protected final TProtocol oprot;

    protected final DataCodecGenerate dataCodecGenerate;

    protected final Class<? extends Converter> converterClass;

    private byte currentType;

    public ThriftDataReader(InputStream in) {
        this(in, new DataCodecGenerate());
    }

    public ThriftDataReader(InputStream in, DataCodecGenerate generate) {
        this.inputStream = in;
        TTransport transport;
        try {
            transport = new TIOStreamTransport(in);
        } catch (TTransportException e) {
            throw new CodecException(e);
        }
        this.oprot = new TBinaryProtocol(transport);
        this.dataCodecGenerate = generate;
        Converter<?> converter = generate.getConverter();
        this.converterClass = converter != null ? converter.getClass() : null;
    }

    @Override
    public boolean hasNext() throws IOException {
        return true;
    }

    @Override
    public void skipAny() throws IOException {
        try {
            TProtocolUtil.skip(oprot, currentType);
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public FieldInfo readFieldBegin(Class<?> clz) throws IOException {
        JavaBeanInfo<?> javaBeanInfo = ThriftClassInfoStorage.INSTANCE.getJavaBeanInfo(clz, converterClass);
        return readFieldBegin(javaBeanInfo);
    }

    @Override
    public FieldInfo readFieldBegin(JavaBeanInfo<?> javaBeanInfo) throws IOException {
        try {
            List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
            TField field = oprot.readFieldBegin();
            currentType = field.type;
            if (field.type == TType.STOP) {
                return FieldInfo.STOP;
            }
            int id = field.id - 1;
            return (id >= 0 && id < sortedFieldList.size()) ? sortedFieldList.get(id) : null;
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public void readFieldEnd(Class<?> clz) throws IOException {
        JavaBeanInfo<?> javaBeanInfo = ThriftClassInfoStorage.INSTANCE.getJavaBeanInfo(clz, converterClass);
        readFieldEnd(javaBeanInfo);
    }

    @Override
    public void readFieldEnd(JavaBeanInfo<?> javaBeanInfo) throws IOException {

    }

    @Override
    public boolean readBoolean() throws IOException {
        try {
            return oprot.readBool();
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public byte readByte() throws IOException {
        try {
            return oprot.readByte();
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public short readShort() throws IOException {
        try {
            return oprot.readI16();
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public int readInt() throws IOException {
        try {
            return oprot.readI32();
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public long readLong() throws IOException {
        try {
            return oprot.readI64();
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public BigInteger readBigInteger() throws IOException {
        byte[] bytes = readByteArray();
        return new BigInteger(bytes);
    }

    @Override
    public float readFloat() throws IOException {
        try {
            return (float) oprot.readDouble();
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public double readDouble() throws IOException {
        try {
            return oprot.readDouble();
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public BigDecimal readBigDecimal() throws IOException {
        String s = readString();
        return new BigDecimal(s);
    }

    @Override
    public char readChar() throws IOException {
        String s = readString();
        return s.charAt(0);
    }

    @Override
    public String readString() throws IOException {
        try {
            return oprot.readString();
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public byte[] readByteArray() throws IOException {
        try {
            return oprot.readBinary().array();
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public void readByteArray(byte[] src) throws IOException {
        try {
            byte[] bytes = oprot.readBinary().array();
            System.arraycopy(bytes, 0, src, 0, Math.min(bytes.length, src.length));
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public byte[] readByteArray(int len) throws IOException {
        try {
            byte[] bytes = oprot.readBinary().array();
            return Arrays.copyOf(bytes, len);
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public void readByteArray(byte[] src, int off, int len) throws IOException {
        try {
            byte[] bytes = oprot.readBinary().array();
            System.arraycopy(bytes, 0, src, off, Math.min(bytes.length, len));
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public <T> T[] readArray() throws IOException {
        try {
            TList list = oprot.readListBegin();
        } catch (TException e) {
            throw new CodecException(e);
        }
        return null;
    }

    @Override
    public <T> T[] readArray(TypeToken<T[]> targetType) throws IOException {
        try {
            TList list = oprot.readListBegin();
            TypeToken elementType = TypeToken.get(ConverterTypes.getArrayComponentType(targetType.getType()));
            Object resultArray = Array.newInstance(elementType.getRawType(), list.size);
            for (int i = 0; i < list.size; i++) {
                Object value = readAny(list.elemType, elementType);
                Array.set(resultArray, i, value);
            }
            return (T[]) resultArray;
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public <E> Collection<E> readCollection(TypeToken<Collection<E>> targetType) throws IOException {
        try {
            TList list = oprot.readListBegin();
            Collection result = ConstructorFactory.INSTANCE.get(targetType).construct();
            TypeToken elementType = TypeToken.get(ConverterTypes.getCollectionElementType(targetType.getType(), targetType.getRawType()));
            for (int i = 0; i < list.size; i++) {
                Object value = readAny(list.elemType, elementType);
                result.add(value);
            }
            oprot.readListEnd();
            return result;
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public <K, V> Map<K, V> readMap(TypeToken<Map<K, V>> targetType) throws IOException {
        try {
            TMap map = oprot.readMapBegin();
            Map result = ConstructorFactory.INSTANCE.get(targetType).construct();
            Type[] keyAndValueTypes = ConverterTypes.getMapKeyAndValueTypes(targetType.getType(), targetType.getRawType());
            TypeToken keyType = TypeToken.get(keyAndValueTypes[0]);
            TypeToken valueType = TypeToken.get(keyAndValueTypes[1]);
            Object key;
            Object value;
            for (int i = 0; i < map.size; i++) {
                key = readAny(map.keyType, keyType);
                value = readAny(map.valueType, valueType);
                result.put(key, value);
            }
            oprot.readMapEnd();
            return result;
        } catch (TException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public <E extends Enum<E>> Enum<E> readEnum(TypeToken<Enum<E>> targetType) throws IOException {
        Codec enumCodec = dataCodecGenerate.get(targetType);
        return (Enum<E>) enumCodec.read(this);
    }

    @Override
    public <T> Class<T> readClass(TypeToken<Class<T>> targetType) throws IOException {
        Codec enumCodec = dataCodecGenerate.get(Class.class);
        return (Class<T>) enumCodec.read(this);
    }

    @Override
    public <T> T readBean(TypeToken<T> targetType) throws IOException {
        if (Thrift.isSupported(targetType.getType())) {
            TBase instance = (TBase) ConstructorFactory.INSTANCE.get(targetType).construct();
            try {
                instance.read(oprot);
                return (T) instance;
            } catch (TException e) {
                throw new CodecException(e);
            }
        }
        Codec beanCodec = dataCodecGenerate.get(targetType);
        return (T) beanCodec.read(this);
    }

    public Object readAny(byte type, TypeToken targetType) throws IOException {
        switch (type) {
            case TType.BOOL:
                return readBoolean();

            case TType.BYTE:
                return readByte();

            case TType.DOUBLE:
                return readDouble();

            case TType.I16:
                return readShort();

            case TType.I32:
                return readInt();

            case TType.I64:
                return readLong();

            case TType.STRING:
                return readString();

            case TType.MAP:
                return readMap(targetType);

            case TType.LIST:
            case TType.SET:
                return readCollection(targetType);

            case TType.ENUM:
                return readEnum(targetType);

            default:
                return readBean(targetType);
        }
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
