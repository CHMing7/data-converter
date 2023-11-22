package com.chm.converter.test.json.gson;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.json.GsonConverter;
import com.chm.converter.json.gson.GsonCoreCodecAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class ConverterTest {

    GsonConverter converter;

    Gson gson;

    User user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(GsonConverter.class);
        user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());

        GsonBuilder gsonBuilder = new GsonBuilder();
        // Java8 Time Serializer
        TypeAdapter<String> stringTypeAdapter = TypeAdapters.STRING;
        gsonBuilder.registerTypeAdapter(Instant.class, new GsonCoreCodecAdapter<>(Java8TimeCodec.INSTANT_CODEC.withConverter(converter), stringTypeAdapter));
        gsonBuilder.registerTypeAdapter(LocalDate.class, new GsonCoreCodecAdapter<>(Java8TimeCodec.LOCAL_DATE_CODEC.withConverter(converter), stringTypeAdapter));
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new GsonCoreCodecAdapter<>(Java8TimeCodec.LOCAL_DATE_TIME_CODEC.withConverter(converter), stringTypeAdapter));
        gsonBuilder.registerTypeAdapter(LocalTime.class, new GsonCoreCodecAdapter<>(Java8TimeCodec.LOCAL_TIME_CODEC.withConverter(converter), stringTypeAdapter));
        gsonBuilder.registerTypeAdapter(OffsetDateTime.class, new GsonCoreCodecAdapter<>(Java8TimeCodec.OFFSET_DATE_TIME_CODEC.withConverter(converter), stringTypeAdapter));
        gsonBuilder.registerTypeAdapter(OffsetTime.class, new GsonCoreCodecAdapter<>(Java8TimeCodec.OFFSET_TIME_CODEC.withConverter(converter), stringTypeAdapter));
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new GsonCoreCodecAdapter<>(Java8TimeCodec.ZONED_DATE_TIME_CODEC.withConverter(converter), stringTypeAdapter));
        gsonBuilder.registerTypeAdapter(MonthDay.class, new GsonCoreCodecAdapter<>(Java8TimeCodec.MONTH_DAY_CODEC.withConverter(converter), stringTypeAdapter));
        gsonBuilder.registerTypeAdapter(YearMonth.class, new GsonCoreCodecAdapter<>(Java8TimeCodec.YEAR_MONTH_CODEC.withConverter(converter), stringTypeAdapter));
        gsonBuilder.registerTypeAdapter(Year.class, new GsonCoreCodecAdapter<>(Java8TimeCodec.YEAR_CODEC.withConverter(converter), stringTypeAdapter));
        gsonBuilder.registerTypeAdapter(ZoneOffset.class, new GsonCoreCodecAdapter<>(Java8TimeCodec.ZONE_OFFSET_CODEC.withConverter(converter), stringTypeAdapter));

        // Default Date Serializer
        gsonBuilder.registerTypeAdapter(java.sql.Date.class, new GsonCoreCodecAdapter<>(DefaultDateCodec.SQL_DATE_CODEC.withConverter(converter), stringTypeAdapter));
        gsonBuilder.registerTypeAdapter(Timestamp.class, new GsonCoreCodecAdapter<>(DefaultDateCodec.TIMESTAMP_CODEC.withConverter(converter), stringTypeAdapter));
        gsonBuilder.registerTypeAdapter(Date.class, new GsonCoreCodecAdapter<>(DefaultDateCodec.DATE_CODEC.withConverter(converter), stringTypeAdapter));

        gson = gsonBuilder.create();
    }

    @Test
    public void testOriginal() throws IOException {

        // new
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        userMap.put("user1", user);
        String encode = converter.encode(userMap);
        // original
        String encode2 = gson.toJson(userMap);
        StaticLog.info("testUser:{}", StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:{}", StrUtil.str(encode2, "utf-8"));

        assertEquals(encode, encode2);


        Map<String, User> newUserMap = gson.fromJson(encode, new TypeToken<Map<String, User>>() {
        }.getType());
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
        converter.addGsonBuilderHandler(gsonBuilder ->
                gsonBuilder.registerTypeAdapter(new TypeToken<Map<String, User>>() {
                        }.getType(),
                        (InstanceCreator) type -> ConstructorFactory.INSTANCE.get(type).construct()
                ));
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

    @Test
    public void test123() {
        Map<String, Object> map = MapUtil.newHashMap(true);
        map.put("requestHeader", MapUtil.newHashMap());
        Map<String, Object> params = MapUtil.newHashMap();
        map.put("params", params);
        params.put("uuid", "asdsa");

        List<String> msisdnList = ListUtil.list(false, "123", "123213").subList(0, 1);
        params.put("msisdns", msisdnList);
        System.out.println(converter.encode(map));
        System.out.println(converter.encode(map));
    }
}