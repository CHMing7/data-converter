package com.chm.converter.xml;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.reflect.TypeToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
public class XmlConverterTest {

    @Test
    public void testMapJacksonXml() {
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

        JacksonXmlConverter xmlConverter = (JacksonXmlConverter) ConverterSelector.select(DataType.XML, JacksonXmlConverter.class);
        // xmlConverter.getMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // jsonConverter.addSerializerFeature(SerializerFeature.WriteMapNullValue);
        xmlConverter.getMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        xmlConverter.getMapper().configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        String encodeToString = xmlConverter.encode(userMap);

        TypeToken<Map<String, User>> typeRef0 = new TypeToken<Map<String, User>>() {
        };

        Map<String, User> newUserMap = xmlConverter.convertToJavaObject(encodeToString, typeRef0.getType());

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
        JacksonXmlConverter xmlConverter = (JacksonXmlConverter) ConverterSelector.select(DataType.XML, JacksonXmlConverter.class);
        // jsonConverter.addSerializerFeature(SerializerFeature.WriteMapNullValue);
        String encodeToString = xmlConverter.encode(userList);

        TypeToken<List<User>> typeRef0 = new TypeToken<List<User>>() {
        };

        List<User> newUserList = xmlConverter.convertToJavaObject(encodeToString, List.class);

        assertEquals(userList, newUserList);
    }

    @Test
    public void testXmlMapper() throws JsonProcessingException {
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
        ObjectMapper objectMapper = new XmlMapper();
        StaticLog.info(objectMapper.writeValueAsString(userList));
    }

}