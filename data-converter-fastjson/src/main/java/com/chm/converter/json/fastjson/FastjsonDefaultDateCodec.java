package com.chm.converter.json.fastjson;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.serializer.DateCodec;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.chm.converter.core.Converter;
import com.chm.converter.core.codecs.DefaultDateCodec;
import com.chm.converter.core.utils.StringUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-21
 **/
public class FastjsonDefaultDateCodec<T extends Date> extends DateCodec {

    private final DefaultDateCodec<T> defaultDateCodec;

    public FastjsonDefaultDateCodec(Class<T> dateType) {
        this(dateType, (DateTimeFormatter) null, null);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, String datePattern) {
        this(dateType, datePattern, null);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, DateTimeFormatter dateFormatter) {
        this(dateType, dateFormatter, null);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, Converter<?> converter) {
        this(dateType, (DateTimeFormatter) null, converter);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, String datePattern, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, datePattern, converter);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, DateTimeFormatter dateFormat, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec<>(dateType, dateFormat, converter);
    }

    public FastjsonDefaultDateCodec<T> withDateType(Class<T> dateType) {
        return new FastjsonDefaultDateCodec<>(dateType, this.defaultDateCodec.getDateFormatter(), this.defaultDateCodec.getConverter());
    }

    public FastjsonDefaultDateCodec<T> withDatePattern(String datePattern) {
        return new FastjsonDefaultDateCodec<>(this.defaultDateCodec.getDateType(), datePattern, this.defaultDateCodec.getConverter());
    }

    public FastjsonDefaultDateCodec<T> withDateFormat(DateTimeFormatter dateFormat) {
        return new FastjsonDefaultDateCodec<>(this.defaultDateCodec.getDateType(), dateFormat, this.defaultDateCodec.getConverter());
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;

        if (object == null) {
            out.writeNull();
            return;
        }
        this.defaultDateCodec.write((T) object, out::writeString);
    }

    @Override
    public T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
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

            return this.defaultDateCodec.decode(str);
        }
        return super.deserialze(parser, clazz, fieldName);
    }

    @Override
    public T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName, String format, int features) {
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
            return this.defaultDateCodec.decode(str, format);
        }
        return super.deserialze(parser, clazz, fieldName, format, features);
    }
}
