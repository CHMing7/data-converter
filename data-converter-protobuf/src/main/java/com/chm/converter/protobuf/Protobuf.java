package com.chm.converter.protobuf;

import com.chm.converter.core.Converter;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.protostuff.codec.ByteArrayInput;
import com.chm.converter.protostuff.codec.ProtostuffCodec;
import com.chm.converter.protostuff.codec.ProtostuffCodecGenerate;
import com.google.protobuf.BoolValue;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.Empty;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.google.protobuf.StringValue;
import io.protostuff.Input;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufOutput;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-18
 **/
public class Protobuf {

    private final ConcurrentMap<Type, MessageMarshaller> marshallers = new ConcurrentHashMap<>();

    private final ExtensionRegistryLite globalRegistry = ExtensionRegistryLite.getEmptyRegistry();

    {
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

    private final ProtostuffCodecGenerate protostuffCodecGenerate;

    public Protobuf(Converter<?> converter) {
        this.protostuffCodecGenerate = ProtostuffCodecGenerate.newDefault(converter);
    }


    public static boolean isSupported(Type type) {
        if (type == null) {
            return false;
        }
        Class<?> cls = ClassUtil.getClassByType(type);
        if (MessageLite.class.isAssignableFrom(cls)) {
            return true;
        }
        return false;
    }

    public byte[] serialize(Object value) throws IOException {
        if (value == null) {
            return new byte[0];
        }
        Class<?> cls = value.getClass();

        if (isSupported(cls)) {
            MessageLite messageLite = (MessageLite) value;
            return messageLite.toByteArray();
        }
        ProtostuffCodec protostuffCodec = protostuffCodecGenerate.get(cls);
        ProtobufOutput output = new ProtobufOutput(LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        protostuffCodec.writeTo(output, value);
        return output.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] source, Type targetType) throws IOException {
        if (isSupported(targetType) && !marshallers.containsKey(targetType)) {
            try {
                Class<?> cls = ClassUtil.getClassByType(targetType);
                Method method = cls.getDeclaredMethod("getDefaultInstance");
                MessageLite defaultInstance = (MessageLite) method.invoke(null);
                marshallers.put(targetType, new MessageMarshaller<>(defaultInstance));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            }
        }
        MessageMarshaller<?> marshaller = marshallers.get(targetType);
        if (marshaller != null) {
            return (T) marshaller.parse(source);
        }
        ProtostuffCodec<T> protostuffCodec = protostuffCodecGenerate.get(targetType);
        Input input = new ByteArrayInput(new io.protostuff.ByteArrayInput(source, false));
        return protostuffCodec.mergeFrom(input);
    }

    public <T extends MessageLite> void marshaller(T defaultInstance) {
        marshallers.put(defaultInstance.getClass(), new MessageMarshaller<>(defaultInstance));
    }

    private final class MessageMarshaller<T extends MessageLite> {
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
            return parser.parseFrom(source, globalRegistry);
        }
    }

}
