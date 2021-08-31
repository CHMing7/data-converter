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

    private final Class<T> dateType;

    private final DateTimeFormatter dateFormatter;

    private final JsonConverter jsonConverter;

    private static final String DEFAULT_DATE_PATTERN_STR = "yyyy-MM-dd HH:mm:ss.SSSS";

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them is used for serialization as well.
     */
    private final static DateTimeFormatter DEFAULT_DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern(DEFAULT_DATE_PATTERN_STR).toFormatter(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public FastjsonDefaultDateCodec(Class<T> dateType) {
        this(dateType, (DateTimeFormatter) null, null);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, String datePattern) {
        this(dateType, datePattern, null);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, DateTimeFormatter dateFormatter) {
        this(dateType, dateFormatter, null);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, JsonConverter jsonConverter) {
        this(dateType, (DateTimeFormatter) null, jsonConverter);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, String datePattern, JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
        this.dateType = verifyDateType(dateType);
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        } else {
            this.dateFormatter = null;
        }
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, DateTimeFormatter dateFormat, JsonConverter jsonConverter) {
        this.dateType = verifyDateType(dateType);
        this.dateFormatter = dateFormat;
        this.jsonConverter = jsonConverter;
    }

    private Class<T> verifyDateType(Class<T> dateType) {
        if (dateType != Date.class && dateType != java.sql.Date.class && dateType != Timestamp.class) {
            throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + dateType);
        }
        return dateType;
    }

    public FastjsonDefaultDateCodec<T> withDateType(Class<T> dateType) {
        return new FastjsonDefaultDateCodec<>(dateType, this.dateFormatter, this.jsonConverter);
    }

    public FastjsonDefaultDateCodec<T> withDatePattern(String datePattern) {
        return new FastjsonDefaultDateCodec<>(this.dateType, datePattern, this.jsonConverter);
    }

    public FastjsonDefaultDateCodec<T> withDateFormat(DateTimeFormatter dateFormat) {
        return new FastjsonDefaultDateCodec<>(this.dateType, dateFormat, this.jsonConverter);
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
            DateTimeFormatter dateFormat = this.dateFormatter;
            if (dateFormat == null && format != null) {
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
        DateTimeFormatter formatter = this.dateFormatter;

        if (jsonConverter != null && formatter == null) {
            formatter = jsonConverter.getDateFormat();
        } else if (formatter == null) {
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
                formatter = DateTimeFormatter.ofPattern(dateFormatPattern);
            }
        }

        if (formatter == null) {
            formatter = DEFAULT_DATE_FORMAT;
        }
        return formatter;
    }
}
