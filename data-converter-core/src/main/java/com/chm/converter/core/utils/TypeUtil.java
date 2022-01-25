package com.chm.converter.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-16
 **/
public class TypeUtil {

    public static boolean compatibleWithJavaBean = false;

    /**
     * 获得Type对应的原始类
     *
     * @param type {@link Type}
     * @return 原始类，如果无法获取原始类，返回{@code null}
     */
    public static Class<?> getClass(Type type) {
        if (null != type) {
            if (type instanceof Class) {
                return (Class<?>) type;
            } else if (type instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) type).getRawType();
            } else if (type instanceof TypeVariable) {
                return (Class<?>) ((TypeVariable<?>) type).getBounds()[0];
            } else if (type instanceof WildcardType) {
                final Type[] upperBounds = ((WildcardType) type).getUpperBounds();
                if (upperBounds.length == 1) {
                    return getClass(upperBounds[0]);
                }
            }
        }
        return null;
    }

    /**
     * 获取当前属性中的注解，如果获取不到，就从父类集的属性中获取
     *
     * @param field
     * @param annotationClass
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A[] getAnnotation(Field field, Class<A> annotationClass) {
        A[] targetAnnotations = field.getAnnotationsByType(annotationClass);

        Class<?> clazz = field.getDeclaringClass();

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
            return targetAnnotations;
        }
        return mixInField.getAnnotationsByType(annotationClass);
    }

    /**
     * 获取当前方法中的注解，如果获取不到，就从父类集的方法中获取
     *
     * @param method
     * @param annotationClass
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A[] getAnnotation(Method method, Class<A> annotationClass) {
        A[] targetAnnotations = method.getAnnotationsByType(annotationClass);

        Class<?> clazz = method.getDeclaringClass();
        A[] mixInAnnotation;
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
                return targetAnnotations;
            }
            mixInAnnotation = mixInMethod.getAnnotationsByType(annotationClass);
            if (mixInAnnotation != null) {
                return mixInAnnotation;
            }
        }
        return targetAnnotations;
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
    public static <A extends Annotation> A[] getSuperMethodAnnotation(final Class<?> clazz, final Method method, Class<A> annotationClass) {
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
                    A[] annotations = TypeUtil.getAnnotation(interfaceMethod, annotationClass);
                    if (annotations != null) {
                        return annotations;
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
                A[] annotations = TypeUtil.getAnnotation(interfaceMethod, annotationClass);
                if (annotations != null) {
                    return annotations;
                }
            }
        }
        return null;
    }

    public static String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }


    /**
     * 获得给定类的第一个泛型参数
     *
     * @param type 被检查的类型，必须是已经确定泛型类型的类型
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getTypeArgument(Type type) {
        return getTypeArgument(type, 0);
    }

    /**
     * 获得给定类的泛型参数
     *
     * @param type  被检查的类型，必须是已经确定泛型类型的类
     * @param index 泛型类型的索引号，即第几个泛型类型
     * @return {@link Type}
     */
    public static Type getTypeArgument(Type type, int index) {
        final Type[] typeArguments = getTypeArguments(type);
        if (null != typeArguments && typeArguments.length > index) {
            return typeArguments[index];
        }
        return null;
    }

    /**
     * 获得指定类型中所有泛型参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到String
     *
     * @param type 指定类型
     * @return 所有泛型参数类型
     */
    public static Type[] getTypeArguments(Type type) {
        if (null == type) {
            return null;
        }

        final ParameterizedType parameterizedType = toParameterizedType(type);
        return (null == parameterizedType) ? null : parameterizedType.getActualTypeArguments();
    }

    /**
     * 将{@link Type} 转换为{@link ParameterizedType}<br>
     * {@link ParameterizedType}用于获取当前类或父类中泛型参数化后的类型<br>
     * 一般用于获取泛型参数具体的参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到B{@link ParameterizedType}，从而获取到String
     *
     * @param type {@link Type}
     * @return {@link ParameterizedType}
     */
    public static ParameterizedType toParameterizedType(Type type) {
        ParameterizedType result = null;
        if (type instanceof ParameterizedType) {
            result = (ParameterizedType) type;
        } else if (type instanceof Class) {
            final Class<?> clazz = (Class<?>) type;
            Type genericSuper = clazz.getGenericSuperclass();
            if (null == genericSuper || Object.class.equals(genericSuper)) {
                // 如果类没有父类，而是实现一些定义好的泛型接口，则取接口的Type
                final Type[] genericInterfaces = clazz.getGenericInterfaces();
                if (ArrayUtil.isNotEmpty(genericInterfaces)) {
                    // 默认取第一个实现接口的泛型Type
                    genericSuper = genericInterfaces[0];
                }
            }
            result = toParameterizedType(genericSuper);
        }
        return result;
    }
}
