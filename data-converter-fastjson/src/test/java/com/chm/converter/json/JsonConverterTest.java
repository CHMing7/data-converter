package com.chm.converter.json;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
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
    public void testFastjson() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        // user.setYearMonth(YearMonth.now());
        userMap.put("user", user);

        FastjsonConverter jsonConverter = (FastjsonConverter) ConverterSelector.select(DataType.JSON, FastjsonConverter.class);
        // jsonConverter.addSerializerFeature(SerializerFeature.WriteMapNullValue);
        String encodeToString = jsonConverter.encode(userMap);

        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };

        Map<String, User> newUserMap = jsonConverter.convertToJavaObject(encodeToString, typeRef0.getType());

        assertEquals(userMap, newUserMap);

        String newEncodeToString = JSON.toJSONString(userMap);

        Map<String, User> newUserMap1 = JSON.parseObject(newEncodeToString, typeRef0.getType());

        assertEquals(userMap, newUserMap1);
    }

}