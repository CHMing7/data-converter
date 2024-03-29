package com.chm.converter.core;

import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.io.Reader;
import com.chm.converter.core.io.Writer;
import com.chm.converter.core.reflect.ConverterTypes;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.AnnotationUtil;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.core.utils.ReflectUtil;
import com.chm.converter.core.utils.StringUtil;
import com.chm.converter.core.utils.TypeUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author caihongming
 * @version v1.0
 * @date 2021-08-16
 **/
public class FieldInfo implements Comparable<FieldInfo> {

    public static final FieldInfo STOP = new FieldInfo(TypeToken.get(Void.class), "stop", null, FieldInfo.class.getFields()[0], -1, null, null) {
        @Override
        public boolean isStop() {
            return true;
        }
    };

    public static final Comparator<FieldInfo> FIELD_INFO_COMPARATOR = (o1, o2) -> {
        // Deal extend bridge
        if (o2.method != null && o1.method != null
                && o2.method.isBridge() && !o1.method.isBridge()
                && o2.method.getName().equals(o1.method.getName())) {
            return 1;
        }

        if (o1.ordinal < o2.ordinal) {
            return -1;
        }

        if (o1.ordinal > o2.ordinal) {
            return 1;
        }

        int result = o1.name.compareTo(o2.name);

        if (result != 0) {
            return result;
        }

        Class<?> thisDeclaringClass = o1.getDeclaredClass();
        Class<?> otherDeclaringClass = o2.getDeclaredClass();

        if (thisDeclaringClass != null && otherDeclaringClass != null && thisDeclaringClass != otherDeclaringClass) {
            if (thisDeclaringClass.isAssignableFrom(otherDeclaringClass)) {
                return -1;
            }

            if (otherDeclaringClass.isAssignableFrom(thisDeclaringClass)) {
                return 1;
            }
        }
        boolean isSampeType = o1.field != null && o1.field.getType() == o1.fieldClass;
        boolean oSameType = o2.field != null && o2.field.getType() == o2.fieldClass;

        if (isSampeType && !oSameType) {
            return 1;
        }

        if (oSameType && !isSampeType) {
            return -1;
        }

        if (o2.fieldClass.isPrimitive() && !o1.fieldClass.isPrimitive()) {
            return 1;
        }

        if (o1.fieldClass.isPrimitive() && !o2.fieldClass.isPrimitive()) {
            return -1;
        }

        if (o2.fieldClass.getName().startsWith("java.") && !o1.fieldClass.getName().startsWith("java.")) {
            return 1;
        }

        if (o1.fieldClass.getName().startsWith("java.") && !o2.fieldClass.getName().startsWith("java.")) {
            return -1;
        }

        return o1.fieldClass.getName().compareTo(o2.fieldClass.getName());
    };

    /**
     * 序列化对应属性名
     */
    public final String name;

    public final Method method;

    public final Method setter;

    public final Method getter;

    public final Field field;
    public final Class<?> fieldClass;
    public final Type fieldType;

    public final TypeToken<?> typeToken;

    public final Class<?> declaringClass;

    public final boolean getOnly;

    public final String format;

    private final FieldProperty fieldAnnotation;

    private final FieldProperty methodAnnotation;

    /**
     * 是否序列化
     */
    private final boolean serialize;

    /**
     * 是否反序列化
     */
    private final boolean deserialize;

    /**
     * 是否可持久化
     */
    private final boolean isTransient;

    /**
     * 扩展属性
     */
    private final Map<String, Object> expandProperty;

    /**
     * 属性注解集
     */
    private final List<Annotation> fieldAnnotationList;

    /**
     * 属性注解类型集
     */
    private final Set<Class<? extends Annotation>> fieldAnnotationClassSet;

    /**
     * 方法注解集
     */
    private final List<Annotation> methodAnnotationList;

    /**
     * 方法注解类型集
     */
    private final Set<Class<? extends Annotation>> methodAnnotationClassSet;

    private int ordinal = 0;

    /**
     * 排序编号
     */
    private int sortedNumber = 0;

