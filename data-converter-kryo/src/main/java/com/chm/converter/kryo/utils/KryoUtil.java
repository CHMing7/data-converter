package com.chm.converter.kryo.utils;

import com.chm.converter.kryo.KryoSerializerRegister;
import com.chm.converter.kryo.factory.AbstractKryoFactory;
import com.chm.converter.kryo.factory.ThreadLocalKryoFactory;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-26
 **/
public class KryoUtil {

    private static final AbstractKryoFactory KRYO_FACTORY = new ThreadLocalKryoFactory();

    public static Kryo get() {
        return KRYO_FACTORY.getKryo();
    }

    public static void register(Class<?> clazz) {
        KRYO_FACTORY.registerClass(clazz);
    }

    public static void register(Class<?> clazz, Serializer serializer) {
        KRYO_FACTORY.registerClass(clazz, serializer);
    }

    public static void register(KryoSerializerRegister kryoSerializerRegister) {
        KRYO_FACTORY.register(kryoSerializerRegister);
    }

    public static void setRegistrationRequired(boolean registrationRequired) {
        KRYO_FACTORY.setRegistrationRequired(registrationRequired);
    }
}
