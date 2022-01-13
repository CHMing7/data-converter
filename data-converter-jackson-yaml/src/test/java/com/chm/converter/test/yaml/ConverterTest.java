package com.chm.converter.test.yaml;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.yaml.JacksonYamlConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;
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
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());
        userMap.put("user", user);
        userMap.put("user1", user);

        JacksonYamlConverter converter = (JacksonYamlConverter) ConverterSelector.select(DataType.YAML, JacksonYamlConverter.class);
        String encode = converter.encode(userMap);
        StaticLog.info(encode);

        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };

        Map<String, User> newUserMap = converter.convertToJavaObject(encode, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testMapJacksonYaml() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());
        userMap.put("user", user);

        JacksonYamlConverter converter = (JacksonYamlConverter) ConverterSelector.select(DataType.YAML, JacksonYamlConverter.class);
        // xmlConverter.getMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // jsonConverter.addSerializerFeature(SerializerFeature.WriteMapNullValue);
        String encodeToString = converter.encode(userMap);
        StaticLog.info(encodeToString);

        TypeToken<Map<String, User>> typeRef0 = new TypeToken<Map<String, User>>() {
        };

        Map<String, User> newUserMap = converter.convertToJavaObject(encodeToString, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testListJdom() {
        List<User> userList = ListUtil.list(true);
        User user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());
        userList.add(user);
        userList.add(user);
        userList.add(user);
        userList.add(user);
        JacksonYamlConverter converter = (JacksonYamlConverter) ConverterSelector.select(DataType.YAML, JacksonYamlConverter.class);
        // jsonConverter.addSerializerFeature(SerializerFeature.WriteMapNullValue);
        String encodeToString = converter.encode(userList);
        StaticLog.info(encodeToString);

        TypeToken<List<User>> typeRef0 = new TypeToken<List<User>>() {
        };

        List<User> newUserList = converter.convertToJavaObject(encodeToString, typeRef0.getType());

        assertEquals(userList, newUserList);
    }

    @Test
    public void testMapper() throws JsonProcessingException {
        List<User> userList = ListUtil.list(true);
        User user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        //user.setLocalDateTime(LocalDateTime.now());
        //user.setYearMonth(YearMonth.now());
        userList.add(user);
        userList.add(user);
        userList.add(user);
        userList.add(user);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        StaticLog.info(mapper.writeValueAsString(userList));
    }
}