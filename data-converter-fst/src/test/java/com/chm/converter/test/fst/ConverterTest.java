package com.chm.converter.test.fst;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.utils.DateUtil;
import com.chm.converter.fst.DefaultFstConverter;
import com.chm.converter.fst.serializers.DefaultDateSerializer;
import com.chm.converter.fst.serializers.Java8TimeSerializer;
import org.junit.jupiter.api.Test;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;

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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class ConverterTest {

    @Test
    public void testConverter() throws Exception {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(DateUtil.parseToDate("2022-01-20 15:50:50.6670", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
        user.setLocalDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        user.setYearMonth(YearMonth.now());
        userMap.put("user", user);
        userMap.put("user1", user);

        DefaultFstConverter fstConverter = (DefaultFstConverter) ConverterSelector.select(DataType.FST, DefaultFstConverter.class);
        byte[] encode = fstConverter.encode(userMap);

        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };

        FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();
        conf.registerSerializer(java.sql.Date.class, new DefaultDateSerializer<>(fstConverter), false);
        conf.registerSerializer(Timestamp.class, new DefaultDateSerializer<>(fstConverter), false);
        conf.registerSerializer(Date.class, new DefaultDateSerializer<>(fstConverter), false);

        // Java8 Time Serializer
        conf.registerSerializer(Instant.class, new Java8TimeSerializer<>(Instant.class, fstConverter), false);
        conf.registerSerializer(LocalDate.class, new Java8TimeSerializer<>(LocalDate.class, fstConverter), false);
        conf.registerSerializer(LocalDateTime.class, new Java8TimeSerializer<>(LocalDateTime.class, fstConverter), false);
        conf.registerSerializer(LocalTime.class, new Java8TimeSerializer<>(LocalTime.class, fstConverter), false);
        conf.registerSerializer(OffsetDateTime.class, new Java8TimeSerializer<>(OffsetDateTime.class, fstConverter), false);
        conf.registerSerializer(OffsetTime.class, new Java8TimeSerializer<>(OffsetTime.class, fstConverter), false);
        conf.registerSerializer(ZonedDateTime.class, new Java8TimeSerializer<>(ZonedDateTime.class, fstConverter), false);
        conf.registerSerializer(MonthDay.class, new Java8TimeSerializer<>(MonthDay.class, fstConverter), false);
        conf.registerSerializer(YearMonth.class, new Java8TimeSerializer<>(YearMonth.class, fstConverter), false);
        conf.registerSerializer(Year.class, new Java8TimeSerializer<>(Year.class, fstConverter), false);
        conf.registerSerializer(ZoneOffset.class, new Java8TimeSerializer<>(ZoneOffset.class, fstConverter), false);
        FSTObjectInput objectInput = conf.getObjectInput(encode);

        Map<String, User> newUserMap = (Map<String, User>) objectInput.readObject(HashMap.class);

        assertEquals(userMap, newUserMap);
    }
}