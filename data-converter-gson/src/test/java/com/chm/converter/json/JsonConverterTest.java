package com.chm.converter.json;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
    public void testGson() {
        Map<User, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
       // user.setYearMonth(YearMonth.now());
        userMap.put(user, user);

        GsonConverter gsonConverter = (GsonConverter) ConverterSelector.select(DataType.JSON, GsonConverter.class);

        String encodeToString = gsonConverter.encodeToString(userMap);

        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };

        Map<String, User> newUserMap = gsonConverter.convertToJavaObject(encodeToString, typeRef0.getType());

        assertEquals(userMap, newUserMap);

        Gson gson = new Gson();

        String newEncodeToString = gson.toJson(userMap);

        Map<String, User> newUserMap1 = gson.fromJson(newEncodeToString, typeRef0.getType());

        assertEquals(userMap, newUserMap1);
    }
}