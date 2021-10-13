package com.chm.converter.core;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.utils.StringUtil;
import com.chm.converter.core.utils.TypeUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
public class FieldInfo implements Comparable<FieldInfo> {

    /**
     * 序列化对应属性名
     */
    public final String name;

    public final Method method;

    public final Method setter;

    public final Method getter;

    public final Field field;

    private int ordinal = 0;

    public final Class<?> fieldClass;

    public final Type fieldType;

    public final Class<?> declaringClass;

    public final boolean getOnly;

    private final FieldProperty fieldAnnotation;

    private final FieldProperty methodAnnotation;

    public final String format;

    /**
     * 是否序列化
     */
    private final boolean serialize;

    /**
     * 是否反序列化
     */
    private final boolean deserialize;

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

    public FieldInfo(String name, Method method, Field field, int ordinal, FieldProperty fieldAnnotation , FieldProperty methodAnnotation) {
        if (field != null) {
            String fieldName = field.getName();
            if (fieldName.equals(name)) {
                name = fieldName;
            }
        }
        if (ordinal < 0) {
            ordinal = 0;
        }
        boolean getOnly = false;
        Type fieldType;
        Class<?> fieldClass;
        Method getter = null;
        Method setter = null;
        String fieldName;
        if (method != null) {
            Class<?>[] types;
            if ((types = method.getParameterTypes()).length == 1) {
                fieldClass = types[0];
                fieldType = method.getGenericParameterTypes()[0];
            } else if (types.length == 2 && types[0] == String.class && types[1] == Object.class) {
                fieldType = fieldClass = types[0];
            } else {
                fieldClass = method.getReturnType();
                fieldType = method.getGenericReturnType();
                getOnly = true;
            }
            this.declaringClass = method.getDeclaringClass();
            fieldName = getGeneralField(method.getName());
        } else {
            fieldClass = field.getType();
            fieldType = field.getGenericType();
            this.declaringClass = field.getDeclaringClass();
            getOnly = Modifier.isFinal(field.getModifiers());
            fieldName = field.getName();
        }
        // 尝试获取getter setter
        final boolean isBooleanField = BooleanUtil.isBoolean(fieldClass);
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
                if (isMatchGetter(methodName, fieldName, isBooleanField)) {
                    // 方法名与字段名匹配，则为Getter方法
                    getter = getOrSetMethod;
                }
            } else if (isMatchSetter(methodName, fieldName, isBooleanField)) {
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
        this.expandProperty = new ConcurrentHashMap<>();
        this.fieldAnnotationList = ListUtil.toList(field != null ? field.getAnnotations() : null);
        this.fieldAnnotationClassSet = this.fieldAnnotationList.stream().map(Annotation::annotationType).collect(Collectors.toSet());
        this.methodAnnotationList = ListUtil.toList(method != null ? method.getAnnotations() : null);
        this.methodAnnotationClassSet = this.methodAnnotationList.stream().map(Annotation::annotationType).collect(Collectors.toSet());
    }


    /**
     * 获得set或get或is方法对应的标准属性名<br>
     * 例如：setName 返回 name
     *
     * <pre>
     * getName =》name
     * setName =》name
     * isName  =》name
     * </pre>
     *
     * @param getOrSetMethodName Get或Set方法名
     * @return 如果是set或get方法名，返回field， 否则null
     */
    public static String getGeneralField(CharSequence getOrSetMethodName) {
        // 将下划线式命名改成驼峰式
        getOrSetMethodName = StringUtil.toCamelCase(getOrSetMethodName);
        final String getOrSetMethodNameStr = getOrSetMethodName.toString();
        if (getOrSetMethodNameStr.startsWith("get") || getOrSetMethodNameStr.startsWith("set")) {
            return StringUtil.removePreAndLowerFirst(getOrSetMethodName, 3);
        } else if (getOrSetMethodNameStr.startsWith("is")) {
            return StringUtil.removePreAndLowerFirst(getOrSetMethodName, 2);
        }

        return null;
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
    private boolean isMatchGetter(String methodName, String fieldName, boolean isBooleanField) {
        // 将下划线式命名改成驼峰式
        methodName = StringUtil.toCamelCase(methodName);
        final String handledFieldName = StringUtil.upperFirst(fieldName);

        if (!methodName.startsWith("get") && !methodName.startsWith("is")) {
            // 非标准Getter方法
            return false;
        }
        if ("getclass".equals(methodName)) {
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
    private boolean isMatchSetter(String methodName, String fieldName, boolean isBooleanField) {
        // 将下划线式命名改成驼峰式
        methodName = StringUtil.toCamelCase(methodName);
        final String handledFieldName = StringUtil.upperFirst(fieldName);

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
                    || methodName.equals("set" + handledFieldName)
            ) {
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

    public Field getField() {
        return field;
    }

    public Member getMember() {
        return this.method != null ? this.method : this.field;
    }

    public Class<?> getFieldClass() {
        return this.fieldClass;
    }

    protected Class<?> getDeclaredClass() {
        if (this.method != null) {
            return this.method.getDeclaringClass();
        }

        if (this.field != null) {
            return this.field.getDeclaringClass();
        }

        return null;
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
        return expandProperty.getOrDefault(key, defaultVal);
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

    @Override
    public int compareTo(FieldInfo o) {
        // Deal extend bridge
        if (o.method != null && this.method != null
                && o.method.isBridge() && !this.method.isBridge()
                && o.method.getName().equals(this.method.getName())) {
            return 1;
        }

        if (this.ordinal < o.ordinal) {
            return -1;
        }

        if (this.ordinal > o.ordinal) {
            return 1;
        }

        int result = this.name.compareTo(o.name);

        if (result != 0) {
            return result;
        }

        Class<?> thisDeclaringClass = this.getDeclaredClass();
        Class<?> otherDeclaringClass = o.getDeclaredClass();

        if (thisDeclaringClass != null && otherDeclaringClass != null && thisDeclaringClass != otherDeclaringClass) {
            if (thisDeclaringClass.isAssignableFrom(otherDeclaringClass)) {
                return -1;
            }

            if (otherDeclaringClass.isAssignableFrom(thisDeclaringClass)) {
                return 1;
            }
        }
        boolean isSampeType = this.field != null && this.field.getType() == this.fieldClass;
        boolean oSameType = o.field != null && o.field.getType() == o.fieldClass;

        if (isSampeType && !oSameType) {
            return 1;
        }

        if (oSameType && !isSampeType) {
            return -1;
        }

        if (o.fieldClass.isPrimitive() && !this.fieldClass.isPrimitive()) {
            return 1;
        }

        if (this.fieldClass.isPrimitive() && !o.fieldClass.isPrimitive()) {
            return -1;
        }

        if (o.fieldClass.getName().startsWith("java.") && !this.fieldClass.getName().startsWith("java.")) {
            return 1;
        }

        if (this.fieldClass.getName().startsWith("java.") && !o.fieldClass.getName().startsWith("java.")) {
            return -1;
        }

        return this.fieldClass.getName().compareTo(o.fieldClass.getName());
    }

    public String getFormat() {
        return this.format;
    }

    public boolean isSerialize() {
        return serialize;
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
}
