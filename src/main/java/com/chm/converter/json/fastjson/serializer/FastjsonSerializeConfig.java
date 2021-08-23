package com.chm.converter.json.fastjson.serializer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.serializer.*;
import com.chm.converter.ClassInfoStorage;
import com.chm.converter.FieldInfo;
import com.chm.converter.JavaBeanInfo;
import com.chm.converter.json.fastjson.FastjsonDefaultDateCodec;
import com.chm.converter.json.fastjson.FastjsonJdk8DateCodec;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-08
 **/
public class FastjsonSerializeConfig extends SerializeConfig {

    private static final NameFilter NAME_FILTER = new FastjsonNameFilter();

    private static final PropertyFilter PROPERTY_FILTER = new FastjsonPropertyFilter();

    public FastjsonSerializeConfig() {
        super();
        // Java8 Time Serializer
        put(Instant.class, new FastjsonJdk8DateCodec<>(Instant.class));
        put(LocalDate.class, new FastjsonJdk8DateCodec<>(LocalDate.class));
        put(LocalDateTime.class, new FastjsonJdk8DateCodec<>(LocalDateTime.class));
        put(LocalTime.class, new FastjsonJdk8DateCodec<>(LocalTime.class));
        put(OffsetDateTime.class, new FastjsonJdk8DateCodec<>(OffsetDateTime.class));
        put(OffsetTime.class, new FastjsonJdk8DateCodec<>(OffsetTime.class));
        put(ZonedDateTime.class, new FastjsonJdk8DateCodec<>(ZonedDateTime.class));
        put(MonthDay.class, new FastjsonJdk8DateCodec<>(MonthDay.class));
        put(YearMonth.class, new FastjsonJdk8DateCodec<>(YearMonth.class));
        put(Year.class, new FastjsonJdk8DateCodec<>(Year.class));
        put(ZoneOffset.class, new FastjsonJdk8DateCodec<>(ZoneOffset.class));

        // Default Date Serializer
        put(java.sql.Date.class, new FastjsonDefaultDateCodec<>(java.sql.Date.class));
        put(Timestamp.class, new FastjsonDefaultDateCodec<>(Timestamp.class));
        put(Date.class, new FastjsonDefaultDateCodec<>(Date.class));
    }

    @Override
    public ObjectSerializer getObjectWriter(Class<?> clazz) {
        ObjectSerializer objectWriter = super.getObjectWriter(clazz);
        if (objectWriter instanceof FastjsonJavaBeanSerializer) {
            return objectWriter;
        }
        if (objectWriter instanceof JavaBeanSerializer) {
            JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz);
            if (CollectionUtil.isNotEmpty(javaBeanInfo.getSortedFieldList())) {
                put(clazz, objectWriter = new FastjsonJavaBeanSerializer(clazz));
                return objectWriter;
            }
        }
        return objectWriter;
    }

    private static class FastjsonJavaBeanSerializer extends JavaBeanSerializer {

        public FastjsonJavaBeanSerializer(Class<?> beanType) {
            super(beanType);
            Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldInfoMap(beanType);
            if (MapUtil.isEmpty(fieldInfoMap)) {
                return;
            }
            // 对属性进行排序
            List<FieldSerializer> sortedGetterList = ListUtil.toList(this.sortedGetters);

            sortedGetterList = CollectionUtil.sort(sortedGetterList, (o1, o2) -> {
                String name1 = (String) ReflectUtil.getFieldValue(o1.fieldInfo, "name");
                String name2 = (String) ReflectUtil.getFieldValue(o2.fieldInfo, "name");
                FieldInfo fieldInfo1 = fieldInfoMap.get(name1);
                FieldInfo fieldInfo2 = fieldInfoMap.get(name2);
                if (fieldInfo1 != null && fieldInfo2 != null) {
                    return fieldInfo1.compareTo(fieldInfo2);
                }
                return o1.compareTo(o2);
            });
            FieldSerializer[] sortedGetters = ArrayUtil.toArray(sortedGetterList, FieldSerializer.class);
            // 使用反射重新赋值
            ReflectUtil.setFieldValue(this, "sortedGetters", sortedGetters);

            for (FieldSerializer sortedGetter : this.sortedGetters) {
                String name = (String) ReflectUtil.getFieldValue(sortedGetter.fieldInfo, "name");
                FieldInfo fieldInfo = fieldInfoMap.get(name);
                if (StrUtil.isNotBlank(fieldInfo.getFormat())) {
                    // 由于属性定义问题，使用反射注入format属性
                    ReflectUtil.setFieldValue(sortedGetter, "format", fieldInfo.getFormat());
                    ReflectUtil.setFieldValue(ReflectUtil.getFieldValue(sortedGetter, "fieldContext"), "format", fieldInfo.getFormat());
                }
            }
            this.addFilter(NAME_FILTER);
            this.addFilter(PROPERTY_FILTER);
        }

    }

    /**
     * 修改key值
     */
    private static class FastjsonNameFilter implements NameFilter {

        @Override
        public String process(Object object, String name, Object value) {
            Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldInfoMap(ClassUtil.getClass(object));
            FieldInfo fieldInfo = fieldInfoMap.get(name);
            return fieldInfo != null && fieldInfo.getName() != null ? fieldInfo.getName() : name;
        }
    }

    /**
     * 检查属性是否需要序列化
     */
    private static class FastjsonPropertyFilter implements PropertyFilter {

        @Override
        public boolean apply(Object object, String name, Object value) {
            Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldInfoMap(ClassUtil.getClass(object));
            FieldInfo fieldInfo = fieldInfoMap.get(name);
            return fieldInfo.isSerialize();
        }
    }

    /**
     * 时间序列化处理
     */
    private static class DateTimeValueFilter implements ValueFilter {

        @Override
        public Object process(Object object, String name, Object value) {
            Map<String, FieldInfo> fieldInfoMap = ClassInfoStorage.INSTANCE.getFieldInfoMap(ClassUtil.getClass(object));
            FieldInfo fieldInfo = fieldInfoMap.get(name);
            return fieldInfo != null && fieldInfo.getName() != null ? fieldInfo.getName() : name;
        }
    }

}
