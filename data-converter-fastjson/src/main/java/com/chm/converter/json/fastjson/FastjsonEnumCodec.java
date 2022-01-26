package com.chm.converter.json.fastjson;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.EnumCodec;
import com.chm.converter.core.utils.StringUtil;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-01-26
 **/
public class FastjsonEnumCodec<E extends Enum<E>> implements ObjectSerializer, ObjectDeserializer {

    private final EnumCodec<E> enumCodec;

    public FastjsonEnumCodec(Class<E> classOfT, Converter<?> converter) {
        this.enumCodec = new EnumCodec<>(classOfT, converter);
    }


    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        JSONLexer lexer = parser.lexer;
        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken();
            return null;
        }
        if (lexer.token() == JSONToken.LITERAL_STRING) {
            String str = lexer.stringVal();
            lexer.nextToken();
            if (StringUtil.isBlank(str)) {
                return null;
            }

            return (T) this.enumCodec.decode(str);
        }
        return null;
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;

        if (object == null) {
            out.writeNull();
            return;
        }
        this.enumCodec.write((E) object, out::writeString);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
}
