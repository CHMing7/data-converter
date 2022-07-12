package com.chm.converter.test.json.fastjson2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.fastjson2.Fastjson2DefaultDateCodec;
import com.chm.converter.fastjson2.Fastjson2Jdk8DateCodec;
import com.chm.converter.json.JsonConverter;
import com.chm.converter.json.fastjson2.Fastjson2Converter;
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

    JsonConverter converter;

    ObjectWriterProvider writerProvider;

    ObjectReaderProvider readerProvider;

    User user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(Fastjson2Converter.class);
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
        writerProvider = new ObjectWriterProvider();
        readerProvider = new ObjectReaderProvider();
        // Java8 Time Serializer
        writerProvider.register(Instant.class, new Fastjson2Jdk8DateCodec<>(Instant.class, converter));
        writerProvider.register(LocalDate.class, new Fastjson2Jdk8DateCodec<>(LocalDate.class, converter));
        writerProvider.register(LocalDateTime.class, new Fastjson2Jdk8DateCodec<>(LocalDateTime.class, converter));
        writerProvider.register(LocalTime.class, new Fastjson2Jdk8DateCodec<>(LocalTime.class, converter));
        writerProvider.register(OffsetDateTime.class, new Fastjson2Jdk8DateCodec<>(OffsetDateTime.class, converter));
        writerProvider.register(OffsetTime.class, new Fastjson2Jdk8DateCodec<>(OffsetTime.class, converter));
        writerProvider.register(ZonedDateTime.class, new Fastjson2Jdk8DateCodec<>(ZonedDateTime.class, converter));
        writerProvider.register(MonthDay.class, new Fastjson2Jdk8DateCodec<>(MonthDay.class, converter));
        writerProvider.register(YearMonth.class, new Fastjson2Jdk8DateCodec<>(YearMonth.class, converter));
        writerProvider.register(Year.class, new Fastjson2Jdk8DateCodec<>(Year.class, converter));
        writerProvider.register(ZoneOffset.class, new Fastjson2Jdk8DateCodec<>(ZoneOffset.class, converter));

        // Default Date Serializer
        writerProvider.register(java.sql.Date.class, new Fastjson2DefaultDateCodec<>(java.sql.Date.class, converter));
        writerProvider.register(Timestamp.class, new Fastjson2DefaultDateCodec<>(Timestamp.class, converter));
        writerProvider.register(Date.class, new Fastjson2DefaultDateCodec<>(Date.class, converter));

        // Java8 Time Serializer
        readerProvider.register(Instant.class, new Fastjson2Jdk8DateCodec<>(Instant.class, converter));
        readerProvider.register(LocalDate.class, new Fastjson2Jdk8DateCodec<>(LocalDate.class, converter));
        // readerProvider.register(LocalDateTime.class, new Fastjson2Jdk8DateCodec<>(LocalDateTime.class, converter));
        readerProvider.register(LocalTime.class, new Fastjson2Jdk8DateCodec<>(LocalTime.class, converter));
        readerProvider.register(OffsetDateTime.class, new Fastjson2Jdk8DateCodec<>(OffsetDateTime.class, converter));
        readerProvider.register(OffsetTime.class, new Fastjson2Jdk8DateCodec<>(OffsetTime.class, converter));
        readerProvider.register(ZonedDateTime.class, new Fastjson2Jdk8DateCodec<>(ZonedDateTime.class, converter));
        readerProvider.register(MonthDay.class, new Fastjson2Jdk8DateCodec<>(MonthDay.class, converter));
        readerProvider.register(YearMonth.class, new Fastjson2Jdk8DateCodec<>(YearMonth.class, converter));
        readerProvider.register(Year.class, new Fastjson2Jdk8DateCodec<>(Year.class, converter));
        readerProvider.register(ZoneOffset.class, new Fastjson2Jdk8DateCodec<>(ZoneOffset.class, converter));

        // Default Date Serializer
        readerProvider.register(java.sql.Date.class, new Fastjson2DefaultDateCodec<>(java.sql.Date.class, converter));
        readerProvider.register(Timestamp.class, new Fastjson2DefaultDateCodec<>(Timestamp.class, converter));
        readerProvider.register(Date.class, new Fastjson2DefaultDateCodec<>(Date.class, converter));

    }

    @Test
    public void testOriginal() throws IOException {
        // new
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        userMap.put("user1", user);
        String encode = converter.encode(userMap);
        // original
        String encode2;
        JSONWriter.Context writeContext = new JSONWriter.Context(writerProvider);
        try (JSONWriter writer = JSONWriter.of(writeContext)) {
            Class<?> valueClass = userMap.getClass();
            ObjectWriter<?> objectWriter = writeContext.getProvider().getObjectWriter(valueClass, valueClass);
            objectWriter.write(writer, userMap, null, null, 0);
            encode2 = writer.toString();
        } catch (NullPointerException | NumberFormatException ex) {
            throw new JSONException("toJSONString error", ex);
        }
        StaticLog.info("testUser:{}", StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:{}", StrUtil.str(encode2, "utf-8"));

        assertEquals(encode, encode2);
        Map<String, User> newUserMap;
        if (encode == null || encode.isEmpty()) {
            newUserMap = null;
        } else {
            JSONReader.Context readContext = new JSONReader.Context(readerProvider);
            try (JSONReader reader = JSONReader.of(readContext, encode)) {
                ObjectReader objectReader = readContext.getProvider().getObjectReader(new TypeToken<Map<String, User>>() {
                }.getType());

                Object object = objectReader.readObject(reader, 0);

                newUserMap = (Map<String, User>) object;
            }
        }
        StaticLog.info("equals:{}", StrUtil.str(userMap.equals(newUserMap), "utf-8"));

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