package com.chm.converter.json.fastjson;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.Jdk8DateCodec;
import com.alibaba.fastjson.serializer.BeanContext;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.chm.converter.codec.Java8TimeCodec;
import com.chm.converter.core.Converter;
import com.chm.converter.core.utils.StringUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-21
 **/
public class FastjsonJdk8DateCodec<T extends TemporalAccessor> extends Jdk8DateCodec {

    private final Java8TimeCodec<T> java8TimeCodec;

    public FastjsonJdk8DateCodec(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public FastjsonJdk8DateCodec(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public FastjsonJdk8DateCodec(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public FastjsonJdk8DateCodec(Class<T> clazz, Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter);
    }

    public FastjsonJdk8DateCodec(Class<T> clazz, String datePattern, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, datePattern, converter);
    }

    public FastjsonJdk8DateCodec(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, dateFormatter, converter);
    }

    public FastjsonJdk8DateCodec<T> withClass(Class<T> clazz) {
        return new FastjsonJdk8DateCodec<>(clazz, this.java8TimeCodec.getDateFormatter(), this.java8TimeCodec.getConverter());
    }

    public FastjsonJdk8DateCodec<T> withDatePattern(String datePattern) {
        return new FastjsonJdk8DateCodec<>(this.java8TimeCodec.getClazz(), datePattern, this.java8TimeCodec.getConverter());
    }

    public FastjsonJdk8DateCodec<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new FastjsonJdk8DateCodec<>(this.java8TimeCodec.getClazz(), dateFormatter, this.java8TimeCodec.getConverter());
    }

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName, String format, int feature) {
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
            try {
                return (T) this.java8TimeCodec.read(s -> this.java8TimeCodec.decode(s, format), () -> str);
            } catch (IOException e) {
                return null;
            }
        }
        return super.deserialze(parser, type, fieldName, format, feature);
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        serializer(serializer, object);
    }

    @Override
    public void write(JSONSerializer serializer, Object object, BeanContext context) throws IOException {
        serializer(serializer, object);
    }

    private void serializer(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
            return;
        }

        this.java8TimeCodec.write((T) object, out::writeString);
    }
}
