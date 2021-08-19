package com.chm.converter.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
public class TypeUtil {

    public static boolean compatibleWithJavaBean = false;

    /**
     * 获取当前属性中的注解，如果获取不到，就从父类集的属性中获取
     *
     * @param field
     * @param annotationClass
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A getAnnotation(Field field, Class<A> annotationClass) {
        A targetAnnotation = field.getAnnotation(annotationClass);

        Class<?> clazz = field.getDeclaringClass();
        A mixInAnnotation;

        Field mixInField = null;
        String fieldName = field.getName();
        // 递归从MixIn类的父类中查找注解（如果有父类的话）
        for (Class<?> currClass = clazz; currClass != null && currClass != Object.class;
             currClass = currClass.getSuperclass()) {
            try {
                mixInField = currClass.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // skip
            }
        }
        if (mixInField == null) {
            return targetAnnotation;
        }
        mixInAnnotation = mixInField.getAnnotation(annotationClass);
        if (mixInAnnotation != null) {
            return mixInAnnotation;
        }
        return targetAnnotation;
    }

    /**
     * 获取当前方法中的注解，如果获取不到，就从父类集的方法中获取
     *
     * @param method
     * @param annotationClass
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationClass) {
        A targetAnnotation = method.getAnnotation(annotationClass);

        Class<?> clazz = method.getDeclaringClass();
        A mixInAnnotation;
        if (clazz != Object.class) {
            Method mixInMethod = null;
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            // 递归从MixIn类的父类中查找注解（如果有父类的话）
            for (Class<?> currClass = clazz; currClass != null && currClass != Object.class;
                 currClass = currClass.getSuperclass()) {
                try {
                    mixInMethod = currClass.getDeclaredMethod(methodName, parameterTypes);
                    break;
                } catch (NoSuchMethodException e) {
                    // skip
                }
            }
            if (mixInMethod == null) {
                return targetAnnotation;
            }
            mixInAnnotation = mixInMethod.getAnnotation(annotationClass);
            if (mixInAnnotation != null) {
                return mixInAnnotation;
            }
        }
        return targetAnnotation;
    }

    /**
     * 从当前类中获取属性，如果获取不到就递归从父类中获取
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            String itemName = field.getName();
            if (fieldName.equals(itemName)) {
                return field;
            }

            char c0, c1;
            if (fieldName.length() > 2
                    && (c0 = fieldName.charAt(0)) >= 'a' && c0 <= 'z'
                    && (c1 = fieldName.charAt(1)) >= 'A' && c1 <= 'Z'
                    && fieldName.equalsIgnoreCase(itemName)) {
                return field;
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            return getField(superClass, fieldName);
        }
        return null;
    }

    /**
     * 从当前类的接口类中的同方法中获取注解，如果获取不到再从父类的接口类的同方法中获取
     *
     * @param clazz
     * @param method
     * @param annotationClass
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A getSuperMethodAnnotation(final Class<?> clazz, final Method method, Class<A> annotationClass) {
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            Class<?>[] types = method.getParameterTypes();
            for (Class<?> interfaceClass : interfaces) {
                for (Method interfaceMethod : interfaceClass.getMethods()) {
                    Class<?>[] interfaceTypes = interfaceMethod.getParameterTypes();
                    if (interfaceTypes.length != types.length) {
                        continue;
                    }
                    if (!interfaceMethod.getName().equals(method.getName())) {
                        continue;
                    }
                    boolean match = true;
                    for (int i = 0; i < types.length; ++i) {
                        if (!interfaceTypes[i].equals(types[i])) {
                            match = false;
                            break;
                        }
                    }
                    if (!match) {
                        continue;
                    }
                    A annotation = TypeUtil.getAnnotation(interfaceMethod, annotationClass);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass == null) {
            return null;
        }
        if (Modifier.isAbstract(superClass.getModifiers())) {
            Class<?>[] types = method.getParameterTypes();
            for (Method interfaceMethod : superClass.getMethods()) {
                Class<?>[] interfaceTypes = interfaceMethod.getParameterTypes();
                if (interfaceTypes.length != types.length) {
                    continue;
                }
                if (!interfaceMethod.getName().equals(method.getName())) {
                    continue;
                }
                boolean match = true;
                for (int i = 0; i < types.length; ++i) {
                    if (!interfaceTypes[i].equals(types[i])) {
                        match = false;
                        break;
                    }
                }
                if (!match) {
                    continue;
                }
                A annotation = TypeUtil.getAnnotation(interfaceMethod, annotationClass);
                if (annotation != null) {
                    return annotation;
                }
            }
        }
        return null;
    }

    public static String decapitalize(String name){
        if(name == null || name.length() == 0){
            return name;
        }
        if(name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))){
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
}
