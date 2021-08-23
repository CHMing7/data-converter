package com.chm.converter.json.fastjson;

import cn.hutool.core.date.TemporalAccessorUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.Jdk8DateCodec;
import com.alibaba.fastjson.serializer.BeanContext;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.chm.converter.TimeConstant;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-21
 **/
public class FastjsonJdk8DateCodec<T extends TemporalAccessor> extends Jdk8DateCodec {

    private final Class<T> clazz;

    private final DateTimeFormatter defaultDateTimeFormatter;

    private final TemporalQuery<T> temporalQuery;

    public FastjsonJdk8DateCodec(Class<T> clazz) {
        this.clazz = clazz;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
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
            if (StrUtil.isBlank(str)) {
                return null;
            }
            DateTimeFormatter dtf = null;
            if (format != null) {
                dtf = DateTimeFormatter.ofPattern(format);
                if (clazz == Instant.class && dtf.getZone() == null) {
                    dtf = dtf.withZone(ZoneId.systemDefault());
                }
            }
            if (dtf != null) {
                return (T) dtf.parse(str, temporalQuery);
            } else {
                return (T) defaultDateTimeFormatter.parse(str, temporalQuery);
            }
        }
        return super.deserialze(parser, type, fieldName, format, feature);
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
            return;
        }
        String str = TemporalAccessorUtil.format((TemporalAccessor) object, defaultDateTimeFormatter);
        out.writeString(str);
    }

    @Override
    public void write(JSONSerializer serializer, Object object, BeanContext context) throws IOException {
        SerializeWriter out = serializer.out;
        String format = context.getFormat();
        if (object == null) {
            out.writeNull();
            return;
        }
        DateTimeFormatter dtf = null;
        if (format != null) {
            dtf = DateTimeFormatter.ofPattern(format);
            if (clazz == Instant.class && dtf.getZone() == null) {
                dtf = dtf.withZone(ZoneId.systemDefault());
            }
        }
        String str;
        if (dtf != null) {
            str = TemporalAccessorUtil.format((TemporalAccessor) object, dtf);
        } else {
            str = TemporalAccessorUtil.format((TemporalAccessor) object, defaultDateTimeFormatter);
        }
        out.writeString(str);
    }
}
