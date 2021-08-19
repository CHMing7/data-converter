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
import com.chm.converter.constant.TimeConstant;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    private DateTimeFormatter dateFormatter;

    private final DateTimeFormatter defaultDateTimeFormatter;

    private final TemporalQuery<T> temporalQuery;

    public FastjsonJdk8DateCodec(Class<T> clazz) {
        this.clazz = clazz;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public FastjsonJdk8DateCodec(Class<T> clazz, String datePattern) {
        this.clazz = clazz;
        if (StrUtil.isNotBlank(datePattern)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
            if (clazz == Instant.class && dateFormatter.getZone() == null) {
                dateFormatter = dateFormatter.withZone(ZoneId.systemDefault());
            }
            this.dateFormatter = dateFormatter;
        }
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public FastjsonJdk8DateCodec(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this.clazz = clazz;
        if (dateFormatter != null && clazz == Instant.class && dateFormatter.getZone() == null) {
            dateFormatter = dateFormatter.withZone(ZoneId.systemDefault());
        }
        this.dateFormatter = dateFormatter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public FastjsonJdk8DateCodec<T> withDatePattern(String datePattern) {
        return new FastjsonJdk8DateCodec<>(this.clazz, datePattern);
    }

    public FastjsonJdk8DateCodec<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new FastjsonJdk8DateCodec<>(this.clazz, dateFormatter);
    }

    public FastjsonJdk8DateCodec<T> withClass(Class<T> clazz) {
        return new FastjsonJdk8DateCodec<>(clazz, this.dateFormatter);
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
        serializer(serializer, object);
    }

    @Override
    public void write(JSONSerializer serializer, Object object, BeanContext context) throws IOException {
        serializer(serializer, object);
    }

    private void serializer(JSONSerializer serializer, Object object) {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
            return;
        }

        DateTimeFormatter dtf = this.dateFormatter;
        if (dtf == null) {
            DateFormat dateFormat = serializer.getDateFormat();
            if (dateFormat instanceof SimpleDateFormat) {
                String fastjsonDateFormat = ((SimpleDateFormat) dateFormat).toPattern();
                dtf = DateTimeFormatter.ofPattern(fastjsonDateFormat);
                if (object instanceof Instant && dtf.getZone() == null) {
                    dtf = dtf.withZone(dateFormat.getTimeZone().toZoneId());
                }
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
