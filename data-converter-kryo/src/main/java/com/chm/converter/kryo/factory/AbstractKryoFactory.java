package com.chm.converter.kryo.factory;

import com.chm.converter.core.Converter;
import com.chm.converter.kryo.CompatibleKryo;
import com.chm.converter.kryo.KryoSerializerRegister;
import com.chm.converter.kryo.serializers.KryoDefaultDateSerializer;
import com.chm.converter.kryo.serializers.KryoJava8TimeSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

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


        // now just added some very common classes
        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(LinkedList.class);
        kryo.register(HashSet.class);
        kryo.register(Hashtable.class);
        kryo.register(ConcurrentHashMap.class);
        kryo.register(SimpleDateFormat.class);
        kryo.register(GregorianCalendar.class);
        kryo.register(Vector.class);
        kryo.register(BitSet.class);
        kryo.register(Object.class);

        // Java8 Time Serializer
        kryo.register(Instant.class, new KryoJava8TimeSerializer<>(Instant.class, (String) null, converter));
        kryo.register(LocalDate.class, new KryoJava8TimeSerializer<>(LocalDate.class, (String) null, converter));
        kryo.register(LocalDateTime.class, new KryoJava8TimeSerializer<>(LocalDateTime.class, (String) null, converter));
        kryo.register(LocalTime.class, new KryoJava8TimeSerializer<>(LocalTime.class, (String) null, converter));
        kryo.register(OffsetDateTime.class, new KryoJava8TimeSerializer<>(OffsetDateTime.class, (String) null, converter));
        kryo.register(OffsetTime.class, new KryoJava8TimeSerializer<>(OffsetTime.class, (String) null, converter));
        kryo.register(ZonedDateTime.class, new KryoJava8TimeSerializer<>(ZonedDateTime.class, (String) null, converter));
        kryo.register(MonthDay.class, new KryoJava8TimeSerializer<>(MonthDay.class, (String) null, converter));
        kryo.register(YearMonth.class, new KryoJava8TimeSerializer<>(YearMonth.class, (String) null, converter));
        kryo.register(Year.class, new KryoJava8TimeSerializer<>(Year.class, (String) null, converter));
        kryo.register(ZoneOffset.class, new KryoJava8TimeSerializer<>(ZoneOffset.class, (String) null, converter));

        // Default Date Serializer
        kryo.register(java.sql.Date.class, new KryoDefaultDateSerializer<>(java.sql.Date.class, (String) null, converter));
        kryo.register(Timestamp.class, new KryoDefaultDateSerializer<>(Timestamp.class, (String) null, converter));
        kryo.register(Date.class, new KryoDefaultDateSerializer<>(Date.class, (String) null, converter));

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

    public abstract Kryo getKryo();
}
