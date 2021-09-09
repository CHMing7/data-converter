package com.chm.converter.json;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.log.StaticLog;
import com.chm.converter.xml.JaxbConverter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-01
 **/
public class XmlTest {

    @Test
    public void testXml(){
        System.out.println(Integer.class.isAssignableFrom(Number.class));
        System.out.println(int.class.isAssignableFrom(Number.class));
        System.out.println(Number.class.isAssignableFrom(Integer.class));
        System.out.println(Number.class.isAssignableFrom(int.class));
        Map<String, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        user.setUserName("user");
        user.setPassword("password");
        //user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());
        userMap.put("user", user);

        User[] users = new User[4];
        users[0] = user;
        users[1] = user;
        users[2] = user;
        users[3] = user;

        JaxbConverter jsonConverter = new JaxbConverter();
        String encodeToString = jsonConverter.encodeToString(user);
        StaticLog.info(encodeToString);
        StaticLog.info(XmlUtil.toStr(XmlUtil.beanToXml(users)));
        // assertEquals(encodeToString, "{\"user\":{\"userName1\":\"user\",\"password2\":\"password\"}}");
        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };
        User newUserMap = jsonConverter.convertToJavaObject(encodeToString, User.class);

        assertEquals(user, newUserMap);

    }
}
