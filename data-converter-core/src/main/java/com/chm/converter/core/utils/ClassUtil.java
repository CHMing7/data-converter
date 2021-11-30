package com.chm.converter.core.utils;

import java.lang.reflect.*;
import java.net.URI;
import java.net.URL;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-18
 **/
public class ClassUtil {

    /**
     * {@code null}安全的获取对象类型
     *
     * @param <T> 对象类型
     * @param obj 对象，如果为{@code null} 返回{@code null}
     * @return 对象类型，提供对象如果为{@code null} 返回{@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(T obj) {
        return ((null == obj) ? null : (Class<T>) obj.getClass());
    }

    /**
     * 是否为数字类型
     *
     * @param clazz 类
     * @return 是否为包装类型
     */
    public static boolean isNumberClass(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return Number.class.isAssignableFrom(clazz)
                || byte.class.equals(clazz)
                || double.class.equals(clazz)
                || float.class.equals(clazz)
                || int.class.equals(clazz)
                || long.class.equals(clazz)
                || short.class.equals(clazz);
    }

    /**
     * 是否为包装类型
     *
     * @param clazz 类
     * @return 是否为包装类型
     */
    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return BasicType.WRAPPER_PRIMITIVE_MAP.containsKey(clazz);
    }

    /**
     * 是否为基本类型（包括包装类和原始类）
     *
     * @param clazz 类
     * @return 是否为基本类型
     */
    public static boolean isBasicType(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    /**
     * 是否简单值类型或简单值类型的数组<br>
     * 包括：原始类型,、String、other CharSequence, a Number, a Date, a URI, a URL, a Locale or a Class及其数组
     *
     * @param clazz 属性类
     * @return 是否简单值类型或简单值类型的数组
     */
    public static boolean isSimpleTypeOrArray(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return isSimpleValueType(clazz) || (clazz.isArray() && isSimpleValueType(clazz.getComponentType()));
    }

    /**
     * 是否为简单值类型<br>
     * 包括：
     * <pre>
     *     原始类型
     *     String、other CharSequence
     *     Number
     *     Date
     *     URI
     *     URL
     *     Locale
     *     Class
     * </pre>
     *
     * @param clazz 类
     * @return 是否为简单值类型
     */
    public static boolean isSimpleValueType(Class<?> clazz) {
        return isBasicType(clazz)
                || clazz.isEnum()
                || CharSequence.class.isAssignableFrom(clazz)
                || Number.class.isAssignableFrom(clazz)
                || Date.class.isAssignableFrom(clazz)
                || clazz.equals(URI.class)
                || clazz.equals(URL.class)
                || clazz.equals(Locale.class)
                || clazz.equals(Class.class)
                // jdk8 date object
                || TemporalAccessor.class.isAssignableFrom(clazz);
    }

    public static List<Class<?>> getAllSuperclassesAndInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }

        final List<Class<?>> allSuperClassesAndInterfaces = new ArrayList<>();
        final List<Class<?>> allSuperclasses = getAllSuperclasses(cls);
        int superClassIndex = 0;

        final List<Class<?>> allInterfaces = getAllInterfaces(cls);
        int interfaceIndex = 0;
        while (interfaceIndex < allInterfaces.size() ||
                superClassIndex < allSuperclasses.size()) {
            Class<?> acls;
            if (interfaceIndex >= allInterfaces.size()) {
                acls = allSuperclasses.get(superClassIndex++);
            } else if ((superClassIndex >= allSuperclasses.size()) || (interfaceIndex < superClassIndex) || !(superClassIndex < interfaceIndex)) {
                acls = allInterfaces.get(interfaceIndex++);
            } else {
                acls = allSuperclasses.get(superClassIndex++);
            }
            allSuperClassesAndInterfaces.add(acls);
        }
        return allSuperClassesAndInterfaces;
    }

    public static List<Class<?>> getAllInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }

        final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
        getAllInterfaces(cls, interfacesFound);

        return new ArrayList<>(interfacesFound);
    }

    private static void getAllInterfaces(Class<?> cls, final HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            final Class<?>[] interfaces = cls.getInterfaces();

            for (final Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
        }
    }

    public static List<Class<?>> getAllSuperclasses(final Class<?> cls) {
        if (cls == null) {
            return null;
        }
        final List<Class<?>> classes = new ArrayList<>();
        Class<?> superclass = cls.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    /**
     * 从Type获取Class
     *
     * @param type Java Type类型，{@link Type}接口实例
     * @return Java类，{@link Class}类实例
     */
    public static Class<?> getClassByType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }

        if (type instanceof WildcardType) {
            // Forget lower bounds and only deal with first upper bound...
            Type[] ubs = ((WildcardType) type).getUpperBounds();
            if (ubs.length > 0) {
                return getClassByType(ubs[0]);
            }
        }
        if (type instanceof GenericArrayType) {
            Class<?> ct = getClassByType(((GenericArrayType) type).getGenericComponentType());
            return (ct != null ? Array.newInstance(ct, 0).getClass() : Object[].class);
        }
        if (type instanceof TypeVariable<?>) {
            // Only deal with first (upper) bound...
            Type[] ubs = ((TypeVariable<?>) type).getBounds();
            if (ubs.length > 0) {
                return getClassByType(ubs[0]);
            }
        }
        // Should never append...
        return Object.class;
    }

    public static boolean isJdk(Class clazz) {
        return clazz.getName().startsWith("java.") || clazz.getName().startsWith("javax.");
    }

    public static boolean checkZeroArgConstructor(Class clazz) {
        try {
            clazz.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
