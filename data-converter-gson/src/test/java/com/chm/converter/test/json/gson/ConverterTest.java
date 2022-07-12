package com.chm.converter.test.json.gson;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.json.GsonConverter;
import com.chm.converter.json.gson.GsonDefaultDateTypeAdapter;
import com.chm.converter.json.gson.GsonJava8TimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        gsonBuilder.registerTypeAdapter(Instant.class, new GsonJava8TimeAdapter<>(Instant.class, converter));
        gsonBuilder.registerTypeAdapter(LocalDate.class, new GsonJava8TimeAdapter<>(LocalDate.class, converter));
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new GsonJava8TimeAdapter<>(LocalDateTime.class, converter));
        gsonBuilder.registerTypeAdapter(LocalTime.class, new GsonJava8TimeAdapter<>(LocalTime.class, converter));
        gsonBuilder.registerTypeAdapter(OffsetDateTime.class, new GsonJava8TimeAdapter<>(OffsetDateTime.class, converter));
        gsonBuilder.registerTypeAdapter(OffsetTime.class, new GsonJava8TimeAdapter<>(OffsetTime.class, converter));
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new GsonJava8TimeAdapter<>(ZonedDateTime.class, converter));
        gsonBuilder.registerTypeAdapter(MonthDay.class, new GsonJava8TimeAdapter<>(MonthDay.class, converter));
        gsonBuilder.registerTypeAdapter(YearMonth.class, new GsonJava8TimeAdapter<>(YearMonth.class, converter));
        gsonBuilder.registerTypeAdapter(Year.class, new GsonJava8TimeAdapter<>(Year.class, converter));
        gsonBuilder.registerTypeAdapter(ZoneOffset.class, new GsonJava8TimeAdapter<>(ZoneOffset.class, converter));

        // Default Date Serializer
        gsonBuilder.registerTypeAdapter(java.sql.Date.class, new GsonDefaultDateTypeAdapter<>(java.sql.Date.class, converter));
        gsonBuilder.registerTypeAdapter(Timestamp.class, new GsonDefaultDateTypeAdapter<>(Timestamp.class, converter));
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDefaultDateTypeAdapter<>(Date.class, converter));

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