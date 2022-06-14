package com.chm.converter.test.protostuff;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.DateUtil;
import com.chm.converter.protostuff.DefaultProtostuffConverter;
import io.protostuff.Input;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffOutput;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
public class ProtostuffConverterTest {

    DefaultProtostuffConverter converter;

    User user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(DefaultProtostuffConverter.class);
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
        TestUser user1 = new TestUser();
        user1.setUserName("testName");
        TestUser testUser = new TestUser();
        testUser.setUser(user1);
        testUser.setUserName(user.userName);
        testUser.setPassword(user.password);
        testUser.setDate(DateUtil.format(user.date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
        testUser.setLocalDateTime(user.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
        testUser.setYearMonth(user.getYearMonth().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        Schema<TestUser> userSchema = RuntimeSchema.getSchema(TestUser.class);
        ProtostuffOutput output = new ProtostuffOutput(LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        userSchema.writeTo(output, testUser);
        byte[] encode2 = output.toByteArray();
        StaticLog.info("testUser:{}", StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:{}", StrUtil.str(encode2, "utf-8"));

        assertArrayEquals(encode, encode2);

        Input input = new io.protostuff.ByteArrayInput(encode, true);
        TestUser newTestUser = ConstructorFactory.INSTANCE.get(TestUser.class).construct();
        userSchema.mergeFrom(input, newTestUser);
        assertEquals(testUser, newTestUser);
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

        Map<String, User> newUserMap = converter.convertToJavaObject(encode, typeRef0.getType());

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

        Collection<User> newUserCollection = converter.convertToJavaObject(encode, typeRef0.getType());

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

        User[] newUserArray = converter.convertToJavaObject(encode, typeRef0.getType());

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