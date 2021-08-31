package com.chm.converter.json.fastjson;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.Jdk8DateCodec;
import com.alibaba.fastjson.serializer.BeanContext;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.chm.converter.constant.TimeConstant;
import com.chm.converter.json.JsonConverter;
import com.chm.converter.utils.DateUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.TimeZone;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-21
 **/
public class FastjsonJdk8DateCodec<T extends TemporalAccessor> extends Jdk8DateCodec {

    private final Class<T> clazz;

    private final DateTimeFormatter dateFormatter;

    private final JsonConverter jsonConverter;

    private final DateTimeFormatter defaultDateTimeFormatter;

    private final TemporalQuery<T> temporalQuery;

    public FastjsonJdk8DateCodec(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public FastjsonJdk8DateCodec(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public FastjsonJdk8DateCodec(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public FastjsonJdk8DateCodec(Class<T> clazz, JsonConverter jsonConverter) {
        this(clazz, (DateTimeFormatter) null, jsonConverter);
    }

    public FastjsonJdk8DateCodec(Class<T> clazz, String datePattern, JsonConverter jsonConverter) {
        this.clazz = clazz;
        if (StrUtil.isNotBlank(datePattern)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
            if (clazz == Instant.class && dateFormatter.getZone() == null) {
                TimeZone timeZone = jsonConverter != null ? jsonConverter.getTimeZone() : TimeZone.getDefault();
                dateFormatter = dateFormatter.withZone(timeZone.toZoneId());
            }
            this.dateFormatter = dateFormatter;
        } else {
            this.dateFormatter = null;
        }
        this.jsonConverter = jsonConverter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public FastjsonJdk8DateCodec(Class<T> clazz, DateTimeFormatter dateFormatter, JsonConverter jsonConverter) {
        this.clazz = clazz;
        if (dateFormatter != null && clazz == Instant.class && dateFormatter.getZone() == null) {
            TimeZone timeZone = jsonConverter != null ? jsonConverter.getTimeZone() : TimeZone.getDefault();
            dateFormatter = dateFormatter.withZone(timeZone.toZoneId());
        }
        this.dateFormatter = dateFormatter;
        this.jsonConverter = jsonConverter;
        this.defaultDateTimeFormatter = TimeConstant.JAVA8_TIME_DEFAULT_FORMATTER_MAP.get(clazz);
        this.temporalQuery = (TemporalQuery<T>) TimeConstant.CLASS_TEMPORAL_QUERY_MAP.get(clazz);
    }

    public FastjsonJdk8DateCodec<T> withClass(Class<T> clazz) {
        return new FastjsonJdk8DateCodec<>(clazz, this.dateFormatter, this.jsonConverter);
    }

    public FastjsonJdk8DateCodec<T> withDatePattern(String datePattern) {
        return new FastjsonJdk8DateCodec<>(this.clazz, datePattern, this.jsonConverter);
    }

    public FastjsonJdk8DateCodec<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new FastjsonJdk8DateCodec<>(this.clazz, dateFormatter, this.jsonConverter);
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
            DateTimeFormatter dtf = this.dateFormatter;
            if (dtf == null && format != null) {
                dtf = DateTimeFormatter.ofPattern(format);
            }

            if (this.jsonConverter != null && dtf == null) {
                dtf = this.jsonConverter.getDateFormat();
            } else if (dtf == null) {
                // 如果是通过FastJsonConfig进行设置，优先从FastJsonConfig获取
                DateFormat dateFormat = parser.getDateFormat();
                String dateFormatPattern = parser.getDateFomartPattern();
                if (StrUtil.isNotBlank(dateFormatPattern)) {
                    dtf = DateTimeFormatter.ofPattern(dateFormatPattern);
                    if (clazz == Instant.class && dtf.getZone() == null) {
                        // Instant类需设置时区
                        dtf = dtf.withZone(dateFormat.getTimeZone().toZoneId());
                    }
                }
            }

            if (dtf != null) {
                if (clazz == Instant.class && dtf.getZone() == null) {
                    // Instant类需设置时区
                    TimeZone timeZone = this.jsonConverter != null ? this.jsonConverter.getTimeZone() : TimeZone.getDefault();
                    dtf = dtf.withZone(timeZone.toZoneId());
                }
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
        if (jsonConverter != null && dtf == null) {
            dtf = jsonConverter.getDateFormat();
        } else if (dtf == null) {
            DateFormat dateFormat = serializer.getDateFormat();
            String dateFormatPattern = serializer.getDateFormatPattern();
            if (StrUtil.isNotBlank(dateFormatPattern) && dateFormat != null) {
                dtf = DateTimeFormatter.ofPattern(dateFormatPattern);
                if (object instanceof Instant && dtf.getZone() == null) {
                    dtf = dtf.withZone(dateFormat.getTimeZone().toZoneId());
                }
            }
        }

        String str;
        if (dtf != null) {
            if (object instanceof Instant && dtf.getZone() == null) {
                // Instant类需设置时区
                TimeZone timeZone = this.jsonConverter != null ? this.jsonConverter.getTimeZone() : TimeZone.getDefault();
                dtf = dtf.withZone(timeZone.toZoneId());
            }
            str = DateUtil.format((TemporalAccessor) object, dtf);
        } else {
            str = DateUtil.format((TemporalAccessor) object, defaultDateTimeFormatter);
        }
        out.writeString(str);
    }
}
