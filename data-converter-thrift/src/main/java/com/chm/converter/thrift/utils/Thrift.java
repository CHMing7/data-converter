package com.chm.converter.thrift.utils;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.thrift.pack.ThriftDataReader;
import com.chm.converter.thrift.pack.ThriftDataWriter;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-15
 **/
public class Thrift {

    private final DataCodecGenerate dataCodec;

    public Thrift(Converter<?> converter) {
        this.dataCodec = DataCodecGenerate.newDefault(converter);
    }

    public static boolean isSupported(Type type) {
        if (type == null) {
            return false;
        }
        Class<?> cls = ClassUtil.getClassByType(type);
        if (TBase.class.isAssignableFrom(cls)) {
            return true;
        }
        return false;
    }

    public byte[] serialize(Object value) throws IOException {
        if (value == null) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ThriftDataWriter writer = new ThriftDataWriter(out, dataCodec);
        writer.writeAny(value);
        return out.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] source, Type targetType) throws TException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(source);
        ThriftDataReader reader = new ThriftDataReader(in, dataCodec);
        return (T) reader.readAny(TypeToken.get(targetType));
    }

}
