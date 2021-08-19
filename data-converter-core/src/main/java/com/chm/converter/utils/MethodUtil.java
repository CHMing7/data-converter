package com.chm.converter.utils;

import cn.hutool.core.lang.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-18
 **/
public class MethodUtil {

    public static final Method[] EMPTY_METHOD_ARRAY = new Method[0];

    /**
     * 获取给定类中带annotationCls注解的方法数组
     *
     * @param cls
     * @param annotationCls
     * @return
     */
    public static Method[] getMethodsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls) {
        return getMethodsWithAnnotation(cls, annotationCls, false, false);
    }

    /**
     * 获取给定类中带annotationCls注解的方法列表
     *
     * @param cls
     * @param annotationCls
     * @return
     */
    public static List<Method> getMethodsListWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls) {
        return getMethodsListWithAnnotation(cls, annotationCls, false, false);
    }

    /**
     * 获取给定类中带annotationCls注解的方法数组
     *
     * @param cls
     * @param annotationCls
     * @param searchSupers
     * @param ignoreAccess
     * @return
     */
    public static Method[] getMethodsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls, boolean searchSupers, boolean ignoreAccess) {
        List<Method> annotatedMethodsList = getMethodsListWithAnnotation(cls, annotationCls, searchSupers, ignoreAccess);
        return annotatedMethodsList.toArray(EMPTY_METHOD_ARRAY);
    }

    /**
     * 获取给定类中带annotationCls注解的方法列表
     *
     * @param cls
     * @param annotationCls
     * @param searchSupers
     * @param ignoreAccess
     * @return
     */
    public static List<Method> getMethodsListWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls, boolean searchSupers, boolean ignoreAccess) {
        Assert.notNull(cls, "The class must not be null");
        Assert.notNull(annotationCls, "The annotation class must not be null");
        List<Class<?>> classes = searchSupers ? ClassUtil.getAllSuperclassesAndInterfaces(cls) : new ArrayList();
        classes.add(0, cls);
        List<Method> annotatedMethods = new ArrayList();

        for (final Class<?> acls : classes) {
            final Method[] methods = (ignoreAccess ? acls.getDeclaredMethods() : acls.getMethods());
            for (final Method method : methods) {
                if (method.getAnnotation(annotationCls) != null) {
                    annotatedMethods.add(method);
                }
            }
        }
        return annotatedMethods;
    }


}
