package com.chm.converter.test.hessian;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.chm.converter.core.Converter;
import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.hessian.DefaultHessianConverter;
import com.chm.converter.hessian.factory.HessianDefaultDateConverterFactory;
import com.chm.converter.hessian.factory.HessianEnumConverterFactory;
import com.chm.converter.hessian.factory.HessianJava8TimeConverterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class ConverterTest {

    Converter converter;

    SerializerFactory serializerFactory;

    User user;

    @BeforeEach
    public void before() {
        converter = ConverterSelector.select(DataType.HESSIAN, DefaultHessianConverter.class);
        user = new User();
        User user1 = new User();
        user1.setUserName("testName");
        user.setUser(user1);
        user.setUserName("user");
        user.setPassword("password");
        // user.setDate(new Date());
        user.setLocalDateTime(LocalDateTime.now());
        user.setYearMonth(YearMonth.now());

        serializerFactory = new SerializerFactory();
        serializerFactory.addFactory(new HessianJava8TimeConverterFactory(converter));
        serializerFactory.addFactory(new HessianDefaultDateConverterFactory(converter));
        serializerFactory.addFactory(new HessianEnumConverterFactory(converter));

    }

    @Test
    public void testOriginal() throws Exception {
        // new
        Map<String, User> userMap = MapUtil.newHashMap(true);
        userMap.put("user", user);
        userMap.put("user1", user);
        byte[] encode = (byte[]) converter.encode(userMap);
        // original
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(os);
        ho.setSerializerFactory(serializerFactory);
        ho.writeObject(userMap);
        ho.flush();
        byte[] encode2 = os.toByteArray();
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));
        StaticLog.info("testUser2:" + StrUtil.str(encode2, "utf-8"));

        assertArrayEquals(encode, encode2);

        InputStream is = new ByteArrayInputStream(encode);
        Hessian2Input hi = new Hessian2Input(is);
        hi.setSerializerFactory(serializerFactory);
        TypeToken<HashMap<String, User>> typeToken = new TypeToken<HashMap<String, User>>() {
        };
        Map<String, User> newUserMap = (Map<String, User>) hi.readObject(typeToken.getRawType());
        assertEquals(userMap, newUserMap);
    }

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

        DefaultHessianConverter hessianConverter = (DefaultHessianConverter) ConverterSelector.select(DataType.HESSIAN, DefaultHessianConverter.class);
        byte[] encode = hessianConverter.encode(userMap);
        StaticLog.info("testUser:" + StrUtil.str(encode, "utf-8"));
        TypeReference<Map<String, User>> typeRef0 = new TypeReference<Map<String, User>>() {
        };

        Map<String, User> newUserMap = hessianConverter.convertToJavaObject(encode, typeRef0.getType());

        assertEquals(userMap, newUserMap);
    }
}