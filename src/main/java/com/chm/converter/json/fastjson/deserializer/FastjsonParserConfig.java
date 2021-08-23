package com.chm.converter.json.fastjson.deserializer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.chm.converter.ClassInfoStorage;
import com.chm.converter.FieldInfo;
import com.chm.converter.JavaBeanInfo;
import com.chm.converter.json.fastjson.FastjsonDefaultDateCodec;
import com.chm.converter.json.fastjson.FastjsonJdk8DateCodec;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-09
 **/
public class FastjsonParserConfig extends ParserConfig {

    private static final Map<Class<?>, JavaBeanDeserializer> JAVA_BEAN_DESERIALIZER_MAP = MapUtil.newConcurrentHashMap();

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
        if (deserializer instanceof JavaBeanDeserializer && type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            JavaBeanDeserializer objectDeserializer = JAVA_BEAN_DESERIALIZER_MAP.get(type);
            if (objectDeserializer != null) {
                return objectDeserializer;
            }
            JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz);
            if (CollectionUtil.isNotEmpty(javaBeanInfo.getSortedFieldList())) {
                objectDeserializer = new FastjsonJavaBeanDeserializer(this, clazz);
                JAVA_BEAN_DESERIALIZER_MAP.put(clazz, objectDeserializer);
                return objectDeserializer;
            }
        }
        return deserializer;
    }

    private static class FastjsonJavaBeanDeserializer extends JavaBeanDeserializer {

        public FastjsonJavaBeanDeserializer(ParserConfig config, Class<?> clazz) {
            super(config, clazz);
            Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldInfoMap(clazz);
            if (MapUtil.isEmpty(fieldInfoMap)) {
                return;
            }
            // 属性排序并过滤不需要序列化的属性
            List<FieldDeserializer> sortedFieldDeserializerList = ListUtil.toList(this.sortedFieldDeserializers);
            sortedFieldDeserializerList = CollectionUtil.sort(sortedFieldDeserializerList, (o1, o2) -> {
                FieldInfo fieldInfo1 = fieldInfoMap.get(o1.fieldInfo.name);
                FieldInfo fieldInfo2 = fieldInfoMap.get(o2.fieldInfo.name);
                return fieldInfo1.compareTo(fieldInfo2);
            }).stream().filter(fieldDeserializer -> {
                FieldInfo fieldInfo = fieldInfoMap.get(fieldDeserializer.fieldInfo.name);
                return fieldInfo.isDeserialize();
            }).collect(Collectors.toList());
            FieldDeserializer[] sortedFieldDeserializers = ArrayUtil.toArray(sortedFieldDeserializerList, FieldDeserializer.class);
            // 使用反射重新赋值
            ReflectUtil.setFieldValue(this, "sortedFieldDeserializers", sortedFieldDeserializers);

            for (FieldDeserializer sortedFieldDeserializer : this.sortedFieldDeserializers) {
                // 使用反射注入属性名与format值
                FieldInfo fieldInfo = fieldInfoMap.get(sortedFieldDeserializer.fieldInfo.field.getName());
                ReflectUtil.setFieldValue(sortedFieldDeserializer.fieldInfo, "name", fieldInfo.getName());
                ReflectUtil.setFieldValue(sortedFieldDeserializer.fieldInfo, "name_chars", genFieldNameChars(fieldInfo.getName()));
                ReflectUtil.setFieldValue(sortedFieldDeserializer.fieldInfo, "format", fieldInfo.getFormat());
            }

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
}
