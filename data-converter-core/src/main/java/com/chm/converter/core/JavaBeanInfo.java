package com.chm.converter.core;

import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.creator.ObjectConstructor;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.CollStreamUtil;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.core.utils.StringUtil;
import com.chm.converter.core.utils.TypeUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
public class JavaBeanInfo<T> {

    private final Class<T> clazz;

    /**
     * 构造方法对象
     */
    private final ObjectConstructor<T> objectConstructor;

    private final List<FieldInfo> fieldList;

    private final List<FieldInfo> sortedFieldList;

    private final Map<String, FieldInfo> nameFieldInfoMap;

    private final Map<String, FieldInfo> fieldNameFieldInfoMap;

    /**
     * 字段别名键值对
     */
    private final Map<String, String> fieldNameAliasMap;

    /**
     * 扩展属性
     */
    private final Map<String, Object> expandProperty;

    /**
     * 注解集
     */
    private final List<Annotation> annotationList;

    /**
     * 注解类型集
     */
    private final Set<Class<? extends Annotation>> annotationClassSet;

    public JavaBeanInfo(Class<T> clazz, List<FieldInfo> fieldList) {
        this.clazz = clazz;
        this.objectConstructor = ConstructorFactory.INSTANCE.get(TypeToken.get(clazz));
        this.fieldList = fieldList;
        this.sortedFieldList = ListUtil.toList(fieldList);
        CollUtil.sort(sortedFieldList, FieldInfo::compareTo);
        for (int i = 0; i < sortedFieldList.size(); i++) {
            FieldInfo fieldInfo = sortedFieldList.get(i);
            fieldInfo.setSortedNumber(i);
        }
        this.nameFieldInfoMap = CollStreamUtil.toMap(fieldList, FieldInfo::getName, Function.identity());
        this.fieldNameFieldInfoMap = CollStreamUtil.toMap(fieldList, FieldInfo::getFieldName, Function.identity());
        this.fieldNameAliasMap = CollStreamUtil.toMap(fieldList, FieldInfo::getFieldName, FieldInfo::getName);
        this.expandProperty = MapUtil.newConcurrentHashMap();
        this.annotationList = ListUtil.toList(clazz.getAnnotations());
        this.annotationClassSet = this.annotationList.stream().map(Annotation::annotationType).collect(Collectors.toSet());
    }

    /**
     * 检查类中是否包含annotationList中任意一个注解
     *
     * @param cls
     * @param annotationList
     * @return
     */
    public static <T> boolean checkExistAnnotation(Class<T> cls, List<Class<? extends Annotation>> annotationList) {
        JavaBeanInfo<T> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(cls, (Class<? extends Converter>) null);
        List<FieldInfo> fieldList = javaBeanInfo.getFieldList();
        Set<Class<? extends Annotation>> annotationClassSet = javaBeanInfo.getAnnotationClassSet();
        for (Class<? extends Annotation> annotation : annotationList) {
            if (annotationClassSet.contains(annotation)) {
                return true;
            }
        }
        for (FieldInfo fieldInfo : fieldList) {
            if (FieldInfo.checkExistAnnotation(fieldInfo, annotationList)) {
                return true;
            }
        }
        return false;
    }

    private static boolean add(List<FieldInfo> fieldList, FieldInfo field) {
        for (int i = fieldList.size() - 1; i >= 0; --i) {
            FieldInfo item = fieldList.get(i);

            if (item.name.equals(field.name)) {
                if (item.getOnly && !field.getOnly) {
                    continue;
                }

                if (item.getFieldClass().isAssignableFrom(field.getFieldClass())) {
                    fieldList.set(i, field);
                    return true;
                }

                int result = item.compareTo(field);

                if (result < 0) {
                    fieldList.set(i, field);
                    return true;
                } else {
                    return false;
                }
            }
        }
        fieldList.add(field);

        return true;
    }

    private static FieldInfo getField(List<FieldInfo> fieldList, String propertyName) {
        for (FieldInfo item : fieldList) {
            if (item.name.equals(propertyName)) {
                return item;
            }
            Field field = item.field;
            if (field != null && item.getAnnotation() != null && field.getName().equals(propertyName)) {
                return item;
            }
        }
        return null;
    }

