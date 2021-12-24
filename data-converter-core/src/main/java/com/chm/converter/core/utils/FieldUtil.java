package com.chm.converter.core.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-18
 **/
public class FieldUtil {

    public static final Field[] EMPTY_FIELD_ARRAY = new Field[0];

    /**
     * 获取给定类及其父类（如果有）的所有字段。
     *
     * @param cls
     * @return
     */
    public static List<Field> getAllFieldsList(final Class<?> cls) {
        Assert.notNull(cls, "The class must not be null");
        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    /**
     * 获取给定类中带annotationCls注解的字段数组
     *
     * @param cls
     * @param annotationCls
     * @return
     */
    public static Field[] getFieldsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls) {
        return getFieldsWithAnnotation(cls, annotationCls, false, false);
    }

    /**
     * 获取给定类中带annotationCls注解的字段列表
     *
     * @param cls
     * @param annotationCls
     * @return
     */
    public static List<Field> getFieldsListWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls) {
        return getFieldsListWithAnnotation(cls, annotationCls, false, false);
    }

    /**
     * 获取给定类中带annotationCls注解的字段数组
     *
     * @param cls
     * @param annotationCls
     * @return
     */
    public static Field[] getFieldsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls, boolean searchSupers, boolean ignoreAccess) {
        final List<Field> annotatedFieldsList = getFieldsListWithAnnotation(cls, annotationCls, searchSupers, ignoreAccess);
        return annotatedFieldsList.toArray(EMPTY_FIELD_ARRAY);
    }

    /**
     * 获取给定类中带annotationCls注解的字段列表
     *
     * @param cls
     * @param annotationCls
     * @return
     */
    public static List<Field> getFieldsListWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls, boolean searchSupers, boolean ignoreAccess) {
        Assert.notNull(cls, "The class must not be null");
        Assert.notNull(annotationCls, "The annotation class must not be null");
        List<Class<?>> classes = searchSupers ? ClassUtil.getAllSuperclassesAndInterfaces(cls) : new ArrayList();
        classes.add(0, cls);
        final List<Field> annotatedFields = new ArrayList<>();
        for (final Class<?> acls : classes) {
            final Field[] fields = (ignoreAccess ? acls.getDeclaredFields() : acls.getFields());
            for (final Field field : fields) {
                if (field.getAnnotation(annotationCls) != null) {
                    annotatedFields.add(field);
                }
            }
        }

        return annotatedFields;
    }

    /**
     * 获取静态字段值
     *
     * @param field 字段
     * @return 字段值
     * @throws UtilException 包装IllegalAccessException异常
     * @since 5.1.0
     */
    public static Object getStaticFieldValue(Field field) throws UtilException {
        return getFieldValue(null, field);
    }

    /**
     * 获取字段值
     *
     * @param obj   对象，static字段则此字段为null
     * @param field 字段
     * @return 字段值
     * @throws UtilException 包装IllegalAccessException异常
     */
    public static Object getFieldValue(Object obj, Field field) throws UtilException {
        if (null == field) {
            return null;
        }
        if (obj instanceof Class) {
            // 静态字段获取时对象为null
            obj = null;
        }

        setAccessible(field);
        Object result;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            throw new UtilException(e, "IllegalAccess for {}.{}", field.getDeclaringClass(), field.getName());
        }
        return result;
    }

    /**
     * 设置字段值
     *
     * @param obj   对象，如果是static字段，此参数为null
     * @param field 字段
     * @param value 值，值类型必须与字段类型匹配，不会自动转换对象类型
     * @throws UtilException UtilException 包装IllegalAccessException异常
     */
    public static void setFieldValue(Object obj, Field field, Object value) throws UtilException {
        Assert.notNull(field, "Field in [{}] not exist !", obj);

        final Class<?> fieldType = field.getType();
        if (null != value) {
            if (false == fieldType.isAssignableFrom(value.getClass())) {
                //对于类型不同的字段，尝试转换，转换失败则使用原对象类型
                final Object targetValue = Convert.convert(fieldType, value);
                if (null != targetValue) {
                    value = targetValue;
                }
            }
        } else {
            // 获取null对应默认值，防止原始类型造成空指针问题
            value = ClassUtil.getDefaultValue(fieldType);
        }

        setAccessible(field);
        try {
            field.set(obj instanceof Class ? null : obj, value);
        } catch (IllegalAccessException e) {
            throw new UtilException(e, "IllegalAccess for {}.{}", obj, field.getName());
        }
    }


    /**
     * 设置方法为可访问（私有方法可以被外部调用）
     *
     * @param <T>              AccessibleObject的子类，比如Class、Method、Field等
     * @param accessibleObject 可设置访问权限的对象，比如Class、Method、Field等
     * @return 被设置可访问的对象
     * @since 4.6.8
     */
    public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        if (null != accessibleObject && false == accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }
}
