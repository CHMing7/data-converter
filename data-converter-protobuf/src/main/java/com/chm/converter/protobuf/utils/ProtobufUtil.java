package com.chm.converter.protobuf.utils;

import com.google.protobuf.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-15
 **/
public class ProtobufUtil {

    private final static ConcurrentMap<Class<? extends MessageLite>, MessageMarshaller> MARSHALLERS = new ConcurrentHashMap<>();

    private final static ExtensionRegistryLite GLOBAL_REGISTRY = ExtensionRegistryLite.getEmptyRegistry();

    static {
        // Built-in types need to be registered in advance
        marshaller(Empty.getDefaultInstance());
        marshaller(BoolValue.getDefaultInstance());
        marshaller(Int32Value.getDefaultInstance());
        marshaller(Int64Value.getDefaultInstance());
        marshaller(FloatValue.getDefaultInstance());
        marshaller(DoubleValue.getDefaultInstance());
        marshaller(BytesValue.getDefaultInstance());
        marshaller(StringValue.getDefaultInstance());
    }

    public static boolean isSupported(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        if (MessageLite.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }

    public static byte[] serialize(Object value) {
        if (value == null) {
            return new byte[0];
        }
        Class<?> cls = value.getClass();
        if (isSupported(cls) && !MARSHALLERS.containsKey(cls)) {
            marshaller((MessageLite) value);
        }

        if (!isSupported(cls)) {
            throw new IllegalArgumentException("This serialization only supports google protobuf objects, current object class is: " + cls.getName());
        }
        MessageLite messageLite = (MessageLite) value;
        return messageLite.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] source, Class<T> requestClass) throws InvalidProtocolBufferException {
        if (isSupported(requestClass) && !MARSHALLERS.containsKey(requestClass)) {
            try {
                Method method = requestClass.getDeclaredMethod("getDefaultInstance");
                MessageLite defaultInstance = (MessageLite) method.invoke(null);
                MARSHALLERS.put((Class<? extends MessageLite>) requestClass, new MessageMarshaller<>(defaultInstance));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            }
        }
        MessageMarshaller<?> marshaller = MARSHALLERS.get(requestClass);
        if (marshaller == null) {
            throw new IllegalStateException(String.format("Protobuf classes should be registered in advance before " +
                    "do serialization, class name: %s", requestClass.getName()));
        }
        return (T) marshaller.parse(source);
    }

    public static <T extends MessageLite> void marshaller(T defaultInstance) {
        MARSHALLERS.put(defaultInstance.getClass(), new MessageMarshaller<>(defaultInstance));
    }

    private static final class MessageMarshaller<T extends MessageLite> {
        private final Parser<T> parser;
        private final T defaultInstance;

        @SuppressWarnings("unchecked")
        MessageMarshaller(T defaultInstance) {
            this.defaultInstance = defaultInstance;
            parser = (Parser<T>) defaultInstance.getParserForType();
        }

        @SuppressWarnings("unchecked")
        public Class<T> getMessageClass() {
            // Precisely T since protobuf doesn't let messages extend other messages.
            return (Class<T>) defaultInstance.getClass();
        }

        public T getMessagePrototype() {
            return defaultInstance;
        }

        public T parse(byte[] source) throws InvalidProtocolBufferException {
            if (source == null) {
                return null;
            }
            return parser.parseFrom(source, GLOBAL_REGISTRY);
        }
    }
}
