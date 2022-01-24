package com.chm.converter.test.avro;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.avro.DefaultAvroConverter;
import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.annotation.FieldProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
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

    Converter converter;

    User user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(DataType.AVRO_BINARY, DefaultAvroConverter.class);
        user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());
    }


    @Test
    public void testUser() {
        Object encode = converter.encode(user);
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));

        User newUser = (User) converter.convertToJavaObject(encode, User.class);

        assertEquals(user, newUser);
    }


    @Test
    public void testMap() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        Object encode = converter.encode(userMap);
        StaticLog.info("testMap:" + StrUtil.str(encode, "utf-8"));

        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };

        Map<String, User> newUserMap = (Map<String, User>) converter.convertToJavaObject(encode, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testCollection() {
        Collection<User> userCollection = CollUtil.newArrayList();
        userCollection.add(user);
        userCollection.add(user);
        userCollection.add(user);

        Object encode = converter.encode(userCollection);

        StaticLog.info("testCollection:" + StrUtil.str(encode, "utf-8"));

        TypeReference<Collection<User>> typeRef0 = new TypeReference<Collection<User>>() {
        };

        Collection<User> newUserCollection = (Collection<User>) converter.convertToJavaObject(encode, typeRef0.getType());

        assertEquals(userCollection, newUserCollection);
    }


    @Test
    public void testArray() {
        User[] userArray = new User[3];
        userArray[0] = user;
        userArray[1] = user;
        userArray[2] = user;
        Object encode = converter.encode(userArray);
        StaticLog.info("testArray:" + StrUtil.str(encode, "utf-8"));

        TypeReference<User[]> typeRef0 = new TypeReference<User[]>() {
        };

        User[] newUserArray = (User[]) converter.convertToJavaObject(encode, typeRef0.getType());

        assertArrayEquals(userArray, newUserArray);
    }


    @Test
    public void testEnum() {

        Object encode = converter.encode(Enum.ONE);
        StaticLog.info("testEnum:" + StrUtil.str(encode, "utf-8"));

        Enum newEnum = (Enum) converter.convertToJavaObject(encode, Enum.class);

        assertEquals(Enum.ONE, newEnum);
    }

    public enum Enum {
        @FieldProperty(name = "testOne")
        ONE, TWO
    }
}