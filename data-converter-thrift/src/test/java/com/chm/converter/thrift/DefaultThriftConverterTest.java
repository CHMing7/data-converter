package com.chm.converter.thrift;

import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-10-01
 **/
public class DefaultThriftConverterTest {

    @Test
    public void testThrift() {

        User user = new User();
        user.setCode(1);
        user.setMessage("testMessage");
        DefaultThriftConverter protobufConverter = (DefaultThriftConverter) ConverterSelector.select(DataType.THRIFT, DefaultThriftConverter.class);
        // jsonConverter.addSerializerFeature(SerializerFeature.WriteMapNullValue);
        byte[] encode = protobufConverter.encode(user);

        User newUser = protobufConverter.convertToJavaObject(encode, User.class);
        assertEquals(user, newUser);
    }
}
