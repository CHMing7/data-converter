package com.chm.converter.test.json;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.utils.StringUtil;
import com.chm.converter.json.FastjsonConverter;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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

        FastjsonConverter jsonConverter = (FastjsonConverter) ConverterSelector.select(DataType.JSON, FastjsonConverter.class);
        // jsonConverter.addSerializerFeature(SerializerFeature.WriteMapNullValue);
        String encodeToString = jsonConverter.encode(userMap);

        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };

        Map<String, User> newUserMap = jsonConverter.convertToJavaObject(encodeToString, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }

    @Test
    public void testList() {
        List<User> userList = CollUtil.newArrayList();
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

        FastjsonConverter jsonConverter = (FastjsonConverter) ConverterSelector.select(DataType.JSON, FastjsonConverter.class);
        String encodeToString = jsonConverter.encode(userList);

        List<User> newUserList = jsonConverter.convertToList(encodeToString, User.class);

        for (int i = 0; i < userList.size(); i++) {
            assertEquals(userList.get(i), newUserList.get(i));
        }
    }

}