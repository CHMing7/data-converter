package com.chm.converter.test.json.fastjson;

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
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.json.FastjsonConverter;
import com.chm.converter.json.JsonConverter;
import com.chm.converter.json.fastjson.FastjsonCoreCodec;
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

    JsonConverter converter;

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
        serializeConfig.put(Instant.class, new FastjsonCoreCodec(Java8TimeCodec.INSTANT_CODEC.withConverter(converter)));
        serializeConfig.put(LocalDate.class, new FastjsonCoreCodec(Java8TimeCodec.LOCAL_DATE_CODEC.withConverter(converter)));
        serializeConfig.put(LocalDateTime.class, new FastjsonCoreCodec(Java8TimeCodec.LOCAL_DATE_TIME_CODEC.withConverter(converter)));
        serializeConfig.put(LocalTime.class, new FastjsonCoreCodec(Java8TimeCodec.LOCAL_TIME_CODEC.withConverter(converter)));
        serializeConfig.put(OffsetDateTime.class, new FastjsonCoreCodec(Java8TimeCodec.OFFSET_DATE_TIME_CODEC.withConverter(converter)));
        serializeConfig.put(OffsetTime.class, new FastjsonCoreCodec(Java8TimeCodec.OFFSET_TIME_CODEC.withConverter(converter)));
        serializeConfig.put(ZonedDateTime.class, new FastjsonCoreCodec(Java8TimeCodec.ZONED_DATE_TIME_CODEC.withConverter(converter)));
        serializeConfig.put(MonthDay.class, new FastjsonCoreCodec(Java8TimeCodec.MONTH_DAY_CODEC.withConverter(converter)));
        serializeConfig.put(YearMonth.class, new FastjsonCoreCodec(Java8TimeCodec.YEAR_MONTH_CODEC.withConverter(converter)));
        serializeConfig.put(Year.class, new FastjsonCoreCodec(Java8TimeCodec.YEAR_CODEC.withConverter(converter)));
        serializeConfig.put(ZoneOffset.class, new FastjsonCoreCodec(Java8TimeCodec.ZONE_OFFSET_CODEC.withConverter(converter)));

        // Default Date Serializer
        serializeConfig.put(java.sql.Date.class, new FastjsonCoreCodec(DefaultDateCodec.SQL_DATE_CODEC.withConverter(converter)));
        serializeConfig.put(Timestamp.class, new FastjsonCoreCodec(DefaultDateCodec.TIMESTAMP_CODEC.withConverter(converter)));
        serializeConfig.put(Date.class, new FastjsonCoreCodec(DefaultDateCodec.DATE_CODEC.withConverter(converter)));

        // Java8 Time Serializer
        parserConfig.putDeserializer(Instant.class, new FastjsonCoreCodec(Java8TimeCodec.INSTANT_CODEC.withConverter(converter)));
        parserConfig.putDeserializer(LocalDate.class, new FastjsonCoreCodec(Java8TimeCodec.LOCAL_DATE_CODEC.withConverter(converter)));
        parserConfig.putDeserializer(LocalDateTime.class, new FastjsonCoreCodec(Java8TimeCodec.LOCAL_DATE_TIME_CODEC.withConverter(converter)));
        parserConfig.putDeserializer(LocalTime.class, new FastjsonCoreCodec(Java8TimeCodec.LOCAL_TIME_CODEC.withConverter(converter)));
        parserConfig.putDeserializer(OffsetDateTime.class, new FastjsonCoreCodec(Java8TimeCodec.OFFSET_DATE_TIME_CODEC.withConverter(converter)));
        parserConfig.putDeserializer(OffsetTime.class, new FastjsonCoreCodec(Java8TimeCodec.OFFSET_TIME_CODEC.withConverter(converter)));
        parserConfig.putDeserializer(ZonedDateTime.class, new FastjsonCoreCodec(Java8TimeCodec.ZONED_DATE_TIME_CODEC.withConverter(converter)));
        parserConfig.putDeserializer(MonthDay.class, new FastjsonCoreCodec(Java8TimeCodec.MONTH_DAY_CODEC.withConverter(converter)));
        parserConfig.putDeserializer(YearMonth.class, new FastjsonCoreCodec(Java8TimeCodec.YEAR_MONTH_CODEC.withConverter(converter)));
        parserConfig.putDeserializer(Year.class, new FastjsonCoreCodec(Java8TimeCodec.YEAR_CODEC.withConverter(converter)));
        parserConfig.putDeserializer(ZoneOffset.class, new FastjsonCoreCodec(Java8TimeCodec.ZONE_OFFSET_CODEC.withConverter(converter)));

        // Default Date Serializer
        parserConfig.putDeserializer(java.sql.Date.class, new FastjsonCoreCodec(DefaultDateCodec.SQL_DATE_CODEC.withConverter(converter)));
        parserConfig.putDeserializer(Timestamp.class, new FastjsonCoreCodec(DefaultDateCodec.TIMESTAMP_CODEC.withConverter(converter)));
        parserConfig.putDeserializer(Date.class, new FastjsonCoreCodec(DefaultDateCodec.DATE_CODEC.withConverter(converter)));
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