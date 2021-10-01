package com.chm.converter.thrift.utils;

import cn.hutool.core.map.MapUtil;
import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.reflect.TypeToken;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-15
 **/
public class ThriftUtil {

    private final static ConcurrentMap<Class<? extends TBase>, BaseMarshaller> MARSHALLERS = new ConcurrentHashMap<>();

    private final static ConstructorFactory CONSTRUCTOR_FACTORY = new ConstructorFactory(MapUtil.empty());

    public static boolean isSupported(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        if (TBase.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }

    public static byte[] serialize(Object value) throws TException {
        if (value == null) {
            return new byte[0];
        }
        Class<?> cls = value.getClass();
        if (!isSupported(cls)) {
            throw new IllegalArgumentException("This serialization only supports thrift objects, current object class is: " + cls.getName());
        }
        TBase base = (TBase) value;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TTransport transport = new TIOStreamTransport(out);
        TBinaryProtocol tp = new TBinaryProtocol(transport);
        base.write(tp);
        return out.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] source, Class<T> requestClass) throws TException {
        if (isSupported(requestClass) && !MARSHALLERS.containsKey(requestClass)) {
            MARSHALLERS.put((Class<? extends TBase>) requestClass, new BaseMarshaller(requestClass));
        }
        BaseMarshaller<?> marshaller = MARSHALLERS.get(requestClass);
        if (marshaller == null) {
            throw new IllegalStateException(String.format("thrift classes should be registered in advance before " +
                    "do serialization, class name: %s", requestClass.getName()));
        }
        return (T) marshaller.parse(source);
    }

    public static <T extends TBase> void marshaller(Class<T> requestClass) {
        MARSHALLERS.put(requestClass, new BaseMarshaller(requestClass));
    }

    private static final class BaseMarshaller<T extends TBase> {

        private Class<T> requestClass;

        @SuppressWarnings("unchecked")
        BaseMarshaller(Class<T> requestClass) {
            this.requestClass = requestClass;
        }

        @SuppressWarnings("unchecked")
        public Class<T> getMessageClass() {
            // Precisely T since protobuf doesn't let messages extend other messages.
            return requestClass;
        }


        public T parse(byte[] source) throws TException {
            if (source == null) {
                return null;
            }
            T instance = CONSTRUCTOR_FACTORY.get(TypeToken.get(requestClass)).construct();
            instance.read(new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(source))));
            return instance;
        }
    }
}
