package com.chm.converter.protostuff.codec.factory;

import com.chm.converter.core.lang.Pair;
import com.chm.converter.core.reflect.ConverterTypes;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.protostuff.codec.ProtostuffCodec;
import com.chm.converter.protostuff.codec.RuntimeTypeCodec;
import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.ProtostuffException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * map类型编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-18
 **/
public class MapCodecFactory implements UniversalFactory<ProtostuffCodec> {

    /**
     * The field name of the Map.Entry.
     */
    public static final String FIELD_NAME_ENTRY = "e";

    /**
     * The field name of the key.
     */
    public static final String FIELD_NAME_KEY = "k";

    /**
     * The field name of the value;
     */
    public static final String FIELD_NAME_VALUE = "v";

    @Override
    public ProtostuffCodec create(UniversalGenerate<ProtostuffCodec> generate, TypeToken<?> typeToken) {
        Class<?> rawTypeOfSrc = typeToken.getRawType();
        if (!Map.class.isAssignableFrom(rawTypeOfSrc)) {
            return null;
        }
        Type type = typeToken.getType();
        Type[] keyAndValueTypes = ConverterTypes.getMapKeyAndValueTypes(type, rawTypeOfSrc);
        ProtostuffCodec kCodec = new RuntimeTypeCodec(generate, generate.get(keyAndValueTypes[0]),
                keyAndValueTypes[0]);
        ProtostuffCodec vCodec = new RuntimeTypeCodec(generate, generate.get(keyAndValueTypes[1]),
                keyAndValueTypes[1]);
        return new MapCodec(typeToken.getRawType(), kCodec, vCodec);
    }

    public static class MapCodec<K, V> extends ProtostuffCodec<Map<K, V>> {

        private final PairCodec<K, V> pairCodec;

        protected MapCodec(Class<Map<K, V>> clazz, ProtostuffCodec<K> kCodec, ProtostuffCodec<V> vCodec) {
            super(clazz);
            TypeToken<Pair<K, V>> pairType = new TypeToken<Pair<K, V>>() {
            };
            this.pairCodec = new PairCodec<>((Class<Pair<K, V>>) pairType.getRawType(), kCodec, vCodec);
        }

        @Override
        public String getFieldName(int number) {
            return number == 1 ? FIELD_NAME_ENTRY : null;
        }

        @Override
        public int getFieldNumber(String name) {
            return name.length() == 1 && name.charAt(0) == 'e' ? 1 : 0;
        }

        @Override
        public void writeTo(Output output, Map<K, V> message) throws IOException {
            for (Map.Entry<K, V> entry : message.entrySet()) {
                // allow null keys and values.
                Pair<K, V> pair = Pair.of(entry.getKey(), entry.getValue());
                output.writeObject(1, pair, pairCodec, true);
            }
        }

        @Override
        public Map<K, V> mergeFrom(Input input) throws IOException {
            Map<K, V> map = constructor.construct();
            for (int number = input.readFieldNumber(this); ; number = input.readFieldNumber(this)) {
                switch (number) {
                    case 0:
                        return map;
                    case 1:
                        Pair<K, V> pair = input.mergeObject(null, pairCodec);
                        map.put(pair.getKey(), pair.getValue());
                        break;
                    default:
                        throw new ProtostuffException("The map was incorrectly serialized.");
                }
            }
        }
    }

    public static class PairCodec<K, V> extends ProtostuffCodec<Pair<K, V>> {

        private final ProtostuffCodec<K> kCodec;

        private final ProtostuffCodec<V> vCodec;

        protected PairCodec(Class<Pair<K, V>> clazz, ProtostuffCodec<K> kCodec, ProtostuffCodec<V> vCodec) {
            super(clazz);
            this.kCodec = kCodec;
            this.vCodec = vCodec;
        }

        @Override
        public final String getFieldName(int number) {
            switch (number) {
                case 1:
                    return FIELD_NAME_KEY;
                case 2:
                    return FIELD_NAME_VALUE;
                default:
                    return null;
            }
        }

        @Override
        public final int getFieldNumber(String name) {
            if (name.length() != 1) {
                return 0;
            }

            switch (name.charAt(0)) {
                case 'k':
                    return 1;
                case 'v':
                    return 2;
                default:
                    return 0;
            }
        }

        @Override
        public boolean isInitialized(Pair<K, V> message) {
            return true;
        }

        @Override
        public Pair<K, V> newMessage() {
            return new Pair<>(null, null);
        }

        @Override
        public void writeTo(Output output, Pair<K, V> message) throws IOException {
            if (message.getKey() != null) {
                output.writeObject(1, message.getKey(), kCodec, false);
            }
            if (message.getValue() != null) {
                output.writeObject(2, message.getValue(), vCodec, false);
            }
        }

        @Override
        public Pair<K, V> mergeFrom(Input input) throws IOException {
            K key = null;
            V value = null;

            for (int number = input.readFieldNumber(this); ; number = input.readFieldNumber(this)) {
                switch (number) {
                    case 0:
                        return Pair.of(key, value);
                    case 1:
                        if (key != null) {
                            throw new ProtostuffException("The map was incorrectly " +
                                    "serialized.");
                        }
                        key = input.mergeObject(null, kCodec);
                        assert key != null;
                        break;
                    case 2:
                        if (value != null) {
                            throw new ProtostuffException("The map was incorrectly " +
                                    "serialized.");
                        }
                        value = input.mergeObject(null, vCodec);
                        assert value != null;
                        break;
                    default:
                        throw new ProtostuffException("The map was incorrectly " +
                                "serialized.");
                }
            }
        }
    }
}
