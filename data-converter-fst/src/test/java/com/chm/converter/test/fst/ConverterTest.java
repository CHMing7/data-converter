package com.chm.converter.test.fst;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.fst.DefaultFstConverter;
import com.chm.converter.fst.serializers.DefaultDateSerializer;
import com.chm.converter.fst.serializers.Java8TimeSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class ConverterTest {

    DefaultFstConverter converter;

    FSTConfiguration conf;

    User user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(DefaultFstConverter.class);
        user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());
        conf = FSTConfiguration.getDefaultConfiguration();
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
    }

    @Test
    public void testOriginal() throws Exception {
        // new
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        userMap.put("user1", user);
        byte[] encode = converter.encode(userMap);
        // original
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FSTObjectOutput objectOutput = conf.getObjectOutput(byteArrayOutputStream);
        objectOutput.writeObject(userMap, userMap.getClass());
        objectOutput.flush();
        byte[] encode2 = byteArrayOutputStream.toByteArray();
        StaticLog.info("testUser:{}", StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:{}", StrUtil.str(encode2, "utf-8"));

        assertArrayEquals(encode, encode2);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encode);
        FSTObjectInput objectInput = conf.getObjectInput(byteArrayInputStream);
        TypeToken<HashMap<String, User>> typeToken = new TypeToken<HashMap<String, User>>() {
        };
        Map<String, User> newUserMap = (Map<String, User>) objectInput.readObject(typeToken.getRawType());
        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testUser() {
        byte[] encode = converter.encode(user);
        StaticLog.info("testUser:{}", StrUtil.str(encode, "utf-8"));

        User newUser = converter.convertToJavaObject(encode, User.class);

        assertEquals(user, newUser);
    }


    @Test
    public void testMap() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        byte[] encode = converter.encode(userMap);
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

        byte[] encode = converter.encode(userCollection);

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
        byte[] encode = converter.encode(userArray);
        StaticLog.info("testArray:{}", StrUtil.str(encode, "utf-8"));

        TypeToken<User[]> typeRef0 = new TypeToken<User[]>() {
        };

        User[] newUserArray = converter.convertToJavaObject(encode, typeRef0);

        assertArrayEquals(userArray, newUserArray);
    }


    @Test
    public void testEnum() {
        byte[] encode = converter.encode(Enum.ONE);
        StaticLog.info("testEnum:{}", StrUtil.str(encode, "utf-8"));

        Enum newEnum = converter.convertToJavaObject(encode, Enum.class);

        assertEquals(Enum.ONE, newEnum);
    }

    public enum Enum {
        @FieldProperty(name = "testOne")
        ONE, TWO
    }
}