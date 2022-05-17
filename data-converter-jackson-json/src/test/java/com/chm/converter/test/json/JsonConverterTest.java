package com.chm.converter.test.json;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.json.JacksonConverter;
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
public class JsonConverterTest {

    @Test
    public void testJackson() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());
        userMap.put("user", user);


        JacksonConverter jsonConverter = ConverterSelector.select(DataType.JSON, JacksonConverter.class);
        // jsonConverter.getMapper().registerModule(new JavaTimeModule());
        // jsonConverter.setDateFormat("yyyy-MM-dd HH:mm");
        String encodeToString = jsonConverter.encode(userMap);
        // assertEquals(encodeToString, "{\"user\":{\"userName1\":\"user\",\"password2\":\"password\"}}");
        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };
        Map<String, User> newUserMap = jsonConverter.convertToJavaObject(encodeToString, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }
}