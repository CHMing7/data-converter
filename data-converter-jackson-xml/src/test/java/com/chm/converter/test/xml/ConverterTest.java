package com.chm.converter.test.xml;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.jackson.deserializer.JacksonDefaultDateTypeDeserializer;
import com.chm.converter.jackson.deserializer.JacksonJava8TimeDeserializer;
import com.chm.converter.jackson.serializer.JacksonDefaultDateTypeSerializer;
import com.chm.converter.jackson.serializer.JacksonJava8TimeSerializer;
import com.chm.converter.xml.JacksonXmlConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class ConverterTest {

    JacksonXmlConverter converter;

    ObjectMapper mapper;

    User2 user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(JacksonXmlConverter.class);
        user = new User2();
        User2 user1 = new User2();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());


        JacksonXmlModule module = new JacksonXmlModule();
        // Java8 Time Serializer
        module.addSerializer(Instant.class, new JacksonJava8TimeSerializer<>(Instant.class, converter));
        module.addSerializer(LocalDate.class, new JacksonJava8TimeSerializer<>(LocalDate.class, converter));
        module.addSerializer(LocalDateTime.class, new JacksonJava8TimeSerializer<>(LocalDateTime.class, converter));
        module.addSerializer(LocalTime.class, new JacksonJava8TimeSerializer<>(LocalTime.class, converter));
        module.addSerializer(OffsetDateTime.class, new JacksonJava8TimeSerializer<>(OffsetDateTime.class, converter));
        module.addSerializer(OffsetTime.class, new JacksonJava8TimeSerializer<>(OffsetTime.class, converter));
        module.addSerializer(ZonedDateTime.class, new JacksonJava8TimeSerializer<>(ZonedDateTime.class, converter));
        module.addSerializer(MonthDay.class, new JacksonJava8TimeSerializer<>(MonthDay.class, converter));
        module.addSerializer(YearMonth.class, new JacksonJava8TimeSerializer<>(YearMonth.class, converter));
        module.addSerializer(Year.class, new JacksonJava8TimeSerializer<>(Year.class, converter));
        module.addSerializer(ZoneOffset.class, new JacksonJava8TimeSerializer<>(ZoneOffset.class, converter));

        // Default Date Serializer
        module.addSerializer(java.sql.Date.class, new JacksonDefaultDateTypeSerializer<>(java.sql.Date.class, converter));
        module.addSerializer(Timestamp.class, new JacksonDefaultDateTypeSerializer<>(Timestamp.class, converter));
        module.addSerializer(Date.class, new JacksonDefaultDateTypeSerializer<>(Date.class, converter));

        // Java8 Time Deserializer
        module.addDeserializer(Instant.class, new JacksonJava8TimeDeserializer<>(Instant.class, converter));
        module.addDeserializer(LocalDate.class, new JacksonJava8TimeDeserializer<>(LocalDate.class, converter));
        module.addDeserializer(LocalDateTime.class, new JacksonJava8TimeDeserializer<>(LocalDateTime.class, converter));
        module.addDeserializer(LocalTime.class, new JacksonJava8TimeDeserializer<>(LocalTime.class, converter));
        module.addDeserializer(OffsetDateTime.class, new JacksonJava8TimeDeserializer<>(OffsetDateTime.class, converter));
        module.addDeserializer(OffsetTime.class, new JacksonJava8TimeDeserializer<>(OffsetTime.class, converter));
        module.addDeserializer(ZonedDateTime.class, new JacksonJava8TimeDeserializer<>(ZonedDateTime.class, converter));
        module.addDeserializer(MonthDay.class, new JacksonJava8TimeDeserializer<>(MonthDay.class, converter));
        module.addDeserializer(YearMonth.class, new JacksonJava8TimeDeserializer<>(YearMonth.class, converter));
        module.addDeserializer(Year.class, new JacksonJava8TimeDeserializer<>(Year.class, converter));
        module.addDeserializer(ZoneOffset.class, new JacksonJava8TimeDeserializer<>(ZoneOffset.class, converter));

        // Default Date Serializer
        module.addDeserializer(java.sql.Date.class, new JacksonDefaultDateTypeDeserializer<>(java.sql.Date.class, converter));
        module.addDeserializer(Timestamp.class, new JacksonDefaultDateTypeDeserializer<>(Timestamp.class, converter));
        module.addDeserializer(Date.class, new JacksonDefaultDateTypeDeserializer<>(Date.class, converter));
        mapper = new XmlMapper(module);
        mapper.registerModule(module);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Test
    public void testOriginal() throws Exception {
        // new
        Map<String, User2> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        userMap.put("user1", user);
        String encode = converter.encode(userMap);
        // original
        String encode2 = mapper.writeValueAsString(userMap);
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:" + StrUtil.str(encode2, "utf-8"));

        assertEquals(encode, encode2);

        TypeToken<Map<String, User2>> typeToken = new TypeToken<Map<String, User2>>() {
        };
        Map<String, User2> newUserMap = mapper.readValue(encode, mapper.getTypeFactory().constructType(typeToken.getType()));
        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testUser() {
        String encode = converter.encode(user);
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));

        User2 newUser = converter.convertToJavaObject(encode, User2.class);

        assertEquals(user, newUser);
    }


    @Test
    public void testMap() {
        Map<String, User2> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        String encode = converter.encode(userMap);
        StaticLog.info("testMap:" + StrUtil.str(encode, "utf-8"));

        TypeReference<Map<String, User2>> typeRef0 = new TypeReference<Map<String, User2>>() {
        };

        Map<String, User2> newUserMap = converter.convertToJavaObject(encode, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testCollection() {
        Collection<User2> userCollection = CollUtil.newArrayList();
        userCollection.add(user);
        userCollection.add(user);
        userCollection.add(user);

        String encode = converter.encode(userCollection);

        StaticLog.info("testCollection:" + StrUtil.str(encode, "utf-8"));

        TypeReference<Collection<User2>> typeRef0 = new TypeReference<Collection<User2>>() {
        };

        Collection<User2> newUserCollection = converter.convertToJavaObject(encode, typeRef0.getType());

        assertEquals(userCollection, newUserCollection);
    }


    @Test
    public void testArray() {
        User2[] userArray = new User2[3];
        userArray[0] = user;
        userArray[1] = user;
        userArray[2] = user;
        String encode = converter.encode(userArray);
        StaticLog.info("testArray:" + StrUtil.str(encode, "utf-8"));

        TypeReference<User2[]> typeRef0 = new TypeReference<User2[]>() {
        };

        User2[] newUserArray = converter.convertToJavaObject(encode, typeRef0.getType());

        assertArrayEquals(userArray, newUserArray);
    }


    @Test
    public void testEnum() {

        String encode = converter.encode(Enum.ONE);
        StaticLog.info("testEnum:" + StrUtil.str(encode, "utf-8"));

        Enum newEnum = converter.convertToJavaObject(encode, Enum.class);

        assertEquals(Enum.ONE, newEnum);
    }

    public enum Enum {
        @FieldProperty(name = "testOne")
        ONE, TWO
    }
}