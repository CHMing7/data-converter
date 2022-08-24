package com.chm.converter.test.kryo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.kryo.DefaultKryoConverter;
import com.chm.converter.kryo.serializers.KryoCoreCodecSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    DefaultKryoConverter converter;

    Kryo kryo;

    User user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(DefaultKryoConverter.class);
        user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());

        kryo = new Kryo();

        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);

        kryo.addDefaultSerializer(Throwable.class, new JavaSerializer());

        // Java8 Time Serializer
        Serializer<String> stringSerializer = new DefaultSerializers.StringSerializer();
        kryo.addDefaultSerializer(Instant.class, new KryoCoreCodecSerializer<>(Java8TimeCodec.INSTANT_CODEC.withConverter(converter), stringSerializer));
        kryo.addDefaultSerializer(LocalDate.class, new KryoCoreCodecSerializer<>(Java8TimeCodec.LOCAL_DATE_CODEC.withConverter(converter), stringSerializer));
        kryo.addDefaultSerializer(LocalDateTime.class, new KryoCoreCodecSerializer<>(Java8TimeCodec.LOCAL_DATE_TIME_CODEC.withConverter(converter), stringSerializer));
        kryo.addDefaultSerializer(LocalTime.class, new KryoCoreCodecSerializer<>(Java8TimeCodec.LOCAL_TIME_CODEC.withConverter(converter), stringSerializer));
        kryo.addDefaultSerializer(OffsetDateTime.class, new KryoCoreCodecSerializer<>(Java8TimeCodec.OFFSET_DATE_TIME_CODEC.withConverter(converter), stringSerializer));
        kryo.addDefaultSerializer(OffsetTime.class, new KryoCoreCodecSerializer<>(Java8TimeCodec.OFFSET_TIME_CODEC.withConverter(converter), stringSerializer));
        kryo.addDefaultSerializer(ZonedDateTime.class, new KryoCoreCodecSerializer<>(Java8TimeCodec.ZONED_DATE_TIME_CODEC.withConverter(converter), stringSerializer));
        kryo.addDefaultSerializer(MonthDay.class, new KryoCoreCodecSerializer<>(Java8TimeCodec.MONTH_DAY_CODEC.withConverter(converter), stringSerializer));
        kryo.addDefaultSerializer(YearMonth.class, new KryoCoreCodecSerializer<>(Java8TimeCodec.YEAR_MONTH_CODEC.withConverter(converter), stringSerializer));
        kryo.addDefaultSerializer(Year.class, new KryoCoreCodecSerializer<>(Java8TimeCodec.YEAR_CODEC.withConverter(converter), stringSerializer));
        kryo.addDefaultSerializer(ZoneOffset.class, new KryoCoreCodecSerializer<>(Java8TimeCodec.ZONE_OFFSET_CODEC.withConverter(converter), stringSerializer));

        // Default Date Serializer
        kryo.addDefaultSerializer(java.sql.Date.class, new KryoCoreCodecSerializer<>(DefaultDateCodec.SQL_DATE_CODEC.withConverter(converter), stringSerializer));
        kryo.addDefaultSerializer(Timestamp.class, new KryoCoreCodecSerializer<>(DefaultDateCodec.TIMESTAMP_CODEC.withConverter(converter), stringSerializer));
        kryo.addDefaultSerializer(Date.class, new KryoCoreCodecSerializer<>(DefaultDateCodec.DATE_CODEC.withConverter(converter), stringSerializer));
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
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObjectOrNull(output, userMap, userMap.getClass());
        output.close();
        byte[] encode2 = byteArrayOutputStream.toByteArray();
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:" + StrUtil.str(encode2, "utf-8"));

        assertArrayEquals(encode, encode2);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encode);
        Input input = new Input(byteArrayInputStream);
        input.close();
        TypeToken<HashMap<String, User>> typeToken = new TypeToken<HashMap<String, User>>() {
        };
        Map<String, User> newUserMap = (Map<String, User>) kryo.readObjectOrNull(input, typeToken.getRawType());
        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testUser() {
        byte[] encode = converter.encode(user);
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));

        User newUser = converter.convertToJavaObject(encode, User.class);

        assertEquals(user, newUser);
    }


    @Test
    public void testMap() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        byte[] encode = converter.encode(userMap);
        StaticLog.info("testMap:" + StrUtil.str(encode, "utf-8"));

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

        StaticLog.info("testCollection:" + StrUtil.str(encode, "utf-8"));

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
        StaticLog.info("testArray:" + StrUtil.str(encode, "utf-8"));

        TypeToken<User[]> typeRef0 = new TypeToken<User[]>() {
        };

        User[] newUserArray = converter.convertToJavaObject(encode, typeRef0);

        assertArrayEquals(userArray, newUserArray);
    }


    @Test
    public void testEnum() {
        byte[] encode = converter.encode(Enum.ONE);
        StaticLog.info("testEnum:" + StrUtil.str(encode, "utf-8"));

        Enum newEnum = converter.convertToJavaObject(encode, Enum.class);

        assertEquals(Enum.ONE, newEnum);
    }

    public enum Enum {
        @FieldProperty(name = "testOne")
        ONE, TWO
    }
}