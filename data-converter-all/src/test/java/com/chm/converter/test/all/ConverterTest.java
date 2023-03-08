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
import com.chm.converter.fst.DefaultFstConverter;
import com.chm.converter.hessian.DefaultHessianConverter;
import com.chm.converter.spearal.DefaultSpearalConverter;
import com.chm.converter.xml.JacksonXmlConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author caihongming
 * @version v1.0
 * @date 2021-06-03
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

    private void testIgnore() {
        TestIgnore testIgnore = new TestIgnore();
        testIgnore.setUserName("tetetname");
        testIgnore.setPassword("papapapasword");
        Object encode = converter.encode(testIgnore);
        StaticLog.info("testIgnore:{}", StrUtil.str(encode, "utf-8"));

        TestIgnore newTestIgnore = (TestIgnore) converter.convertToJavaObject(encode, TestIgnore.class);

        assertNull(newTestIgnore.userName);
        assertNull(newTestIgnore.password);
    }

    private void testRemainder() {
        Map<String, Object> userMap = MapUtil.newHashMap();
        userMap.put("userName3", "userName");
        userMap.put("password3", "password3");
        userMap.put("password2", "password");

        TestIgnore testIgnore = new TestIgnore();
        testIgnore.setUserName("userName");

        Object encode = converter.encode(userMap);
        StaticLog.info("testIgnore:{}", StrUtil.str(encode, "utf-8"));

        TestIgnore newTestIgnore = (TestIgnore) converter.convertToJavaObject(encode, TestIgnore.class);

        assertEquals(testIgnore, newTestIgnore);
    }

    private void testGeneric1() {
        if (converter instanceof DefaultFstConverter ||
                converter instanceof DefaultSpearalConverter ||
                converter instanceof DefaultHessianConverter ||
                converter instanceof JacksonXmlConverter) {
            // 某些协议序列化会带上类名信息，不适合比较
            return;
        }
        Generic<User> generic1 = new Generic<>();
        generic1.setUserName("userName");
        generic1.setData(user);

        GenericUser generic2 = new GenericUser();
        generic2.setUserName("userName");
        generic2.setData(user);

        Object encode1 = converter.encode(generic1);
        Object encode2 = converter.encode(generic2);
        StaticLog.info("testGeneric1:{}", StrUtil.str(encode1, "utf-8"));
        StaticLog.info("testGeneric1:{}", StrUtil.str(encode2, "utf-8"));

        if (encode1 instanceof byte[] && encode2 instanceof byte[]) {
            assertArrayEquals((byte[]) encode1, (byte[]) encode2);
        } else {
            assertEquals(encode1, encode2);
        }
    }

    private void testGeneric2() {
        Generic<User> generic = new Generic<>();
        generic.setUserName("userName");
        generic.setData(user);

        Object encode = converter.encode(generic);
        StaticLog.info("testGeneric2:{}", StrUtil.str(encode, "utf-8"));

        Generic<User> newGeneric = (Generic<User>) converter.convertToJavaObject(encode, new TypeToken<Generic<User>>() {
        });

        assertEquals(generic, newGeneric);
    }

    private void testGeneric3() {
        if (converter instanceof DefaultFstConverter ||
                converter instanceof DefaultSpearalConverter) {
            return;
        }
        GenericUser generic = new GenericUser();
        generic.setUserName("userName");
        generic.setData(user);

        Object encode = converter.encode(generic);
        StaticLog.info("testGeneric3:{}", StrUtil.str(encode, "utf-8"));

        Generic<User> newGeneric = (Generic<User>) converter.convertToJavaObject(encode, new TypeToken<Generic<User>>() {
        });

        assertEquals(generic.data, newGeneric.data);
        assertEquals(generic.userName, newGeneric.userName);
    }

    public static class GenericUser {

        private String userName;

        private User data;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public User getData() {
            return data;
        }

        public void setData(User data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Generic{" +
                    "userName='" + userName + '\'' +
                    ", date=" + data +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Generic<?> generic = (Generic<?>) o;

            if (!Objects.equals(userName, generic.userName)) return false;
            return Objects.equals(data, generic.data);
        }

        @Override
        public int hashCode() {
            int result = userName != null ? userName.hashCode() : 0;
            result = 31 * result + (data != null ? data.hashCode() : 0);
            return result;
        }
    }

    public static class Generic<T> {

        private String userName;

        private T data;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Generic{" +
                    "userName='" + userName + '\'' +
                    ", date=" + data +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Generic<?> generic = (Generic<?>) o;

            if (!Objects.equals(userName, generic.userName)) return false;
            return Objects.equals(data, generic.data);
        }

        @Override
        public int hashCode() {
            int result = userName != null ? userName.hashCode() : 0;
            result = 31 * result + (data != null ? data.hashCode() : 0);
            return result;
        }
    }

    @Test
    public void testAny() {
        ConverterTest converterTest = new ConverterTest();
        converterTest.before();
        this.converter = ConverterSelector.select(JacksonXmlConverter.class);
        // converter.disable(ConvertFeature.ENUMS_USING_TO_STRING);
        StaticLog.info(this.converter.getConverterName());
//        this.testUser();
//        this.testMap();
//        this.testCollection();
//        this.testArray();
//        this.testEnum();
//        this.testIgnore();
//        this.testRemainder();
        this.testGeneric1();
        this.testGeneric2();
        this.testGeneric3();
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
                StaticLog.info("Converter Name: {}", this.converter.getConverterName());
                this.testUser();
                this.testMap();
                this.testCollection();
                this.testArray();
                this.testEnum();
                this.testIgnore();
                this.testGeneric1();
                this.testGeneric2();
                this.testGeneric3();
            }
        }
    }

    public enum Enum {
        @FieldProperty(name = "testOne")
        ONE, TWO
    }

    public static class TestIgnore {

        /**
         * 用户名
         */
        @FieldProperty(name = "userName3", ordinal = 2, serialize = false)
        private String userName;

        /**
         * 用户名
         */
        @FieldProperty(name = "password2", ordinal = 3, deserialize = false)
        private String password;


        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "TestIgnore{" +
                    "userName='" + userName + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestIgnore that = (TestIgnore) o;

            if (!Objects.equals(userName, that.userName)) return false;
            return Objects.equals(password, that.password);
        }

        @Override
        public int hashCode() {
            int result = userName != null ? userName.hashCode() : 0;
            result = 31 * result + (password != null ? password.hashCode() : 0);
            return result;
        }
    }
}