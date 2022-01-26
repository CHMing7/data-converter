package com.chm.converter.test.avro;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.avro.DefaultAvroConverter;
import com.chm.converter.avro.factorys.AvroDefaultDateConversion;
import com.chm.converter.avro.factorys.AvroJava8TimeConversion;
import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.annotation.FieldProperty;
import org.apache.avro.Conversion;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
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

    Converter converter;

    ReflectData reflectData;

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
        reflectData = ReflectData.AllowNull.get();
        // java8Time Conversion
        reflectData.addLogicalTypeConversion(new AvroJava8TimeConversion<>(Instant.class, converter));
        reflectData.addLogicalTypeConversion(new AvroJava8TimeConversion<>(LocalDate.class, converter));
        reflectData.addLogicalTypeConversion(new AvroJava8TimeConversion<>(LocalDateTime.class, converter));
        reflectData.addLogicalTypeConversion(new AvroJava8TimeConversion<>(LocalTime.class, converter));
        reflectData.addLogicalTypeConversion(new AvroJava8TimeConversion<>(OffsetDateTime.class, converter));
        reflectData.addLogicalTypeConversion(new AvroJava8TimeConversion<>(OffsetTime.class, converter));
        reflectData.addLogicalTypeConversion(new AvroJava8TimeConversion<>(ZonedDateTime.class, converter));
        reflectData.addLogicalTypeConversion(new AvroJava8TimeConversion<>(MonthDay.class, converter));
        reflectData.addLogicalTypeConversion(new AvroJava8TimeConversion<>(YearMonth.class, converter));
        reflectData.addLogicalTypeConversion(new AvroJava8TimeConversion<>(Year.class, converter));
        reflectData.addLogicalTypeConversion(new AvroJava8TimeConversion<>(ZoneOffset.class, converter));

        // DefaultDate Conversion
        reflectData.addLogicalTypeConversion(new AvroDefaultDateConversion<>(java.sql.Date.class, converter));
        reflectData.addLogicalTypeConversion(new AvroDefaultDateConversion<>(Timestamp.class, converter));
        reflectData.addLogicalTypeConversion(new AvroDefaultDateConversion<>(Date.class, converter));
    }

    @Test
    public void testOriginal() throws IOException {
        // new
        byte[] encode = (byte[]) converter.encode(user);
        // original
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(os, null);
        // 获取Schema
        Conversion<?> conversion = reflectData.getConversionByClass(user.getClass());
        Schema schema = conversion != null ? conversion.getRecommendedSchema() : reflectData.induce(user);
        ReflectDatumWriter dd = new ReflectDatumWriter(schema, reflectData);
        dd.write(user, encoder);
        encoder.flush();
        byte[] encode2 = os.toByteArray();
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:" + StrUtil.str(os.toByteArray(), "utf-8"));

        assertArrayEquals(encode, encode2);
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