    private static <T> void computeFields(Class<T> clazz, List<FieldInfo> fieldList, Field[] fields, Class<? extends Converter> scope) {
        for (Field field : fields) {
            // public static fields
            int modifiers = field.getModifiers();
            if ((modifiers & Modifier.STATIC) != 0) {
                continue;
            }

            if ((modifiers & Modifier.FINAL) != 0) {
                Class<?> fieldType = field.getType();
                boolean supportReadOnly = Map.class.isAssignableFrom(fieldType)
                        || Collection.class.isAssignableFrom(fieldType)
                        || AtomicLong.class.equals(fieldType)
                        || AtomicInteger.class.equals(fieldType)
                        || AtomicBoolean.class.equals(fieldType);
                if (!supportReadOnly) {
                    continue;
                }
            }

            boolean contains = false;
            for (FieldInfo item : fieldList) {
                if (item.getName().equals(field.getName())) {
                    contains = true;
                    // 已经是 contains = true，无需继续遍历
                    break;
                }
                if (item.getField().getName().equals(field.getName())) {
                    contains = true;
                    // 已经是 contains = true，无需继续遍历
                    break;
                }
            }

            if (contains) {
                continue;
            }

            int ordinal = 0;
            String propertyName = field.getName();

            FieldProperty fieldAnnotation = checkScope(TypeUtil.getAnnotation(field, FieldProperty.class), scope);

            if (fieldAnnotation != null) {
                ordinal = fieldAnnotation.ordinal();

                if (fieldAnnotation.name().length() != 0) {
                    propertyName = fieldAnnotation.name();
                }
            }

            Method method = TypeUtil.getMethod(clazz, field.getName());

            add(fieldList, new FieldInfo(propertyName, method, field, ordinal, fieldAnnotation, null));
        }
    }

    private static void computeEnumFields(List<FieldInfo> fieldList, Field[] fields, Class<? extends Converter> scope) {
        for (Field field : fields) {
            boolean contains = false;
            for (FieldInfo item : fieldList) {
                if (item.getName().equals(field.getName())) {
                    contains = true;
                    // 已经是 contains = true，无需继续遍历
                    break;
                }
            }

            if (contains) {
                continue;
            }

            int ordinal = 0;
            String propertyName = field.getName();

            FieldProperty fieldAnnotation = checkScope(TypeUtil.getAnnotation(field, FieldProperty.class), scope);

            if (fieldAnnotation != null) {
                ordinal = fieldAnnotation.ordinal();

                if (fieldAnnotation.name().length() != 0) {
                    propertyName = fieldAnnotation.name();
                }
            }

            add(fieldList, new FieldInfo(propertyName, null, field, ordinal, fieldAnnotation, null));
        }
    }

    /**
     * 返回匹配scope的第一个FieldProperty
     *
     * @param fieldPropertys
     * @param scope
     * @return
     */
    public static FieldProperty checkScope(FieldProperty[] fieldPropertys, Class<? extends Converter> scope) {
        if (scope == null && fieldPropertys != null && fieldPropertys.length >= 1) {
            return fieldPropertys[0];
        }
        if (fieldPropertys != null) {
            for (FieldProperty fieldProperty : fieldPropertys) {
                Class<? extends Converter>[] annotationScopes = fieldProperty.scope();
                for (Class<? extends Converter> annotationScope : annotationScopes) {
                    if (annotationScope.isAssignableFrom(scope)) {
                        return fieldProperty;
                    }
                }
            }
        }
        return null;
    }

