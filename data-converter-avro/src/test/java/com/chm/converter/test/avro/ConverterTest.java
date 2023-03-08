package com.chm.converter.test.avro;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.avro.DefaultAvroConverter;
import com.chm.converter.avro.factorys.CoreCodecConversion;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.chm.converter.core.codecs.Java8TimeCodec;
import com.chm.converter.core.reflect.TypeToken;
import org.apache.avro.Conversion;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    DefaultAvroConverter converter;

    ReflectData reflectData;

    User user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(DefaultAvroConverter.class);
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

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(Instant.class),
                Java8TimeCodec.INSTANT_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                Instant.class.getName()));

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(LocalDate.class),
                Java8TimeCodec.LOCAL_DATE_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                LocalDate.class.getName()));

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(LocalDateTime.class),
                Java8TimeCodec.LOCAL_DATE_TIME_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                LocalDateTime.class.getName()));

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(LocalTime.class),
                Java8TimeCodec.LOCAL_TIME_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                LocalTime.class.getName()));

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(OffsetDateTime.class),
                Java8TimeCodec.OFFSET_DATE_TIME_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                OffsetDateTime.class.getName()));

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(OffsetTime.class),
                Java8TimeCodec.OFFSET_TIME_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                OffsetTime.class.getName()));

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(ZonedDateTime.class),
                Java8TimeCodec.ZONED_DATE_TIME_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                ZonedDateTime.class.getName()));

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(MonthDay.class),
                Java8TimeCodec.MONTH_DAY_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                MonthDay.class.getName()));

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(YearMonth.class),
                Java8TimeCodec.YEAR_MONTH_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                YearMonth.class.getName()));

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(Year.class),
                Java8TimeCodec.YEAR_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                Year.class.getName()));

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(ZoneOffset.class),
                Java8TimeCodec.ZONE_OFFSET_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                ZoneOffset.class.getName()));

        // DefaultDate Conversion
        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(Date.class),
                DefaultDateCodec.DATE_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                Date.class.getName()));

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(Timestamp.class),
                DefaultDateCodec.TIMESTAMP_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                Timestamp.class.getName()));

        reflectData.addLogicalTypeConversion(new CoreCodecConversion<>(converter, TypeToken.get(java.sql.Date.class),
                DefaultDateCodec.SQL_DATE_CODEC.withConverter(converter), Schema.create(Schema.Type.STRING),
                java.sql.Date.class.getName()));
    }

    @Test
    public void testOriginal() throws IOException {
        // new
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        userMap.put("user1", user);
        byte[] encode = converter.encode(userMap);
        // original
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(os, null);
        // 获取Schema
        Conversion<?> conversion = reflectData.getConversionByClass(userMap.getClass());
        Schema schema = conversion != null ? conversion.getRecommendedSchema() : reflectData.induce(userMap);
        ReflectDatumWriter dd = new ReflectDatumWriter(schema, reflectData);
        dd.write(userMap, encoder);
        encoder.flush();
        byte[] encode2 = os.toByteArray();
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:" + StrUtil.str(encode2, "utf-8"));

        assertArrayEquals(encode, encode2);

        InputStream in = new ByteArrayInputStream(encode);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(in, null);
        ReflectDatumReader reader = new ReflectDatumReader<>(schema, schema, reflectData);
        Map<String, User> newUserMap = (Map<String, User>) reader.read(null, decoder);
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