    public FieldInfo(final TypeToken declaringTypeToken, String name, Method method, Field field, int ordinal, FieldProperty fieldAnnotation, FieldProperty methodAnnotation) {
        if (field != null) {
            String fieldName = field.getName();
            if (fieldName.equals(name)) {
                // fix bug key != fieldInfoName see JavaBeanSerializer.java 418行
                //if (key != fieldInfoName) {
                //    if (!writeAsArray) {
                //        out.writeFieldName(key, true);
                //    }
                //
                //    serializer.write(propertyValue);
                //}
                name = fieldName;
            }
        }
        if (ordinal < 0) {
            ordinal = 0;
        }
        boolean getOnly = false;
        Type fieldType;
        Method getter = null;
        Method setter = null;
        String fieldName;
        Class<?> raw = declaringTypeToken.getRawType();

        if (method != null) {
            Class<?>[] types;
            if ((types = method.getParameterTypes()).length == 1) {
                fieldType = method.getGenericParameterTypes()[0];
            } else if (types.length == 2 && types[0] == String.class && types[1] == Object.class) {
                fieldType = types[0];
            } else {
                fieldType = method.getGenericReturnType();
                getOnly = true;
            }
            this.declaringClass = method.getDeclaringClass();
            fieldName = getGeneralField(method.getName());
        } else {
            fieldType = field.getGenericType();
            this.declaringClass = field.getDeclaringClass();
            getOnly = Modifier.isFinal(field.getModifiers());
            fieldName = field.getName();
        }
        // 获取真实 fieldType
        fieldType = ConverterTypes.resolve(declaringTypeToken.getType(), raw, fieldType);
        Class<?> fieldClass = TypeUtil.getClass(fieldType);
        // 尝试获取getter setter
        final boolean isBooleanField = ClassUtil.isBoolean(fieldClass);
        Method[] methods = this.declaringClass.getMethods();
        String methodName;
        Class<?>[] parameterTypes;
        for (Method getOrSetMethod : methods) {
            parameterTypes = getOrSetMethod.getParameterTypes();
            if (parameterTypes.length > 1) {
                // 多于1个参数说明非Getter或Setter
                continue;
            }

            methodName = getOrSetMethod.getName();
            if (parameterTypes.length == 0) {
                // 无参数，可能为Getter方法
                if (isMatchGetter(methodName, fieldName, isBooleanField, false)) {
                    // 方法名与字段名匹配，则为Getter方法
                    getter = getOrSetMethod;
                } else if (isMatchGetter(methodName, fieldName, isBooleanField, true)) {
                    // 方法名与字段名匹配，则为Getter方法
                    getter = getOrSetMethod;
                }
            } else if (isMatchSetter(methodName, fieldName, isBooleanField, false)) {
                // 只有一个参数的情况下方法名与字段名对应匹配，则为Setter方法
                setter = getOrSetMethod;
            } else if (isMatchSetter(methodName, fieldName, isBooleanField, true)) {
                // 只有一个参数的情况下方法名与字段名对应匹配，则为Setter方法
                setter = getOrSetMethod;
            }
            if (null != getter && null != setter) {
                // 如果Getter和Setter方法都找到了，不再继续寻找
                break;
            }
        }
        this.name = name;
        this.method = method;
        this.getter = getter;
        this.setter = setter;
        this.field = field;
        this.ordinal = ordinal;
        this.fieldClass = fieldClass;
        this.fieldType = fieldType;
        this.typeToken = TypeToken.get(fieldType != null ? fieldType : fieldClass);
        this.getOnly = getOnly;
        this.fieldAnnotation = fieldAnnotation;
        this.methodAnnotation = methodAnnotation;
        FieldProperty annotation = this.getAnnotation();
        boolean serialize = true;
        boolean deserialize = true;
        String format = null;
        if (annotation != null) {
            format = annotation.format();
            if (format.trim().length() == 0) {
                format = null;
            }
            serialize = annotation.serialize();
            deserialize = annotation.deserialize();
        }
        this.format = format;
        this.serialize = serialize;
        this.deserialize = deserialize;
        if (this.field != null) {
            this.isTransient = Modifier.isTransient(field.getModifiers());
        } else {
            this.isTransient = false;
        }
        this.expandProperty = MapUtil.newConcurrentHashMap();
        this.fieldAnnotationList = ListUtil.toList(field != null ? AnnotationUtil.getAnnotations(field, true) : null);
        this.fieldAnnotationClassSet = this.fieldAnnotationList.stream().map(Annotation::annotationType).collect(Collectors.toSet());
        this.methodAnnotationList = ListUtil.toList(method != null ? AnnotationUtil.getAnnotations(method, true) : null);
        this.methodAnnotationClassSet = this.methodAnnotationList.stream().map(Annotation::annotationType).collect(Collectors.toSet());
    }