    public static <T> JavaBeanInfo<T> build(Class<T> clazz, Class<? extends Converter> scope) {
        Method[] methods = clazz.getMethods();

        List<FieldInfo> fieldList = ListUtil.list(true);
        for (Method method : methods) {
            int ordinal = 0;
            String methodName = method.getName();

            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            // support builder set
            Class<?> returnType = method.getReturnType();
            if (!(returnType.equals(Void.TYPE) || returnType.equals(method.getDeclaringClass()))) {
                continue;
            }

            if (method.getDeclaringClass() == Object.class) {
                continue;
            }

            Class<?>[] types = method.getParameterTypes();

            if (types.length == 0 || types.length > 2) {
                continue;
            }

            if (types.length != 1) {
                continue;
            }

            FieldProperty annotation = checkScope(TypeUtil.getAnnotation(method, FieldProperty.class), scope);

            if (annotation == null) {
                annotation = checkScope(TypeUtil.getSuperMethodAnnotation(clazz, method, FieldProperty.class), scope);
            }

            if (annotation == null && methodName.length() < 4) {
                continue;
            }

            if (annotation != null) {

                ordinal = annotation.ordinal();

                if (annotation.name().length() != 0) {
                    String propertyName = annotation.name();
                    Field field = TypeUtil.getField(clazz, StringUtil.getGeneralField(methodName));
                    add(fieldList, new FieldInfo(propertyName, method, field, ordinal, null, annotation));
                    continue;
                }
            }

            if (annotation == null && !methodName.startsWith("set")) {
                continue;
            }

            char c3 = methodName.charAt(3);

            String propertyName;
            Field field = null;
            if (Character.isUpperCase(c3) || c3 > 512) {
                if (TypeUtil.compatibleWithJavaBean) {
                    propertyName = TypeUtil.decapitalize(methodName.substring(3));
                } else {
                    propertyName = StringUtil.getGeneralField(methodName);
                }
            } else if (c3 == '_') {
                propertyName = methodName.substring(4);
                field = TypeUtil.getField(clazz, propertyName);
                if (field == null) {
                    String temp = propertyName;
                    propertyName = methodName.substring(3);
                    field = TypeUtil.getField(clazz, propertyName);
                    if (field == null) {
                        //减少修改代码带来的影响
                        propertyName = temp;
                    }
                }
            } else if (c3 == 'f') {
                propertyName = methodName.substring(3);
            } else if (methodName.length() >= 5 && Character.isUpperCase(methodName.charAt(4))) {
                propertyName = TypeUtil.decapitalize(methodName.substring(3));
            } else {
                propertyName = methodName.substring(3);
                field = TypeUtil.getField(clazz, propertyName);
                if (field == null) {
                    continue;
                }
            }

            if (field == null) {
                field = TypeUtil.getField(clazz, propertyName);
            }

            if (field == null && types[0] == boolean.class) {
                String isFieldName = "is" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
                field = TypeUtil.getField(clazz, isFieldName);
            }

            FieldProperty fieldAnnotation = null;
            if (field != null) {
                fieldAnnotation = checkScope(TypeUtil.getAnnotation(field, FieldProperty.class), scope);

                if (fieldAnnotation != null) {
                    ordinal = fieldAnnotation.ordinal();

                    if (fieldAnnotation.name().length() != 0) {
                        propertyName = fieldAnnotation.name();
                        add(fieldList, new FieldInfo(propertyName, method, field, ordinal, fieldAnnotation, null));
                        continue;
                    }
                }
            }

            add(fieldList, new FieldInfo(propertyName, method, field, ordinal, fieldAnnotation, null));
        }

        Field[] fields = clazz.getFields();
        if (clazz.isEnum()) {
            computeEnumFields(fieldList, fields, scope);
        } else {
            computeFields(clazz, fieldList, fields, scope);

            for (Method method : clazz.getMethods()) {
                // getter methods
                String methodName = method.getName();
                if (methodName.length() < 4) {
                    continue;
                }

                if (Modifier.isStatic(method.getModifiers())) {
                    continue;
                }

                if (methodName.startsWith("get") && Character.isUpperCase(methodName.charAt(3))) {
                    if (method.getParameterTypes().length != 0) {
                        continue;
                    }

                    if (Collection.class.isAssignableFrom(method.getReturnType())
                            || Map.class.isAssignableFrom(method.getReturnType())
                            || AtomicBoolean.class == method.getReturnType()
                            || AtomicInteger.class == method.getReturnType()
                            || AtomicLong.class == method.getReturnType()) {
                        String propertyName;
                        Field collectionField = null;

                        FieldProperty annotation = checkScope(TypeUtil.getAnnotation(method, FieldProperty.class), scope);

                        if (annotation != null && annotation.name().length() > 0) {
                            propertyName = annotation.name();
                        } else {
                            propertyName = StringUtil.getGeneralField(methodName);

                            Field field = TypeUtil.getField(clazz, propertyName);
                            if (field != null) {
                                if (Collection.class.isAssignableFrom(method.getReturnType())
                                        || Map.class.isAssignableFrom(method.getReturnType())) {
                                    collectionField = field;
                                }
                            }
                        }

                        FieldInfo fieldInfo = getField(fieldList, propertyName);
                        if (fieldInfo != null) {
                            continue;
                        }

                        add(fieldList, new FieldInfo(propertyName, method, collectionField, 0, null, annotation));
                    }
                }
            }
        }

        return new JavaBeanInfo<>(clazz, fieldList);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public ObjectConstructor<T> getObjectConstructor() {
        return objectConstructor;
    }

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    public List<FieldInfo> getSortedFieldList() {
        return sortedFieldList;
    }

    public Map<String, FieldInfo> getNameFieldInfoMap() {
        return nameFieldInfoMap;
    }

    public Map<String, FieldInfo> getFieldNameFieldInfoMap() {
        return fieldNameFieldInfoMap;
    }

    public Map<String, String> getFieldNameAliasMap() {
        return fieldNameAliasMap;
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

    public List<Annotation> getAnnotationList() {
        return annotationList;
    }

    public Set<Class<? extends Annotation>> getAnnotationClassSet() {
        return annotationClassSet;
    }
}

