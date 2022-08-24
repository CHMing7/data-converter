package com.chm.converter.kryo.factory;

import com.chm.converter.core.Converter;
import com.chm.converter.kryo.CompatibleKryo;
import com.chm.converter.kryo.KryoSerializerRegister;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * kryo工厂
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-26
 **/
public abstract class AbstractKryoFactory implements KryoFactory {

    private final Converter<?> converter;

    private final Set<Class> registrations = new LinkedHashSet<>();

    private final Map<Class, Serializer> classSerializerMap = new HashMap<>();

    private final Set<KryoSerializerRegister> kryoSerializerRegisters = new LinkedHashSet<>();

    private boolean registrationRequired;

    public AbstractKryoFactory(Converter<?> converter) {
        this.converter = converter;
    }

    /**
     * only supposed to be called at startup time
     * <p>
     * later may consider adding support for custom serializer, custom id, etc
     */
    public void registerClass(Class clazz) {
        registrations.add(clazz);
    }

    /**
     * only supposed to be called at startup time
     * <p>
     * later may consider adding support for custom serializer, custom id, etc
     */
    public void registerClass(Class clazz, Serializer serializer) {
        classSerializerMap.put(clazz, serializer);
    }

    public void register(KryoSerializerRegister kryoSerializerRegister) {
        kryoSerializerRegisters.add(kryoSerializerRegister);
    }

    @Override
    public Kryo create() {
        Kryo kryo = new CompatibleKryo(converter);

        kryo.setReferences(true);
        kryo.setRegistrationRequired(registrationRequired);

        kryo.addDefaultSerializer(Throwable.class, new JavaSerializer());

        for (Class clazz : registrations) {
            kryo.register(clazz);
        }

        for (Map.Entry<Class, Serializer> classSerializerEntry : classSerializerMap.entrySet()) {
            kryo.register(classSerializerEntry.getKey(), classSerializerEntry.getValue());
        }

        for (KryoSerializerRegister kryoSerializerRegister : kryoSerializerRegisters) {
            kryoSerializerRegister.registerSerializers(kryo);
        }

        return kryo;
    }

    public void setRegistrationRequired(boolean registrationRequired) {
        this.registrationRequired = registrationRequired;
    }

    /**
     * 获取Kryo
     *
     * @return
     */
    public abstract Kryo getKryo();
}