    /**
     * 获得set或get或is方法对应的标准属性名<br>
     * 例如：setName 返回 name
     *
     * <pre>
     * getName =》name
     * get_user_name =》user_name
     * setName =》name
     * isName  =》name
     * </pre>
     *
     * @param getOrSetMethodName Get或Set方法名
     * @return 如果是set或get方法名，返回field， 否则null
     */
    public static String getGeneralField(CharSequence getOrSetMethodName) {
        final String getOrSetMethodNameStr = getOrSetMethodName.toString();
        if (getOrSetMethodNameStr.startsWith("get") || getOrSetMethodNameStr.startsWith("set")) {
            return StringUtil.removePreAndLowerFirst(getOrSetMethodName, 3);
        } else if (getOrSetMethodNameStr.startsWith("is")) {
            return StringUtil.removePreAndLowerFirst(getOrSetMethodName, 2);
        } else if (getOrSetMethodNameStr.charAt(3) == '_') {
            return StringUtil.subSuf(getOrSetMethodName, 3);
        }

        return null;
    }

    /**
     * 检查fieldInfo中是否包含annotationList中任意一个注解
     *
     * @param fieldInfo
     * @param annotationList
     * @return
     */
    public static boolean checkExistAnnotation(FieldInfo fieldInfo, List<Class<? extends Annotation>> annotationList) {
        if (fieldInfo == null) {
            return false;
        }
        Set<Class<? extends Annotation>> fieldAnnotationClassSet = fieldInfo.getFieldAnnotationClassSet();
        Set<Class<? extends Annotation>> methodAnnotationClassSet = fieldInfo.getMethodAnnotationClassSet();
        for (Class<? extends Annotation> annotation : annotationList) {
            if (fieldAnnotationClassSet.contains(annotation) || methodAnnotationClassSet.contains(annotation)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 方法是否为Getter方法<br>
     * 匹配规则如下（忽略大小写）：
     *
     * <pre>
     * 字段名    -》 方法名
     * isName  -》 isName
     * isName  -》 isIsName
     * isName  -》 getIsName
     * name     -》 isName
     * name     -》 getName
     * </pre>
     *
     * @param methodName     方法名
     * @param fieldName      字段名
     * @param isBooleanField 是否为Boolean类型字段
     * @return 是否匹配
     */
    private boolean isMatchGetter(String methodName, String fieldName, boolean isBooleanField, boolean ignoreCase) {
        final String handledFieldName;
        if (ignoreCase) {
            // 全部转为小写，忽略大小写比较
            methodName = methodName.toLowerCase();
            handledFieldName = fieldName.toLowerCase();
            fieldName = handledFieldName;
        } else {
            handledFieldName = StringUtil.upperFirst(fieldName);
        }

        if (!methodName.startsWith("get") && !methodName.startsWith("is")) {
            // 非标准Getter方法
            return false;
        }
        if ("getClass".equals(methodName)) {
            //跳过getClass方法
            return false;
        }

        // 针对Boolean类型特殊检查
        if (isBooleanField) {
            if (fieldName.startsWith("is")) {
                // 字段已经是is开头
                // isName -》 isName
                if (methodName.equals(fieldName)
                        // isName -》 getIsName
                        || methodName.equals("get" + handledFieldName)
                        // isName -》 isIsName
                        || methodName.equals("is" + handledFieldName)
                ) {
                    return true;
                }
            } else if (methodName.equals("is" + handledFieldName)) {
                // 字段非is开头， name -》 isName
                return true;
            }
        }

        // 包括boolean的任何类型只有一种匹配情况：name -》 getName
        return methodName.equals("get" + handledFieldName);
    }

    /**
     * 方法是否为Setter方法<br>
     * 匹配规则如下（忽略大小写）：
     *
     * <pre>
     * 字段名    -》 方法名
     * isName  -》 setName
     * isName  -》 setIsName
     * name     -》 setName
     * </pre>
     *
     * @param methodName     方法名
     * @param fieldName      字段名
     * @param isBooleanField 是否为Boolean类型字段
     * @return 是否匹配
     */
    private boolean isMatchSetter(String methodName, String fieldName, boolean isBooleanField, boolean ignoreCase) {
        final String handledFieldName;
        if (ignoreCase) {
            // 全部转为小写，忽略大小写比较
            methodName = methodName.toLowerCase();
            handledFieldName = fieldName.toLowerCase();
            fieldName = handledFieldName;
        } else {
            handledFieldName = StringUtil.upperFirst(fieldName);
        }

        // 非标准Setter方法跳过
        if (!methodName.startsWith("set")) {
            return false;
        }

        // 针对Boolean类型特殊检查
        if (isBooleanField && fieldName.startsWith("is")) {
            // 字段是is开头
            // isName -》 setName
            if (methodName.equals("set" + StringUtil.removePrefix(fieldName, "is"))
                    // isName -》 setIsName
                    || methodName.equals("set" + handledFieldName)) {
                return true;
            }
        }

        // 包括boolean的任何类型只有一种匹配情况：name -》 setName
        return methodName.equals("set" + handledFieldName);
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return this.method != null ? getGeneralField(this.method.getName()) : this.field.getName();
    }

    public Method getMethod() {
        return method;
    }

    public Method getSetter() {
        return setter;
    }

    public Method getGetter() {
        return getter;
    }

    public Field getField() {
        return field;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public Member getMember() {
        return this.getter != null ? this.getter : (this.setter != null ? this.setter : this.field);
    }

    public int getSortedNumber() {
        return sortedNumber;
    }

    public void setSortedNumber(int sortedNumber) {
        this.sortedNumber = sortedNumber;
    }

    public Class<?> getFieldClass() {
        return this.fieldClass;
    }

    public Type getFieldType() {
        return fieldType;
    }

    public TypeToken<?> getTypeToken() {
        return typeToken;
    }

    public Class<?> getDeclaredClass() {
        return this.declaringClass;
    }

    public FieldProperty getAnnotation() {
        return this.methodAnnotation != null ? this.methodAnnotation : this.fieldAnnotation;
    }

    public Map<String, Object> getExpandProperty() {
        return expandProperty;
    }

    public Object getExpandProperty(String key) {
        return expandProperty.get(key);
    }

    public Object getExpandProperty(String key, Object defaultVal) {
        return MapUtil.computeIfAbsent(expandProperty, key, s -> defaultVal);
    }

    public Object getExpandProperty(String key, Supplier defaultVal) {
        return MapUtil.computeIfAbsent(expandProperty, key, s -> defaultVal.get());
    }

    public void putExpandProperty(String key, Object value) {
        expandProperty.put(key, value);
    }

    public List<Annotation> getFieldAnnotationList() {
        return fieldAnnotationList;
    }

    public Set<Class<? extends Annotation>> getFieldAnnotationClassSet() {
        return fieldAnnotationClassSet;
    }

    public List<Annotation> getMethodAnnotationList() {
        return methodAnnotationList;
    }

    public Set<Class<? extends Annotation>> getMethodAnnotationClassSet() {
        return methodAnnotationClassSet;
    }

    @Override
    public int compareTo(FieldInfo o) {
        return FIELD_INFO_COMPARATOR.compare(this, o);
    }

    public String getFormat() {
        return this.format;
    }

    public boolean isSerialize() {
        return serialize && !isTransient;
    }

    public boolean isDeserialize() {
        return deserialize;
    }

    public Object get(Object javaObject) {
        return this.getter != null ? ReflectUtil.invoke(javaObject, getter) : ReflectUtil.getFieldValue(javaObject, field);
    }

    public void set(Object javaObject, Object value) {
        if (this.setter != null) {
            ReflectUtil.invoke(javaObject, setter, value);
        } else {
            ReflectUtil.setFieldValue(javaObject, field, value);
        }
    }

    public void read(Object javaObject, Reader reader) throws IOException {
        if (reader != null) {
            set(javaObject, reader.read());
        }
    }

    public void write(Object javaObject, Writer writer) throws IOException {
        if (writer != null) {
            writer.write(get(javaObject));
        }
    }

    public boolean isStop() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldInfo fieldInfo = (FieldInfo) o;
        return Objects.equals(name, fieldInfo.name) && Objects.equals(method, fieldInfo.method) && Objects.equals(field, fieldInfo.field) && Objects.equals(fieldClass, fieldInfo.fieldClass) && Objects.equals(declaringClass, fieldInfo.declaringClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, method, field, fieldClass, declaringClass);
    }
}
