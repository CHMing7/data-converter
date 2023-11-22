package com.chm.converter.test.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.Converter;
import com.chm.converter.core.DataArray;
import com.chm.converter.core.DataMapper;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.chm.converter.core.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class ConverterTest {

    Converter<?> converter;

    User user;

    @BeforeEach
    public void before() {
        converter = Converter.DEFAULT;
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
    }


    @Test
    public void testMapper() {
        // new
        Map<String, Object> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        userMap.put("user1", user);
        Map<String, Object> newUserMap = MapUtil.newHashMap(true);
        newUserMap.put("u", user);
        userMap.put("map", newUserMap);

        DataMapper dataMapper = converter.toMapper(userMap);
        StaticLog.info("newDataMapper:{}", StrUtil.str(dataMapper, "utf-8"));

        Map<String, Object> newUserMap2 = dataMapper.toJavaBean(new TypeToken<Map<String, Object>>() {
        });
        assertEquals(userMap, newUserMap2);

        DataMapper userDataMapper = dataMapper.getObject("user", DataMapper.class);
        userDataMapper.put("strList", null);
        StaticLog.info("userDataMapper:{}", StrUtil.str(userDataMapper, "utf-8"));
        User newUser = userDataMapper.toJavaBean(User.class);
        assertEquals(user, newUser);

        userDataMapper.put("id2", "PT24H");
        CharSequence str = userDataMapper.getObject("id2", CharSequence.class);
        StaticLog.info("str:{}", StrUtil.str(str, "utf-8"));
        assertEquals("PT24H", str);
    }


    @Test
    public void testDataArray() {
        Collection<User> userCollection = CollUtil.newArrayList();
        userCollection.add(user);
        userCollection.add(user);
        userCollection.add(user);

        DataArray dataArray = converter.toArray(userCollection);

        Collection newUserCollection = dataArray.toJavaList(User.class);
        assertEquals(userCollection, newUserCollection);


        DataMapper userDataMapper = dataArray.getObject(0, DataMapper.class);
        StaticLog.info("userDataMapper:{}", StrUtil.str(userDataMapper, "utf-8"));
        User newUser = userDataMapper.toJavaBean(User.class);
        assertEquals(user, newUser);


        User newUser2 = dataArray.getObject(0, User.class);
        StaticLog.info("newUser2:{}", StrUtil.str(newUser2, "utf-8"));
        assertEquals(user, newUser2);

        userDataMapper.put("id2", "PT24H");
        CharSequence str = userDataMapper.getObject("id2", CharSequence.class);
        StaticLog.info("str:{}", StrUtil.str(str, "utf-8"));
        assertEquals("PT24H", str);
    }

    @Test
    public void testLocalDateTime() {
        String str = "120304";
        System.out.println(Java8TimeCodec.LOCAL_DATE_TIME_CODEC.decode(str, "yyMMdd"));
    }
}