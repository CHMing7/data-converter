package com.chm.converter.json.fastjson;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.serializer.DateCodec;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-21
 **/
public class FastjsonDefaultDateCodec<T extends Date> extends DateCodec {

    private DateFormat dateFormat;

    private final Class<T> dateType;

    private static final String DEFAULT_DATE_PATTERN_STR = "yyyy-MM-dd HH:mm:ss";

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them is used for serialization as well.
     */
    private final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_PATTERN_STR);

    public FastjsonDefaultDateCodec(Class<T> dateType) {
        this.dateType = verifyDateType(dateType);
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, String datePattern) {
        this.dateType = verifyDateType(dateType);
        if (StrUtil.isNotBlank(datePattern)) {
            this.dateFormat = new SimpleDateFormat(datePattern);
        }
    }

    public FastjsonDefaultDateCodec(Class<T> dateType, DateFormat dateFormat) {
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
        return new FastjsonDefaultDateCodec<>(this.dateType, datePattern);
    }

    public FastjsonDefaultDateCodec<T> withDateFormat(DateFormat dateFormat) {
        return new FastjsonDefaultDateCodec<>(this.dateType, dateFormat);
    }

    public FastjsonDefaultDateCodec<T> withDateType(Class<T> dateType) {
        return new FastjsonDefaultDateCodec<>(dateType, this.dateFormat);
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;

        if (object == null) {
            out.writeNull();
            return;
        }

        DateFormat format = getDateFormat(serializer, null);
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

            DateFormat format = getDateFormat(null, parser);

            Date date = DateUtil.parse(str, format);

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
            DateFormat dateFormat = null;
            if (format != null) {
                dateFormat = new SimpleDateFormat(format);
            }
            if (dateFormat == null) {
                // 如果是通过FastJsonConfig进行设置，优先从FastJsonConfig获取
                String dateFormatPattern = parser.getDateFomartPattern();
                if (dateFormatPattern == null) {
                    dateFormatPattern = JSON.DEFFAULT_DATE_FORMAT;
                }
                if (StrUtil.isNotBlank(dateFormatPattern)) {
                    dateFormat = new SimpleDateFormat(dateFormatPattern);
                }
            }

            if (dateFormat == null) {
                dateFormat = DEFAULT_DATE_FORMAT;
            }
            Date date = DateUtil.parse(str, dateFormat);

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

    private DateFormat getDateFormat(JSONSerializer serializer, DefaultJSONParser parser) {
        DateFormat format = this.dateFormat;

        if (format == null && serializer != null) {
            format = serializer.getDateFormat();
        }

        if (format == null && serializer != null) {
            // 如果是通过FastJsonConfig进行设置，优先从FastJsonConfig获取
            String dateFormatPattern = serializer.getFastJsonConfigDateFormatPattern();
            if (dateFormatPattern == null) {
                dateFormatPattern = JSON.DEFFAULT_DATE_FORMAT;
            }
            if (StrUtil.isNotBlank(dateFormatPattern)) {
                format = new SimpleDateFormat(dateFormatPattern);
            }
        }


        if (format == null && parser != null) {
            format = parser.getDateFormat();
        }
        if (format == null && parser != null) {
            // 如果是通过FastJsonConfig进行设置，优先从FastJsonConfig获取
            String dateFormatPattern = parser.getDateFomartPattern();
            if (dateFormatPattern == null) {
                dateFormatPattern = JSON.DEFFAULT_DATE_FORMAT;
            }
            if (StrUtil.isNotBlank(dateFormatPattern)) {
                format = new SimpleDateFormat(dateFormatPattern);
            }
        }
        if (format == null) {
            format = DEFAULT_DATE_FORMAT;
        }
        return format;
    }
}
