package com.chm.converter.test.cbor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.cbor.JacksonCborConverter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.jackson.deserializer.JacksonCoreCodecDeserializer;
import com.chm.converter.jackson.serializer.JacksonCoreCodecSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
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

    JacksonCborConverter converter;

    ObjectMapper mapper;

    User user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(JacksonCborConverter.class);
        user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());

        mapper = new ObjectMapper(new CBORFactory());
        SimpleModule module = new SimpleModule();
        // Java8 Time Serializer
        module.addSerializer(Instant.class, new JacksonCoreCodecSerializer<>(Java8TimeCodec.INSTANT_CODEC.withConverter(converter)));
        module.addSerializer(LocalDate.class, new JacksonCoreCodecSerializer<>(Java8TimeCodec.LOCAL_DATE_CODEC.withConverter(converter)));
        module.addSerializer(LocalDateTime.class, new JacksonCoreCodecSerializer<>(Java8TimeCodec.LOCAL_DATE_TIME_CODEC.withConverter(converter)));
        module.addSerializer(LocalTime.class, new JacksonCoreCodecSerializer<>(Java8TimeCodec.LOCAL_TIME_CODEC.withConverter(converter)));
        module.addSerializer(OffsetDateTime.class, new JacksonCoreCodecSerializer<>(Java8TimeCodec.OFFSET_DATE_TIME_CODEC.withConverter(converter)));
        module.addSerializer(OffsetTime.class, new JacksonCoreCodecSerializer<>(Java8TimeCodec.OFFSET_TIME_CODEC.withConverter(converter)));
        module.addSerializer(ZonedDateTime.class, new JacksonCoreCodecSerializer<>(Java8TimeCodec.ZONED_DATE_TIME_CODEC.withConverter(converter)));
        module.addSerializer(MonthDay.class, new JacksonCoreCodecSerializer<>(Java8TimeCodec.MONTH_DAY_CODEC.withConverter(converter)));
        module.addSerializer(YearMonth.class, new JacksonCoreCodecSerializer<>(Java8TimeCodec.YEAR_MONTH_CODEC.withConverter(converter)));
        module.addSerializer(Year.class, new JacksonCoreCodecSerializer<>(Java8TimeCodec.YEAR_CODEC.withConverter(converter)));
        module.addSerializer(ZoneOffset.class, new JacksonCoreCodecSerializer<>(Java8TimeCodec.ZONE_OFFSET_CODEC.withConverter(converter)));

        // Default Date Serializer
        module.addSerializer(java.sql.Date.class, new JacksonCoreCodecSerializer<>(DefaultDateCodec.SQL_DATE_CODEC.withConverter(converter)));
        module.addSerializer(Timestamp.class, new JacksonCoreCodecSerializer<>(DefaultDateCodec.TIMESTAMP_CODEC.withConverter(converter)));
        module.addSerializer(Date.class, new JacksonCoreCodecSerializer<>(DefaultDateCodec.DATE_CODEC.withConverter(converter)));

        // Java8 Time Deserializer
        module.addDeserializer(Instant.class, new JacksonCoreCodecDeserializer<>(Java8TimeCodec.INSTANT_CODEC.withConverter(converter)));
        module.addDeserializer(LocalDate.class, new JacksonCoreCodecDeserializer<>(Java8TimeCodec.LOCAL_DATE_CODEC.withConverter(converter)));
        module.addDeserializer(LocalDateTime.class, new JacksonCoreCodecDeserializer<>(Java8TimeCodec.LOCAL_DATE_TIME_CODEC.withConverter(converter)));
        module.addDeserializer(LocalTime.class, new JacksonCoreCodecDeserializer<>(Java8TimeCodec.LOCAL_TIME_CODEC.withConverter(converter)));
        module.addDeserializer(OffsetDateTime.class, new JacksonCoreCodecDeserializer<>(Java8TimeCodec.OFFSET_DATE_TIME_CODEC.withConverter(converter)));
        module.addDeserializer(OffsetTime.class, new JacksonCoreCodecDeserializer<>(Java8TimeCodec.OFFSET_TIME_CODEC.withConverter(converter)));
        module.addDeserializer(ZonedDateTime.class, new JacksonCoreCodecDeserializer<>(Java8TimeCodec.ZONED_DATE_TIME_CODEC.withConverter(converter)));
        module.addDeserializer(MonthDay.class, new JacksonCoreCodecDeserializer<>(Java8TimeCodec.MONTH_DAY_CODEC.withConverter(converter)));
        module.addDeserializer(YearMonth.class, new JacksonCoreCodecDeserializer<>(Java8TimeCodec.YEAR_MONTH_CODEC.withConverter(converter)));
        module.addDeserializer(Year.class, new JacksonCoreCodecDeserializer<>(Java8TimeCodec.YEAR_CODEC.withConverter(converter)));
        module.addDeserializer(ZoneOffset.class, new JacksonCoreCodecDeserializer<>(Java8TimeCodec.ZONE_OFFSET_CODEC.withConverter(converter)));

        // Default Date Deserializer
        module.addDeserializer(java.sql.Date.class, new JacksonCoreCodecDeserializer<>(DefaultDateCodec.SQL_DATE_CODEC.withConverter(converter)));
        module.addDeserializer(Timestamp.class, new JacksonCoreCodecDeserializer<>(DefaultDateCodec.TIMESTAMP_CODEC.withConverter(converter)));
        module.addDeserializer(Date.class, new JacksonCoreCodecDeserializer<>(DefaultDateCodec.DATE_CODEC.withConverter(converter)));
        mapper.registerModule(module);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Test
    public void testOriginal() throws Exception {
        // new
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        userMap.put("user1", user);
        byte[] encode = converter.encode(userMap);
        // original
        byte[] encode2 = mapper.writeValueAsBytes(userMap);
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:" + StrUtil.str(encode2, "utf-8"));

        assertArrayEquals(encode, encode2);

        TypeToken<Map<String, User>> typeToken = new TypeToken<Map<String, User>>() {
        };
        Map<String, User> newUserMap = mapper.readValue(encode, mapper.getTypeFactory().constructType(typeToken.getType()));
        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testUser() {
        byte[] encode = converter.encode(user);
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));

        User newUser = converter.convertToJavaObject(encode, User.class);

        assertEquals(user, newUser);
    }


    @Test
    public void testMap() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        byte[] encode = converter.encode(userMap);
        StaticLog.info("testMap:" + StrUtil.str(encode, "utf-8"));

        TypeToken<Map<String, User>> typeRef0 = new TypeToken<Map<String, User>>() {
        };

        Map<String, User> newUserMap = converter.convertToJavaObject(encode, typeRef0);

        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testCollection() {
        Collection<User> userCollection = CollUtil.newArrayList();
        userCollection.add(user);
        userCollection.add(user);
        userCollection.add(user);

        byte[] encode = converter.encode(userCollection);

        StaticLog.info("testCollection:" + StrUtil.str(encode, "utf-8"));

        TypeToken<Collection<User>> typeRef0 = new TypeToken<Collection<User>>() {
        };

        Collection<User> newUserCollection = converter.convertToJavaObject(encode, typeRef0);

        assertEquals(userCollection, newUserCollection);
    }


    @Test
    public void testArray() {
        User[] userArray = new User[3];
        userArray[0] = user;
        userArray[1] = user;
        userArray[2] = user;
        byte[] encode = converter.encode(userArray);
        StaticLog.info("testArray:" + StrUtil.str(encode, "utf-8"));

        TypeToken<User[]> typeRef0 = new TypeToken<User[]>() {
        };

        User[] newUserArray = converter.convertToJavaObject(encode, typeRef0);

        assertArrayEquals(userArray, newUserArray);
    }


    @Test
    public void testEnum() {
        byte[] encode = converter.encode(Enum.ONE);
        StaticLog.info("testEnum:" + StrUtil.str(encode, "utf-8"));

        Enum newEnum = converter.convertToJavaObject(encode, Enum.class);

        assertEquals(Enum.ONE, newEnum);
    }

    public enum Enum {
        @FieldProperty(name = "testOne")
        ONE, TWO
    }
}