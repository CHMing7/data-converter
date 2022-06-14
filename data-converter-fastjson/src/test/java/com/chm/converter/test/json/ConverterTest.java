package com.chm.converter.test.json;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.json.FastjsonConverter;
import com.chm.converter.json.fastjson.FastjsonDefaultDateCodec;
import com.chm.converter.json.fastjson.FastjsonJdk8DateCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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

    FastjsonConverter converter;

    SerializeConfig serializeConfig;

    ParserConfig parserConfig;

    User user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(FastjsonConverter.class);
        user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user1.setPassword("password2");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());
        serializeConfig = new SerializeConfig();
        parserConfig = new ParserConfig();
        // Java8 Time Serializer
        serializeConfig.put(Instant.class, new FastjsonJdk8DateCodec<>(Instant.class, converter));
        serializeConfig.put(LocalDate.class, new FastjsonJdk8DateCodec<>(LocalDate.class, converter));
        serializeConfig.put(LocalDateTime.class, new FastjsonJdk8DateCodec<>(LocalDateTime.class, converter));
        serializeConfig.put(LocalTime.class, new FastjsonJdk8DateCodec<>(LocalTime.class, converter));
        serializeConfig.put(OffsetDateTime.class, new FastjsonJdk8DateCodec<>(OffsetDateTime.class, converter));
        serializeConfig.put(OffsetTime.class, new FastjsonJdk8DateCodec<>(OffsetTime.class, converter));
        serializeConfig.put(ZonedDateTime.class, new FastjsonJdk8DateCodec<>(ZonedDateTime.class, converter));
        serializeConfig.put(MonthDay.class, new FastjsonJdk8DateCodec<>(MonthDay.class, converter));
        serializeConfig.put(YearMonth.class, new FastjsonJdk8DateCodec<>(YearMonth.class, converter));
        serializeConfig.put(Year.class, new FastjsonJdk8DateCodec<>(Year.class, converter));
        serializeConfig.put(ZoneOffset.class, new FastjsonJdk8DateCodec<>(ZoneOffset.class, converter));

        // Default Date Serializer
        serializeConfig.put(java.sql.Date.class, new FastjsonDefaultDateCodec<>(java.sql.Date.class, converter));
        serializeConfig.put(Timestamp.class, new FastjsonDefaultDateCodec<>(Timestamp.class, converter));
        serializeConfig.put(Date.class, new FastjsonDefaultDateCodec<>(Date.class, converter));

        // Java8 Time Serializer
        parserConfig.putDeserializer(Instant.class, new FastjsonJdk8DateCodec<>(Instant.class, converter));
        parserConfig.putDeserializer(LocalDate.class, new FastjsonJdk8DateCodec<>(LocalDate.class, converter));
        parserConfig.putDeserializer(LocalDateTime.class, new FastjsonJdk8DateCodec<>(LocalDateTime.class, converter));
        parserConfig.putDeserializer(LocalTime.class, new FastjsonJdk8DateCodec<>(LocalTime.class, converter));
        parserConfig.putDeserializer(OffsetDateTime.class, new FastjsonJdk8DateCodec<>(OffsetDateTime.class, converter));
        parserConfig.putDeserializer(OffsetTime.class, new FastjsonJdk8DateCodec<>(OffsetTime.class, converter));
        parserConfig.putDeserializer(ZonedDateTime.class, new FastjsonJdk8DateCodec<>(ZonedDateTime.class, converter));
        parserConfig.putDeserializer(MonthDay.class, new FastjsonJdk8DateCodec<>(MonthDay.class, converter));
        parserConfig.putDeserializer(YearMonth.class, new FastjsonJdk8DateCodec<>(YearMonth.class, converter));
        parserConfig.putDeserializer(Year.class, new FastjsonJdk8DateCodec<>(Year.class, converter));
        parserConfig.putDeserializer(ZoneOffset.class, new FastjsonJdk8DateCodec<>(ZoneOffset.class, converter));

        // Default Date Serializer
        parserConfig.putDeserializer(java.sql.Date.class, new FastjsonDefaultDateCodec<>(java.sql.Date.class, converter));
        parserConfig.putDeserializer(Timestamp.class, new FastjsonDefaultDateCodec<>(Timestamp.class, converter));
        parserConfig.putDeserializer(Date.class, new FastjsonDefaultDateCodec<>(Date.class, converter));

    }

    @Test
    public void testOriginal() throws IOException {
        // new
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        userMap.put("user1", user);
        String encode = converter.encode(userMap);
        // original
        String encode2 = JSON.toJSONString(userMap, serializeConfig, SerializerFeature.DisableCircularReferenceDetect);
        StaticLog.info("testUser:{}", StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:{}", StrUtil.str(encode2, "utf-8"));

        assertEquals(encode, encode2);


        Map<String, User> newUserMap = JSON.parseObject(encode, new TypeToken<Map<String, User>>() {
        }.getType(), parserConfig);
        assertEquals(userMap, newUserMap);
    }


    @Test
    public void testUser() {
        String encode = converter.encode(user);
        StaticLog.info("testUser:{}", StrUtil.str(encode, "utf-8"));

        User newUser = converter.convertToJavaObject(encode, User.class);

        assertEquals(user, newUser);
    }


    @Test
    public void testMap() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        String encode = converter.encode(userMap);
        StaticLog.info("testMap:{}", StrUtil.str(encode, "utf-8"));

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

        String encode = converter.encode(userCollection);

        StaticLog.info("testCollection:{}", StrUtil.str(encode, "utf-8"));

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
        String encode = converter.encode(userArray);
        StaticLog.info("testArray:{}", StrUtil.str(encode, "utf-8"));

        TypeToken<User[]> typeRef0 = new TypeToken<User[]>() {
        };

        User[] newUserArray = converter.convertToJavaObject(encode, typeRef0);

        assertArrayEquals(userArray, newUserArray);
    }


    @Test
    public void testEnum() {
        String encode = converter.encode(Enum.ONE);
        StaticLog.info("testEnum:{}", StrUtil.str(encode, "utf-8"));

        Enum newEnum = converter.convertToJavaObject(encode, Enum.class);

        assertEquals(Enum.ONE, newEnum);
    }

    public enum Enum {
        @FieldProperty(name = "testOne")
        ONE, TWO
    }
}