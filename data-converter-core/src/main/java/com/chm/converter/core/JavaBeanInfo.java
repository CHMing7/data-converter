package com.chm.converter.core;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.chm.converter.annotation.FieldProperty;
import com.chm.converter.creator.ConstructorFactory;
import com.chm.converter.creator.ObjectConstructor;
import com.chm.converter.reflect.TypeToken;
import com.chm.converter.utils.TypeUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
public class JavaBeanInfo {

    private final Class<?> clazz;

    /**
     * 构造方法对象
     */
    private final ObjectConstructor<?> objectConstructor;

    private final List<FieldInfo> fieldList;

    private final List<FieldInfo> sortedFieldList;

    private final Map<String, FieldInfo> nameFieldInfoMap;

    private final Map<String, FieldInfo> fieldNameFieldInfoMap;

    /**
     * 扩展属性
     */
    private final Map<String, Object> expandProperty;

    public JavaBeanInfo(Class<?> clazz, List<FieldInfo> fieldList) {
        this.clazz = clazz;
        this.objectConstructor = ConstructorFactory.INSTANCE.get(TypeToken.get(clazz));
        this.fieldList = fieldList;
        this.sortedFieldList = ListUtil.toList(fieldList);
        CollectionUtil.sort(sortedFieldList, FieldInfo::compareTo);
        this.nameFieldInfoMap = CollStreamUtil.toMap(fieldList, FieldInfo::getName, fieldInfo -> fieldInfo);
        this.fieldNameFieldInfoMap = CollStreamUtil.toMap(fieldList, FieldInfo::getFieldName, fieldInfo -> fieldInfo);
        this.expandProperty = new ConcurrentHashMap<>();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public ObjectConstructor<?> getObjectConstructor() {
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

    /**
     * 检车类中是否包含annotationList中任意一个注解
     *
     * @param cls
     * @param annotationList
     * @return
     */
    public static boolean checkExistAnnotation(Class<?> cls, List<Class<? extends Annotation>> annotationList) {
        JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(cls);
        List<FieldInfo> fieldList = javaBeanInfo.getFieldList();
        for (FieldInfo fieldInfo : fieldList) {
            Field field = fieldInfo.getField();
            Method method = fieldInfo.getMethod();
            for (Class<? extends Annotation> annotation : annotationList) {
                if (field != null && field.getAnnotation(annotation) != null) {
                    return true;
                }
                if (method != null && method.getAnnotation(annotation) != null) {
                    return true;
                }
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

    private static void computeFields(List<FieldInfo> fieldList, Field[] fields) {
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
                if (item.name.equals(field.getName())) {
                    contains = true;
                    break; // 已经是 contains = true，无需继续遍历
                }
            }

            if (contains) {
                continue;
            }

            int ordinal = 0;
            String propertyName = field.getName();

            FieldProperty fieldAnnotation = TypeUtil.getAnnotation(field, FieldProperty.class);

            if (fieldAnnotation != null) {
                ordinal = fieldAnnotation.ordinal();

                if (fieldAnnotation.name().length() != 0) {
                    propertyName = fieldAnnotation.name();
                }
            }

            add(fieldList, new FieldInfo(propertyName, null, field, ordinal));
        }
    }

    public static JavaBeanInfo build(Class<?> clazz) {
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

            FieldProperty annotation = TypeUtil.getAnnotation(method, FieldProperty.class);

            if (types.length != 1) {
                continue;
            }

            if (annotation == null) {
                annotation = TypeUtil.getSuperMethodAnnotation(clazz, method, FieldProperty.class);
            }

            if (annotation == null && methodName.length() < 4) {
                continue;
            }

            if (annotation != null) {

                ordinal = annotation.ordinal();

                if (annotation.name().length() != 0) {
                    String propertyName = annotation.name();
                    Field field = TypeUtil.getField(clazz, StrUtil.getGeneralField(methodName));
                    add(fieldList, new FieldInfo(propertyName, method, field, ordinal));
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
                    propertyName = StrUtil.getGeneralField(methodName);
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

            FieldProperty fieldAnnotation;
            if (field != null) {
                fieldAnnotation = TypeUtil.getAnnotation(field, FieldProperty.class);

                if (fieldAnnotation != null) {
                    ordinal = fieldAnnotation.ordinal();

                    if (fieldAnnotation.name().length() != 0) {
                        propertyName = fieldAnnotation.name();
                        add(fieldList, new FieldInfo(propertyName, method, field, ordinal));
                        continue;
                    }
                }
            }

            add(fieldList, new FieldInfo(propertyName, method, field, ordinal));
        }

        Field[] fields = clazz.getFields();
        computeFields(fieldList, fields);

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
                        || AtomicLong.class == method.getReturnType()
                ) {
                    String propertyName;
                    Field collectionField = null;

                    FieldProperty annotation = TypeUtil.getAnnotation(method, FieldProperty.class);

                    if (annotation != null && annotation.name().length() > 0) {
                        propertyName = annotation.name();
                    } else {
                        propertyName = StrUtil.getGeneralField(methodName);

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

                    add(fieldList, new FieldInfo(propertyName, method, collectionField, 0));
                }
            }
        }
        return new JavaBeanInfo(clazz, fieldList);
    }
}

