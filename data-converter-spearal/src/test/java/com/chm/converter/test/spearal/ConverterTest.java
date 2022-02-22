package com.chm.converter.test.spearal;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.spearal.DefaultSpearalConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spearal.DefaultSpearalFactory;
import org.spearal.SpearalDecoder;
import org.spearal.SpearalEncoder;
import org.spearal.SpearalFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.YearMonth;
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

    SpearalFactory factory;

    User user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(DataType.SPEARAL, DefaultSpearalConverter.class);
        user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());

        factory = new DefaultSpearalFactory();
        factory.getContext().configure(new DefaultDateCoder(Date.class, converter));
        factory.getContext().configure(new Java8TimeCoder(LocalDateTime.class, converter));
    }

    @Test
    public void testOriginal() throws Exception {
        // new
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        userMap.put("user1", user);
        byte[] encode = (byte[]) converter.encode(userMap);
        // original
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SpearalEncoder encoder = factory.newEncoder(baos);
        encoder.writeAny(userMap);
        byte[] encode2 = baos.toByteArray();
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:" + StrUtil.str(encode2, "utf-8"));

        assertArrayEquals(encode, encode2);

        ByteArrayInputStream bais = new ByteArrayInputStream(encode);
        SpearalDecoder decoder = factory.newDecoder(bais);
        TypeToken<Map<String, User>> typeToken = new TypeToken<Map<String, User>>() {
        };

        Map<String, User> newUserMap = decoder.readAny(typeToken.getType());

        assertEquals(userMap, newUserMap);
    }


    @Test
    public void testConverter() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());
        userMap.put("user", user);
        userMap.put("user1", user);

        DefaultSpearalConverter converter = (DefaultSpearalConverter) ConverterSelector.select(DataType.SPEARAL, DefaultSpearalConverter.class);
        byte[] encode = converter.encode(userMap);

        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };

        Map<String, User> newUserMap = converter.convertToJavaObject(encode, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }
}