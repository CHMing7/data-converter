package com.chm.converter.core.utils;

import cn.hutool.core.lang.Assert;

import java.lang.annotation.Annotation;
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
}
