package com.chm.converter.json.fastjson.deserializer;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.DefaultFieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.FieldInfo;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-19
 **/
public class FastjsonFieldDeserializer extends DefaultFieldDeserializer {

    public FastjsonFieldDeserializer(ParserConfig config, Class<?> clazz, FieldInfo fieldInfo, ObjectDeserializer fieldValueDeserilizer) {
        super(config, clazz, fieldInfo);
        this.fieldValueDeserilizer = fieldValueDeserilizer;
    }
}
