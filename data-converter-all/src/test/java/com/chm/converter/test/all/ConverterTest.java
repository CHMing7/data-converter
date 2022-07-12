package com.chm.converter.test.all;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.json.JsonConverter;
import com.chm.converter.json.fastjson2.Fastjson2Converter;
import com.chm.converter.jsonb.fastjson2.Fastjson2JsonbConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
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

    Converter converter;

    User user;

    @BeforeEach
    public void before() {
        user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());

        List<User> userList = ListUtil.list(true);
        userList.add(user1);
        user.setUserList(userList);

        Map<String, User> userMap = MapUtil.newHashMap();
        userMap.put("user3", user1);
        user.setUserMap(userMap);

        user.setOne(User.Enum.ONE);
    }


    private void testUser() {
        Object encode = converter.encode(user);
        StaticLog.info("testUser:{}", StrUtil.str(encode, "utf-8"));

        User newUser = (User) converter.convertToJavaObject(encode, User.class);

        assertEquals(user, newUser);
    }


    private void testMap() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("testMap", user);
        Object encode = converter.encode(userMap);
        StaticLog.info("testMap:{}", StrUtil.str(encode, "utf-8"));

        TypeToken<Map<String, User>> typeRef0 = new TypeToken<Map<String, User>>() {
        };

        Map<String, User> newUserMap = (Map<String, User>) converter.convertToJavaObject(encode, typeRef0);

        assertEquals(userMap, newUserMap);
    }

    private void testCollection() {
        Collection<User> userCollection = CollUtil.newArrayList();
        userCollection.add(user);
        userCollection.add(user);
        userCollection.add(user);

        Object encode = converter.encode(userCollection);

        StaticLog.info("testCollection:{}", StrUtil.str(encode, "utf-8"));

        TypeToken<Collection<User>> typeRef0 = new TypeToken<Collection<User>>() {
        };

        Collection<User> newUserCollection = (Collection<User>) converter.convertToJavaObject(encode, typeRef0);

        assertEquals(userCollection, newUserCollection);
    }

    private void testArray() {
        User[] userArray = new User[3];
        userArray[0] = user;
        userArray[1] = user;
        userArray[2] = user;
        Object encode = converter.encode(userArray);
        StaticLog.info("testArray:{}", StrUtil.str(encode, "utf-8"));

        User[] newUserArray = (User[]) converter.convertToJavaObject(encode, User[].class);

        assertArrayEquals(userArray, newUserArray);
    }

    private void testEnum() {

        Object encode = converter.encode(Enum.ONE);
        StaticLog.info("testEnum:{}", StrUtil.str(encode, "utf-8"));

        Enum newEnum = (Enum) converter.convertToJavaObject(encode, Enum.class);

        assertEquals(Enum.ONE, newEnum);
    }

    public enum Enum {
        @FieldProperty(name = "testOne")
        ONE, TWO
    }

    @Test
    public void testAny() {
        ConverterTest converterTest = new ConverterTest();
        converterTest.before();
        this.converter = ConverterSelector.select(Fastjson2Converter.class);
        // converter.disable(ConvertFeature.ENUMS_USING_TO_STRING);
        StaticLog.info(this.converter.getConverterName());
        this.testUser();
        /*this.testMap();
        this.testCollection();
        this.testArray();
        this.testEnum();*/
    }

    @Test
    public void testAll() {
        ConverterTest converterTest = new ConverterTest();
        converterTest.before();
        List<DataType> dateTypeList = ConverterSelector.getDateTypeList();
        for (DataType dataType : dateTypeList) {
            List<Converter> converterList = ConverterSelector.getConverterListByDateType(dataType);
            for (Converter converter : converterList) {
                this.converter = converter;
                StaticLog.info(this.converter.getConverterName());
                this.testUser();
                this.testMap();
                this.testCollection();
                this.testArray();
                this.testEnum();
            }
        }
    }
}