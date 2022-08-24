package com.chm.converter.json.fastjson;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.WithFormat;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-08-10
 **/
public class FastjsonCoreCodec implements ObjectSerializer, ObjectDeserializer, WithFormat {

    private final Codec codec;

    public FastjsonCoreCodec(Codec codec) {
        this.codec = codec;
    }

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object o = parser.parse();
        if (o == null) {
            return null;
        }
        return (T) this.codec.decode(o);
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;

        if (object == null) {
            out.writeNull();
            return;
        }
        Object encode = codec.encode(object);
        if (encode != null && encode.getClass() != object.getClass()) {
            serializer.write(encode);
        }
    }

    @Override
    public FastjsonCoreCodec withDatePattern(String datePattern) {
        if (codec instanceof WithFormat) {
            Codec withCodec = (Codec) ((WithFormat) codec).withDatePattern(datePattern);
            return new FastjsonCoreCodec(withCodec);
        }
        return new FastjsonCoreCodec(this.codec);
    }

    @Override
    public FastjsonCoreCodec withDateFormatter(DateTimeFormatter dateFormatter) {
        if (codec instanceof WithFormat) {
            Codec withCodec = (Codec) ((WithFormat) codec).withDateFormatter(dateFormatter);
            return new FastjsonCoreCodec(withCodec);
        }
        return new FastjsonCoreCodec(this.codec);
    }
}
