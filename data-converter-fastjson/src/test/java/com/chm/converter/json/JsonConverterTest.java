package com.chm.converter.json;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class JsonConverterTest {

    @Test
    public void testFastjson() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());
        userMap.put("user", user);

        FastjsonConverter jsonConverter = (FastjsonConverter) JsonConverterSelector.select(FastjsonConverter.class);
        // jsonConverter.addSerializerFeature(SerializerFeature.WriteMapNullValue);
        String encodeToString = jsonConverter.encodeToString(userMap);

        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };
        Map<String, User> newUserMap = jsonConverter.convertToJavaObject(encodeToString, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }

    @Test
    public void main() {
        String dateStr = "2019-12-12 12:12:12";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateTimeFormatter.parse(dateStr, YearMonth::from));
        System.out.println(LocalDateTime.parse(dateStr, dateTimeFormatter));
    }
}