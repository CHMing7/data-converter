package com.chm.converter.test.thrift;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.utils.DateUtil;
import com.chm.converter.thrift.DefaultThriftConverter;
import org.apache.thrift.TBase;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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

    Converter<byte[]> converter;

    User user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(DataType.THRIFT_BINARY, DefaultThriftConverter.class);
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
    public void testOriginal() throws Exception {
        // new

        byte[] encode = converter.encode(user);
        // original
        TestUser testUser = new TestUser();
        TestUser user1 = new TestUser();
        user1.setUserName("testName");
        testUser.setUser(user1);
        testUser.setUserName(user.userName);
        testUser.setPassword(user.password);
        testUser.setDate(DateUtil.format(user.date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
        testUser.setLocalDateTime(user.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
        testUser.setYearMonth(user.getYearMonth().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TTransport transport = new TIOStreamTransport(out);
        TProtocol oprot = new TBinaryProtocol(transport);
        testUser.write(oprot);
        byte[] encode2 = out.toByteArray();
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:" + StrUtil.str(encode2, "utf-8"));

        assertArrayEquals(encode, encode2);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encode);
        TBase instance = ConstructorFactory.INSTANCE.get(TestUser.class).construct();
        TTransport transport2 = new TIOStreamTransport(byteArrayInputStream);
        TProtocol oprot2 = new TBinaryProtocol(transport2);
        instance.read(oprot2);
        assertEquals(testUser, instance);
    }


    @Test
    public void testUser() {
        TestUser testUser = new TestUser();
        testUser.setUserName("user");
        testUser.setPassword("password");
        testUser.setDate(DateUtil.format(user.date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
        testUser.setLocalDateTime(user.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
        testUser.setYearMonth(user.getYearMonth().format(DateTimeFormatter.ofPattern("yyyy-MM")));

        byte[] encode1 = converter.encode(user);
        byte[] encode2 = converter.encode(testUser);

        assertArrayEquals(encode1, encode2);
        TestUser newUser = converter.convertToJavaObject(encode1, TestUser.class);
        assertEquals(testUser, newUser);
    }


    @Test
    public void testMap() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        byte[] encode = converter.encode(userMap);

        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };

        Map<String, User> newUserMap = converter.convertToJavaObject(encode, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testCollection() throws IOException {
        Collection<User> userCollection = CollUtil.newArrayList();
        userCollection.add(user);
        userCollection.add(user);
        userCollection.add(user);

        byte[] encode = converter.encode(userCollection);

        TypeReference<Collection<User>> typeRef0 = new TypeReference<Collection<User>>() {
        };

        Collection<User> newUserCollection = converter.convertToJavaObject(encode, typeRef0.getType());

        assertEquals(userCollection, newUserCollection);
    }


    @Test
    public void testArray() throws IOException {
        User[] userArray = new User[3];
        userArray[0] = user;
        userArray[1] = user;
        userArray[2] = user;
        byte[] encode = converter.encode(userArray);

        TypeReference<User[]> typeRef0 = new TypeReference<User[]>() {
        };

        User[] newUserArray = converter.convertToJavaObject(encode, typeRef0.getType());

        assertArrayEquals(userArray, newUserArray);
    }


    @Test
    public void testEnum() throws IOException {

        byte[] encode = converter.encode(Enum.ONE);

        Enum newEnum = converter.convertToJavaObject(encode, Enum.class);

        assertEquals(Enum.ONE, newEnum);
    }

    public enum Enum {
        @FieldProperty(name = "testOne")
        ONE("one"),

        @FieldProperty(name = "testTwo")
        TWO("two");

        private String name;

        Enum(String name) {
            this.name = name;
        }
    }
}