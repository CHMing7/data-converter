package com.chm.converter.protobuf;

import com.chm.converter.core.ConverterSelector;
import com.chm.converter.core.DataType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class ProtobufConverterTest {

    @Test
    public void testProtobuf() {

        PersonOuterClass.Person person = PersonOuterClass.Person.newBuilder()
                .setEmail("23").setName("name").build();

        DefaultProtobufConverter protobufConverter = (DefaultProtobufConverter) ConverterSelector.select(DataType.PROTOBUF_BINARY, DefaultProtobufConverter.class);
        // jsonConverter.addSerializerFeature(SerializerFeature.WriteMapNullValue);
        byte[] bytes = person.toByteArray();
        byte[] encode = protobufConverter.encode(person);
        assertArrayEquals(encode, bytes);

        PersonOuterClass.Person newUserMap = protobufConverter.convertToJavaObject(encode, PersonOuterClass.Person.class);
        PersonOuterClass.Person newPerson = protobufConverter.convertToJavaObject(bytes, PersonOuterClass.Person.class);
        assertEquals(newUserMap, newPerson);
        assertEquals(person, newUserMap);
    }
}