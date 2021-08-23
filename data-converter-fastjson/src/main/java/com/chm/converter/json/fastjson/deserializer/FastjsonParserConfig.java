package com.chm.converter.json.fastjson.deserializer;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.json.FastjsonConverter;
import com.chm.converter.json.fastjson.FastjsonDefaultDateCodec;
import com.chm.converter.json.fastjson.FastjsonJdk8DateCodec;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-09
 **/
public class FastjsonParserConfig extends ParserConfig {

    /**
     * 日期格式
     */
    private String dateFormat;

    public FastjsonParserConfig() {
        super();
        // Java8 Time Deserializer
        putDeserializer(Instant.class, new FastjsonJdk8DateCodec<>(Instant.class));
        putDeserializer(LocalDate.class, new FastjsonJdk8DateCodec<>(LocalDate.class));
        putDeserializer(LocalDateTime.class, new FastjsonJdk8DateCodec<>(LocalDateTime.class));
        putDeserializer(LocalTime.class, new FastjsonJdk8DateCodec<>(LocalTime.class));
        putDeserializer(OffsetDateTime.class, new FastjsonJdk8DateCodec<>(OffsetDateTime.class));
        putDeserializer(OffsetTime.class, new FastjsonJdk8DateCodec<>(OffsetTime.class));
        putDeserializer(ZonedDateTime.class, new FastjsonJdk8DateCodec<>(ZonedDateTime.class));
        putDeserializer(MonthDay.class, new FastjsonJdk8DateCodec<>(MonthDay.class));
        putDeserializer(YearMonth.class, new FastjsonJdk8DateCodec<>(YearMonth.class));
        putDeserializer(Year.class, new FastjsonJdk8DateCodec<>(Year.class));
        putDeserializer(ZoneOffset.class, new FastjsonJdk8DateCodec<>(ZoneOffset.class));

        // Default Date Deserializer
        putDeserializer(java.sql.Date.class, new FastjsonDefaultDateCodec<>(java.sql.Date.class));
        putDeserializer(Timestamp.class, new FastjsonDefaultDateCodec<>(Timestamp.class));
        putDeserializer(Date.class, new FastjsonDefaultDateCodec<>(Date.class));
    }

    @Override
    public ObjectDeserializer getDeserializer(Type type) {
        ObjectDeserializer deserializer = super.getDeserializer(type);
        if (deserializer instanceof FastjsonJavaBeanDeserializer) {
            return deserializer;
        }
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            // 校验制定类或其父类集中是否存在Fastjson框架注解
            if (FastjsonConverter.checkExistFastjsonAnnotation(clazz)) {
                return deserializer;
            }

            if (deserializer instanceof JavaBeanDeserializer) {
                JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz);
                if (CollectionUtil.isNotEmpty(javaBeanInfo.getSortedFieldList())) {
                    putDeserializer(clazz, deserializer = new FastjsonJavaBeanDeserializer(this, clazz));
                    return deserializer;
                }
            }
        }

        return deserializer;
    }

    private class FastjsonJavaBeanDeserializer extends JavaBeanDeserializer {

        public FastjsonJavaBeanDeserializer(ParserConfig config, Class<?> clazz) {
            super(config, clazz);
            JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz);
            List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
            if (CollectionUtil.isEmpty(sortedFieldList)) {
                return;
            }
            // 属性排序并过滤不需要序列化的属性
            // 过滤不需要序列化的属性
            List<FieldDeserializer> sortedFieldDeserializerList = ListUtil.toList(this.sortedFieldDeserializers);
            Map<String, FieldDeserializer> fieldDeserializerMap = CollStreamUtil.toMap(sortedFieldDeserializerList,
                    fieldDeserializer -> fieldDeserializer.fieldInfo.name, Function.identity());

            sortedFieldDeserializerList = sortedFieldList.stream().filter(FieldInfo::isDeserialize).map(fieldInfo -> {
                // 使用自定义属性序列化类
                FieldDeserializer fieldDeserializer = fieldDeserializerMap.get(fieldInfo.getFieldName());
                ObjectDeserializer objectDeserializer = getFieldDeserializer(fieldInfo);
                ReflectUtil.setFieldValue(fieldDeserializer.fieldInfo, "name", fieldInfo.getName());
                ReflectUtil.setFieldValue(fieldDeserializer.fieldInfo, "name_chars", genFieldNameChars(fieldInfo.getName()));
                ReflectUtil.setFieldValue(fieldDeserializer.fieldInfo, "format", fieldInfo.getFormat());
                return new FastjsonFieldDeserializer(config, fieldInfo.fieldClass, fieldDeserializer.fieldInfo, fieldInfo, objectDeserializer);
            }).collect(Collectors.toList());

            FieldDeserializer[] sortedFieldDeserializers = ArrayUtil.toArray(sortedFieldDeserializerList, FieldDeserializer.class);
            // 使用反射重新赋值
            ReflectUtil.setFieldValue(this, "sortedFieldDeserializers", sortedFieldDeserializers);
        }

        private ObjectDeserializer getFieldDeserializer(FieldInfo fieldInfo) {
            ObjectDeserializer objectDeserializer = FastjsonParserConfig.this.getDeserializer(fieldInfo.getFieldClass());

            if (objectDeserializer instanceof FastjsonJdk8DateCodec) {
                String format = fieldInfo.getFormat();
                String gsonFormat = JSON.DEFFAULT_DATE_FORMAT;
                objectDeserializer = ((FastjsonJdk8DateCodec<?>) objectDeserializer).withDatePattern(ObjectUtil.defaultIfBlank(format, gsonFormat));
            }
            if (objectDeserializer instanceof FastjsonDefaultDateCodec) {
                String format = fieldInfo.getFormat();
                String gsonFormat = JSON.DEFFAULT_DATE_FORMAT;
                objectDeserializer = ((FastjsonDefaultDateCodec<?>) objectDeserializer).withDatePattern(ObjectUtil.defaultIfBlank(format, gsonFormat));
            }

            return objectDeserializer;
        }

        protected char[] genFieldNameChars(String name) {
            int nameLen = name.length();
            char[] nameChars = new char[nameLen + 3];
            name.getChars(0, name.length(), nameChars, 1);
            nameChars[0] = '"';
            nameChars[nameLen + 1] = '"';
            nameChars[nameLen + 2] = ':';
            return nameChars;
        }
    }

    public void setDateFormat(String format) {
        this.dateFormat = format;

    }

    public String getDateFormat() {
        return this.dateFormat;
    }
    
}
