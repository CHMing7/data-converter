package com.chm.converter.protostuff;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
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
public class ProtostuffConverterTest {

    @Test
    public void testProtostuff() {
        Map<String, User> userMap = MapUtil.newHashMap(true);
        User user = new User();
        user.setUserName("user");
        user.setPassword("password");
        user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        // user.setYearMonth(YearMonth.now());
        userMap.put("user", user);

        DefaultProtostuffConverter protobufConverter = (DefaultProtostuffConverter) ConverterSelector.select(DataType.PROTOSTUFF, DefaultProtostuffConverter.class);
        // jsonConverter.addSerializerFeature(SerializerFeature.WriteMapNullValue);
        byte[] encode = protobufConverter.encode(userMap);

        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };

        Map<String, User> newUserMap = protobufConverter.convertToJavaObject(encode, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }
}