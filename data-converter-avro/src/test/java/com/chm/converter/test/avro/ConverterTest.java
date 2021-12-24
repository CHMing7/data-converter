package com.chm.converter.test.avro;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import com.chm.converter.avro.DefaultAvroConverter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class ConverterTest {

    @Test
    public void testConverter() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());
        userMap.put("user", user);
        userMap.put("user1", user);

        DefaultAvroConverter avroConverter = (DefaultAvroConverter) ConverterSelector.select(DataType.AVRO_BINARY, DefaultAvroConverter.class);
        byte[] encode = avroConverter.encode(userMap);

        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };

        Map<String, User> newUserMap = avroConverter.convertToJavaObject(encode, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }
}