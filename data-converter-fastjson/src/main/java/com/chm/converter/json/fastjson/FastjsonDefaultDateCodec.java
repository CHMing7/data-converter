package com.chm.converter.json.fastjson;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.serializer.DateCodec;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.chm.converter.json.JsonConverter;
import com.chm.converter.utils.DateUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-21
 **/
public class FastjsonDefaultDateCodec<T extends Date> extends DateCodec {

    private final JsonConverter fastjsonConverter;

    private final Class<T> dateType;

    private final DateTimeFormatter dateFormat;

    private static final String DEFAULT_DATE_PATTERN_STR = "yyyy-MM-dd HH:mm:ss.SSSS";

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them is used for serialization as well.
     */
    private final DateTimeFormatter DEFAULT_DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern(DEFAULT_DATE_PATTERN_STR).toFormatter(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public FastjsonDefaultDateCodec(Class<T> dateType) {
        this(dateType, (String) null, null);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, String datePattern) {
        this(dateType, datePattern, null);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, DateTimeFormatter dateFormat) {
        this(dateType, dateFormat, null);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, JsonConverter fastjsonConverter) {
        this(dateType, (String) null, fastjsonConverter);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, String datePattern, JsonConverter fastjsonConverter) {
        this.fastjsonConverter = fastjsonConverter;
        this.dateType = verifyDateType(dateType);
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormat = DateTimeFormatter.ofPattern(datePattern);
        } else {
            this.dateFormat = null;
        }
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, DateTimeFormatter dateFormat, JsonConverter fastjsonConverter) {
        this.fastjsonConverter = fastjsonConverter;
        this.dateType = verifyDateType(dateType);
        this.dateFormat = dateFormat;
    }

    private Class<T> verifyDateType(Class<T> dateType) {
        if (dateType != Date.class && dateType != java.sql.Date.class && dateType != Timestamp.class) {
            throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + dateType);
        }
        return dateType;
    }

    public FastjsonDefaultDateCodec<T> withDatePattern(String datePattern) {
        return new FastjsonDefaultDateCodec<>(this.dateType, datePattern, this.fastjsonConverter);
    }

    public FastjsonDefaultDateCodec<T> withDateFormat(DateTimeFormatter dateFormat) {
        return new FastjsonDefaultDateCodec<>(this.dateType, dateFormat, this.fastjsonConverter);
    }

    public FastjsonDefaultDateCodec<T> withDateType(Class<T> dateType) {
        return new FastjsonDefaultDateCodec<>(dateType, this.dateFormat, this.fastjsonConverter);
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;

        if (object == null) {
            out.writeNull();
            return;
        }

        DateTimeFormatter format = getDateFormat(serializer, null);
        String text = DateUtil.format((Date) object, format);
        out.writeString(text);
    }

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
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

            DateTimeFormatter format = getDateFormat(null, parser);

            Date date = DateUtil.parseToDate(str, format);
            if (dateType == Date.class) {
                return (T) date;
            } else if (dateType == Timestamp.class) {
                return (T) new Timestamp(date.getTime());
            } else if (dateType == java.sql.Date.class) {
                return (T) new java.sql.Date(date.getTime());
            }
        }
        return super.deserialze(parser, clazz, fieldName);
    }

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName, String format, int features) {
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
            DateTimeFormatter dateFormat = null;
            if (format != null) {
                dateFormat = DateTimeFormatter.ofPattern(format);
            }

            if (dateFormat == null) {
                dateFormat = getDateFormat(null, parser);
            }

            Date date = DateUtil.parseToDate(str, dateFormat);

            if (dateType == Date.class) {
                return (T) date;
            } else if (dateType == Timestamp.class) {
                return (T) new Timestamp(date.getTime());
            } else if (dateType == java.sql.Date.class) {
                return (T) new java.sql.Date(date.getTime());
            }
        }
        return super.deserialze(parser, clazz, fieldName, format, features);
    }

    private DateTimeFormatter getDateFormat(JSONSerializer serializer, DefaultJSONParser parser) {
        DateTimeFormatter format = this.dateFormat;

        if (fastjsonConverter != null && format == null) {
            format = fastjsonConverter.getDateFormat();
        } else {
            String dateFormatPattern = null;
            if (serializer != null) {
                dateFormatPattern = serializer.getDateFormatPattern();

                if (StrUtil.isBlank(dateFormatPattern)) {
                    dateFormatPattern = serializer.getFastJsonConfigDateFormatPattern();
                }
            }

            if (parser != null) {
                if (StrUtil.isBlank(dateFormatPattern)) {
                    dateFormatPattern = parser.getDateFomartPattern();
                }
            }

            if (StrUtil.isNotBlank(dateFormatPattern)) {
                format = DateTimeFormatter.ofPattern(dateFormatPattern);
            }
        }

        if (format == null) {
            format = DEFAULT_DATE_FORMAT;
        }
        return format;
    }
}
