package com.chm.converter.json;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    public void testJackson() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        user.setUserName("user");
        user.setPassword("password");
        //user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());
        userMap.put("user", user);


        JacksonConverter jsonConverter = (JacksonConverter) JsonConverterSelector.select(JacksonConverter.class);
        jsonConverter.getMapper().registerModule(new JavaTimeModule());
        jsonConverter.setDateFormat("yyyy-MM-dd HH:mm");
        String encodeToString = jsonConverter.encodeToString(userMap);
        String newEncodeToString = "{\"user\":{\"userName1\":\"user\",\"password2\":\"password\",\"localDateTime\":\"2019-12-12 12:12:12\",\"yearMonth\":\"2033-09\"}}";
        // assertEquals(encodeToString, "{\"user\":{\"userName1\":\"user\",\"password2\":\"password\"}}");
        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };
        Map<String, User> newUserMap = jsonConverter.convertToJavaObject(newEncodeToString, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testGson() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        user.setUserName("user");
        user.setPassword("password");
        //user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        userMap.put("user", user);
        GsonConverter jsonConverter = (GsonConverter) JsonConverterSelector.select(GsonConverter.class);
        //jsonConverter.setDateFormat("yyyy-MM-dd HH:mm");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();


        String encodeToString = jsonConverter.encodeToString(userMap);
        // String encodeToString = gson.toJson(userMap);
        //assertEquals(encodeToString, "{\"user\":{\"userName1\":\"user\",\"password2\":\"password\"}}");
        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };
        Map<String, User> newUserMap = jsonConverter.convertToJavaObject(encodeToString, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testFastjson() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        user.setUserName("user");
        user.setPassword("password");
        user.setLocalDateTime(LocalDateTime.now());
        userMap.put("user", user);

        JsonConverter jsonConverter = JsonConverterSelector.select(FastjsonConverter.class);
        String encodeToString = jsonConverter.encodeToString(userMap);
        String newEncodeToString = "{\"user\":{\"userName1\":\"user\",\"password2\":\"password\",\"localDateTime\":\"2019-12-12\"}}";
       // assertEquals(encodeToString, "{\"user\":{\"userName1\":\"user\",\"password2\":\"password\"}}");
        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };
        Map<String, User> newUserMap = jsonConverter.convertToJavaObject(newEncodeToString, typeRef0.getType());

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