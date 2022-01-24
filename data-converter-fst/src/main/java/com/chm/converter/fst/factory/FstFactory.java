package com.chm.converter.fst.factory;

import com.chm.converter.core.Converter;
import com.chm.converter.fst.instantiators.CustomFstDefaultClassInstantiator;
import com.chm.converter.fst.serialization.FstConfiguration;
import com.chm.converter.fst.serialization.FstSerializerRegistryDelegate;
import com.chm.converter.fst.serializers.DefaultDateSerializer;
import com.chm.converter.fst.serializers.Java8TimeSerializer;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
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
import java.util.Date;

/**
 * Fst object input/output factory
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-27
 **/
public class FstFactory {

    private final FSTConfiguration conf;

    public FstFactory(Converter<?> converter) {
        conf = new FstConfiguration(converter);
        conf.setInstantiator(new CustomFstDefaultClassInstantiator());
        conf.setForceSerializable(true);
        // Default Date Serializer
        conf.registerSerializer(java.sql.Date.class, new DefaultDateSerializer<>(converter), false);
        conf.registerSerializer(Timestamp.class, new DefaultDateSerializer<>(converter), false);
        conf.registerSerializer(Date.class, new DefaultDateSerializer<>(converter), false);

        // Java8 Time Serializer
        conf.registerSerializer(Instant.class, new Java8TimeSerializer<>(Instant.class, converter), false);
        conf.registerSerializer(LocalDate.class, new Java8TimeSerializer<>(LocalDate.class, converter), false);
        conf.registerSerializer(LocalDateTime.class, new Java8TimeSerializer<>(LocalDateTime.class, converter), false);
        conf.registerSerializer(LocalTime.class, new Java8TimeSerializer<>(LocalTime.class, converter), false);
        conf.registerSerializer(OffsetDateTime.class, new Java8TimeSerializer<>(OffsetDateTime.class, converter), false);
        conf.registerSerializer(OffsetTime.class, new Java8TimeSerializer<>(OffsetTime.class, converter), false);
        conf.registerSerializer(ZonedDateTime.class, new Java8TimeSerializer<>(ZonedDateTime.class, converter), false);
        conf.registerSerializer(MonthDay.class, new Java8TimeSerializer<>(MonthDay.class, converter), false);
        conf.registerSerializer(YearMonth.class, new Java8TimeSerializer<>(YearMonth.class, converter), false);
        conf.registerSerializer(Year.class, new Java8TimeSerializer<>(Year.class, converter), false);
        conf.registerSerializer(ZoneOffset.class, new Java8TimeSerializer<>(ZoneOffset.class, converter), false);
        conf.setSerializerRegistryDelegate(FstSerializerRegistryDelegate.newDefault(converter));
    }

    public FSTObjectOutput getObjectOutput(OutputStream outputStream) {
        return conf.getObjectOutput(outputStream);
    }

    public FSTObjectInput getObjectInput(InputStream inputStream) {
        return conf.getObjectInput(inputStream);
    }
}
