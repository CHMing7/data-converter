package com.chm.converter.xml.jackson;

import com.chm.converter.core.Converter;
import com.chm.converter.jackson.deserializer.JacksonDefaultDateTypeDeserializer;
import com.chm.converter.jackson.deserializer.JacksonJava8TimeDeserializer;
import com.chm.converter.jackson.serializer.JacksonDefaultDateTypeSerializer;
import com.chm.converter.jackson.serializer.JacksonJava8TimeSerializer;
import com.chm.converter.xml.JacksonXmlConverter;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

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
import java.util.Map;
import java.util.Objects;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-08
 **/
public class JacksonXmlModule extends com.fasterxml.jackson.dataformat.xml.JacksonXmlModule {

    public JacksonXmlModule(Converter<?> converter) {
        super();
        // Java8 Time Serializer
        addSerializer(Instant.class, new JacksonJava8TimeSerializer<>(Instant.class, converter));
        addSerializer(LocalDate.class, new JacksonJava8TimeSerializer<>(LocalDate.class, converter));
        addSerializer(LocalDateTime.class, new JacksonJava8TimeSerializer<>(LocalDateTime.class, converter));
        addSerializer(LocalTime.class, new JacksonJava8TimeSerializer<>(LocalTime.class, converter));
        addSerializer(OffsetDateTime.class, new JacksonJava8TimeSerializer<>(OffsetDateTime.class, converter));
        addSerializer(OffsetTime.class, new JacksonJava8TimeSerializer<>(OffsetTime.class, converter));
        addSerializer(ZonedDateTime.class, new JacksonJava8TimeSerializer<>(ZonedDateTime.class, converter));
        addSerializer(MonthDay.class, new JacksonJava8TimeSerializer<>(MonthDay.class, converter));
        addSerializer(YearMonth.class, new JacksonJava8TimeSerializer<>(YearMonth.class, converter));
        addSerializer(Year.class, new JacksonJava8TimeSerializer<>(Year.class, converter));
        addSerializer(ZoneOffset.class, new JacksonJava8TimeSerializer<>(ZoneOffset.class, converter));

        // Default Date Serializer
        addSerializer(java.sql.Date.class, new JacksonDefaultDateTypeSerializer<>(java.sql.Date.class, converter));
        addSerializer(Timestamp.class, new JacksonDefaultDateTypeSerializer<>(Timestamp.class, converter));
        addSerializer(Date.class, new JacksonDefaultDateTypeSerializer<>(Date.class, converter));

        // Java8 Time Deserializer
        addDeserializer(Instant.class, new JacksonJava8TimeDeserializer<>(Instant.class, converter));
        addDeserializer(LocalDate.class, new JacksonJava8TimeDeserializer<>(LocalDate.class, converter));
        addDeserializer(LocalDateTime.class, new JacksonJava8TimeDeserializer<>(LocalDateTime.class, converter));
        addDeserializer(LocalTime.class, new JacksonJava8TimeDeserializer<>(LocalTime.class, converter));
        addDeserializer(OffsetDateTime.class, new JacksonJava8TimeDeserializer<>(OffsetDateTime.class, converter));
        addDeserializer(OffsetTime.class, new JacksonJava8TimeDeserializer<>(OffsetTime.class, converter));
        addDeserializer(ZonedDateTime.class, new JacksonJava8TimeDeserializer<>(ZonedDateTime.class, converter));
        addDeserializer(MonthDay.class, new JacksonJava8TimeDeserializer<>(MonthDay.class, converter));
        addDeserializer(YearMonth.class, new JacksonJava8TimeDeserializer<>(YearMonth.class, converter));
        addDeserializer(Year.class, new JacksonJava8TimeDeserializer<>(Year.class, converter));
        addDeserializer(ZoneOffset.class, new JacksonJava8TimeDeserializer<>(ZoneOffset.class, converter));

        // Default Date Serializer
        addDeserializer(java.sql.Date.class, new JacksonDefaultDateTypeDeserializer<>(java.sql.Date.class, converter));
        addDeserializer(Timestamp.class, new JacksonDefaultDateTypeDeserializer<>(Timestamp.class, converter));
        addDeserializer(Date.class, new JacksonDefaultDateTypeDeserializer<>(Date.class, converter));
    }

    /*
    /**********************************************************************
    /* Life-cycle: construction
    /**********************************************************************
     */

    @SuppressWarnings("deprecation")
    @Override
    public void setupModule(SetupContext context) {
        // as well as AnnotationIntrospector
        context.insertAnnotationIntrospector(_constructIntrospector());

        // and finally inform XmlFactory about overrides, if need be:
        if (!Objects.equals(_cfgNameForTextElement, FromXmlParser.DEFAULT_UNNAMED_TEXT_PROPERTY)) {
            XmlMapper m = context.getOwner();
            m.getFactory().setXMLTextElementName(_cfgNameForTextElement);
        }

        // Usually this would be the first call; but here anything added will
        // be stuff user may has added, so do it afterwards instead.
        simpleModuleSetupModule(context);
    }

    private void simpleModuleSetupModule(SetupContext context) {
        if (_serializers != null) {
            context.addSerializers(_serializers);
        }
        if (_deserializers != null) {
            context.addDeserializers(_deserializers);
        }
        if (_keySerializers != null) {
            context.addKeySerializers(_keySerializers);
        }
        if (_keyDeserializers != null) {
            context.addKeyDeserializers(_keyDeserializers);
        }
        if (_abstractTypes != null) {
            context.addAbstractTypeResolver(_abstractTypes);
        }
        if (_valueInstantiators != null) {
            context.addValueInstantiators(_valueInstantiators);
        }
        if (_deserializerModifier != null) {
            context.addBeanDeserializerModifier(_deserializerModifier);
        }
        if (_serializerModifier != null) {
            context.addBeanSerializerModifier(_serializerModifier);
        }
        if (_subtypes != null && _subtypes.size() > 0) {
            context.registerSubtypes(_subtypes.toArray(new NamedType[_subtypes.size()]));
        }
        if (_namingStrategy != null) {
            context.setNamingStrategy(_namingStrategy);
        }
        if (_mixins != null) {
            for (Map.Entry<Class<?>, Class<?>> entry : _mixins.entrySet()) {
                context.setMixInAnnotations(entry.getKey(), entry.getValue());
            }
        }
    }

    /*
    /**********************************************************************
    /* Internal methods
    /**********************************************************************
     */

    @Override
    protected AnnotationIntrospector _constructIntrospector() {
        return new JacksonXmlAnnotationIntrospector(_cfgDefaultUseWrapper, JacksonXmlConverter::checkExistJacksonXmlAnnotation);
    }

}
