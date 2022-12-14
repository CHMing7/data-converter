package com.chm.converter.core;

import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.io.Reader;
import com.chm.converter.core.io.Writer;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.AnnotationUtil;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.core.utils.ReflectUtil;
import com.chm.converter.core.utils.StringUtil;

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
 * @since 2021-08-16
 **/
public class FieldInfo implements Comparable<FieldInfo> {

    public static final FieldInfo STOP = new FieldInfo("stop", null, FieldInfo.class.getFields()[0], -1, null, null) {
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
     * ????????????????????????
     */
    public final String name;

    public final Method method;

    public final Method setter;

    public final Method getter;

    public final Field field;

    private int ordinal = 0;

    /**
     * ????????????
     */
    private int sortedNumber = 0;

    public final Class<?> fieldClass;

    public final Type fieldType;

    public final TypeToken<?> typeToken;

    public final Class<?> declaringClass;

    public final boolean getOnly;

    private final FieldProperty fieldAnnotation;

    private final FieldProperty methodAnnotation;

    public final String format;

    /**
     * ???????????????
     */
    private final boolean serialize;

    /**
     * ??????????????????
     */
    private final boolean deserialize;

    /**
     * ??????????????????
     */
    private final boolean isTransient;

    /**
     * ????????????
     */
    private final Map<String, Object> expandProperty;

    /**
     * ???????????????
     */
    private final List<Annotation> fieldAnnotationList;

    /**
     * ?????????????????????
     */
    private final Set<Class<? extends Annotation>> fieldAnnotationClassSet;

    /**
     * ???????????????
     */
    private final List<Annotation> methodAnnotationList;

    /**
     * ?????????????????????
     */
    private final Set<Class<? extends Annotation>> methodAnnotationClassSet;

    public FieldInfo(String name, Method method, Field field, int ordinal, FieldProperty fieldAnnotation, FieldProperty methodAnnotation) {
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
        // ????????????getter setter
        final boolean isBooleanField = ClassUtil.isBoolean(fieldClass);
        Method[] methods = this.declaringClass.getMethods();
        String methodName;
        Class<?>[] parameterTypes;
        for (Method getOrSetMethod : methods) {
            parameterTypes = getOrSetMethod.getParameterTypes();
            if (parameterTypes.length > 1) {
                // ??????1??????????????????Getter???Setter
                continue;
            }

            methodName = getOrSetMethod.getName();
            if (parameterTypes.length == 0) {
                // ?????????????????????Getter??????
                if (isMatchGetter(methodName, fieldName, isBooleanField)) {
                    // ????????????????????????????????????Getter??????
                    getter = getOrSetMethod;
                }
            } else if (isMatchSetter(methodName, fieldName, isBooleanField)) {
                // ????????????????????????????????????????????????????????????????????????Setter??????
                setter = getOrSetMethod;
            }
            if (null != getter && null != setter) {
                // ??????Getter???Setter???????????????????????????????????????
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
     * ??????set???get???is??????????????????????????????<br>
     * ?????????setName ?????? name
     *
     * <pre>
     * getName =???name
     * setName =???name
     * isName  =???name
     * </pre>
     *
     * @param getOrSetMethodName Get???Set?????????
     * @return ?????????set???get??????????????????field??? ??????null
     */
    public static String getGeneralField(CharSequence getOrSetMethodName) {
        // ????????????????????????????????????
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
     * ???????????????Getter??????<br>
     * ??????????????????????????????????????????
     *
     * <pre>
     * ?????????    -??? ?????????
     * isName  -??? isName
     * isName  -??? isIsName
     * isName  -??? getIsName
     * name     -??? isName
     * name     -??? getName
     * </pre>
     *
     * @param methodName     ?????????
     * @param fieldName      ?????????
     * @param isBooleanField ?????????Boolean????????????
     * @return ????????????
     */
    private boolean isMatchGetter(String methodName, String fieldName, boolean isBooleanField) {
        // ????????????????????????????????????
        methodName = StringUtil.toCamelCase(methodName);
        final String handledFieldName = StringUtil.upperFirst(fieldName);

        if (!methodName.startsWith("get") && !methodName.startsWith("is")) {
            // ?????????Getter??????
            return false;
        }
        if ("getclass".equals(methodName)) {
            //??????getClass??????
            return false;
        }

        // ??????Boolean??????????????????
        if (isBooleanField) {
            if (fieldName.startsWith("is")) {
                // ???????????????is??????
                // isName -??? isName
                if (methodName.equals(fieldName)
                        // isName -??? getIsName
                        || methodName.equals("get" + handledFieldName)
                        // isName -??? isIsName
                        || methodName.equals("is" + handledFieldName)
                ) {
                    return true;
                }
            } else if (methodName.equals("is" + handledFieldName)) {
                // ?????????is????????? name -??? isName
                return true;
            }
        }

        // ??????boolean??????????????????????????????????????????name -??? getName
        return methodName.equals("get" + handledFieldName);
    }

    /**
     * ???????????????Setter??????<br>
     * ??????????????????????????????????????????
     *
     * <pre>
     * ?????????    -??? ?????????
     * isName  -??? setName
     * isName  -??? setIsName
     * name     -??? setName
     * </pre>
     *
     * @param methodName     ?????????
     * @param fieldName      ?????????
     * @param isBooleanField ?????????Boolean????????????
     * @return ????????????
     */
    private boolean isMatchSetter(String methodName, String fieldName, boolean isBooleanField) {
        // ????????????????????????????????????
        methodName = StringUtil.toCamelCase(methodName);
        final String handledFieldName = StringUtil.upperFirst(fieldName);

        // ?????????Setter????????????
        if (!methodName.startsWith("set")) {
            return false;
        }

        // ??????Boolean??????????????????
        if (isBooleanField && fieldName.startsWith("is")) {
            // ?????????is??????
            // isName -??? setName
            if (methodName.equals("set" + StringUtil.removePrefix(fieldName, "is"))
                    // isName -??? setIsName
                    || methodName.equals("set" + handledFieldName)
            ) {
                return true;
            }
        }

        // ??????boolean??????????????????????????????????????????name -??? setName
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

    /**
     * ??????fieldInfo???????????????annotationList?????????????????????
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
