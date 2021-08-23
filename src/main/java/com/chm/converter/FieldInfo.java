package com.chm.converter;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.util.TypeUtils;
import com.chm.converter.annotation.FieldProperty;

import java.lang.reflect.*;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-05
 **/
public class FieldInfo implements Comparable<FieldInfo> {

    public final String name;

    public final Method method;

    public final Field field;

    private int ordinal = 0;
    public final Class<?> fieldClass;

    public final Type fieldType;

    public final Class<?> declaringClass;

    public final boolean getOnly;

    private final FieldProperty fieldAnnotation;

    private final FieldProperty methodAnnotation;

    public final String format;

    private final boolean isSetMethod;

    /**
     * 是否序列化
     */
    private final boolean serialize;

    /**
     * 是否反序列化
     */
    private final boolean deserialize;

    public FieldInfo(String name, Method method, Field field, int ordinal) {
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
        boolean isSetMethod = false;
        Type fieldType;
        Class<?> fieldClass;
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
            isSetMethod = method.getName().startsWith("set") && method.getParameterCount() == 1;
        } else {
            fieldClass = field.getType();
            fieldType = field.getGenericType();
            this.declaringClass = field.getDeclaringClass();
            getOnly = Modifier.isFinal(field.getModifiers());
        }

        this.name = name;
        this.method = method;
        this.field = field;
        this.ordinal = ordinal;
        this.fieldClass = fieldClass;
        this.fieldType = fieldType;
        this.getOnly = getOnly;
        this.fieldAnnotation = field == null ? null : TypeUtils.getAnnotation(field, FieldProperty.class);
        this.methodAnnotation = method == null ? null : TypeUtils.getAnnotation(method, FieldProperty.class);
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
        this.isSetMethod = isSetMethod;
        this.serialize = serialize;
        this.deserialize = deserialize;
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return this.method != null ? StrUtil.getGeneralField(this.method.getName()) : this.field.getName();
    }

    public Member getMember() {
        return this.method != null ? this.method : this.field;
    }

    public Class<?> getFieldClass(){
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
        return this.fieldAnnotation != null ? this.fieldAnnotation : this.methodAnnotation;
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
        return this.method != null && !isSetMethod ? ReflectUtil.invoke(javaObject, method) : ReflectUtil.getFieldValue(javaObject, field);
    }

    public void set(Object javaObject, Object value) {
        if (this.method != null && isSetMethod) {
            ReflectUtil.invoke(javaObject, method, value);
        } else {
            ReflectUtil.setFieldValue(javaObject, field, value);
        }
    }
}
