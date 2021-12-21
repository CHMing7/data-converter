package com.chm.converter.fst.serialization;

import com.chm.converter.core.Converter;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.fst.serializers.DefaultDateSerializer;
import com.chm.converter.fst.serializers.FstSerializer;
import com.chm.converter.fst.serializers.Java8TimeSerializer;
import com.chm.converter.fst.serializers.JavaBeanSerializer;
import org.nustaq.serialization.FSTObjectSerializer;
import org.nustaq.serialization.FSTSerializerRegistry;
import org.nustaq.serialization.FSTSerializerRegistryDelegate;

import java.sql.Timestamp;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-30
 **/
public class FstSerializerRegistryDelegate implements FSTSerializerRegistryDelegate {

    private static FstSerializerRegistryDelegate DEFAULT;

    private final Converter<?> converter;

    private final UniversalGenerate<FstSerializer> generate;

    private FSTSerializerRegistry serializerRegistry;

    private FSTSerializerRegistry skipDelegateSerializerRegistry;

    public FstSerializerRegistryDelegate() {
        this(null, null, false);
    }

    public FstSerializerRegistryDelegate(List<UniversalFactory<FstSerializer>> protostuffCodecFactories, Converter<?> converter, boolean isInitCodecs) {
        generate = new UniversalGenerate<>(protostuffCodecFactories);
        this.converter = converter;
        if (isInitCodecs) {
            initSerializer();
        }
    }

    private void initSerializer() {
      /*  // Default Date Serializer
        generate.put(java.sql.Date.class, new DefaultDateSerializer<>(converter));
        generate.put(Timestamp.class, new DefaultDateSerializer<>(converter));
        generate.put(Date.class, new DefaultDateSerializer<>(converter));

        // Java8 Time Serializer
        generate.put(Instant.class, new Java8TimeSerializer<>(Instant.class, converter));
        generate.put(LocalDate.class, new Java8TimeSerializer<>(LocalDate.class, converter));
        generate.put(LocalDateTime.class, new Java8TimeSerializer<>(LocalDateTime.class, converter));
        generate.put(LocalTime.class, new Java8TimeSerializer<>(LocalTime.class, converter));
        generate.put(OffsetDateTime.class, new Java8TimeSerializer<>(OffsetDateTime.class, converter));
        generate.put(OffsetTime.class, new Java8TimeSerializer<>(OffsetTime.class, converter));
        generate.put(ZonedDateTime.class, new Java8TimeSerializer<>(ZonedDateTime.class, converter));
        generate.put(MonthDay.class, new Java8TimeSerializer<>(MonthDay.class, converter));
        generate.put(YearMonth.class, new Java8TimeSerializer<>(YearMonth.class, converter));
        generate.put(Year.class, new Java8TimeSerializer<>(Year.class, converter));
        generate.put(ZoneOffset.class, new Java8TimeSerializer<>(ZoneOffset.class, converter));*/
    }

    public static FstSerializerRegistryDelegate getDefault() {
        if (DEFAULT == null) {
            DEFAULT = newDefault();
        }
        return DEFAULT;
    }

    public static FstSerializerRegistryDelegate newDefault() {
        return newDefault(null);
    }

    public static FstSerializerRegistryDelegate newDefault(Converter<?> converter) {
        List<UniversalFactory<FstSerializer>> factories = new ArrayList<>();
        return new FstSerializerRegistryDelegate(factories, converter, true);
    }

    public UniversalGenerate<FstSerializer> getGenerate() {
        return generate;
    }

    @Override
    public FSTObjectSerializer getSerializer(Class cl) {
        FstSerializer serializer = generate.get(cl);
        if (serializer != null) {
            return serializer;
        }
        FSTObjectSerializer objectSerializer = skipDelegateSerializerRegistry.getSerializer(cl);
        if (objectSerializer != null) {
            return objectSerializer;
        }
        return new JavaBeanSerializer<>(cl, this.serializerRegistry, this.converter);
    }

    public FSTSerializerRegistry getSerializerRegistry() {
        return serializerRegistry;
    }

    public void setSerializerRegistry(FSTSerializerRegistry serializerRegistry) {
        this.serializerRegistry = serializerRegistry;
    }

    public FSTSerializerRegistry getSkipDelegateSerializerRegistry() {
        return skipDelegateSerializerRegistry;
    }

    public void setSkipDelegateSerializerRegistry(FSTSerializerRegistry skipDelegateSerializerRegistry) {
        this.skipDelegateSerializerRegistry = skipDelegateSerializerRegistry;
    }
}

