package com.chm.converter.utils;

import cn.hutool.core.util.StrUtil;
import com.chm.converter.DataType;
import com.chm.converter.annotation.AliasFor;
import com.chm.converter.json.JsonConverter;
import com.chm.converter.json.JsonConverterSelector;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author caihongming
 * @version v1.0
 * @since 2020-12-31
 **/
public final class ReflectUtils {


    private static JsonConverter FORM_MAP_CONVERTER;

    /**
     * 被排除调注解方法名集合
     */
    private static final Set<String> EXCLUDED_ANNTOTATION_METHOD_NAMES = new HashSet<>();

    static {
        EXCLUDED_ANNTOTATION_METHOD_NAMES.add("equals");
        EXCLUDED_ANNTOTATION_METHOD_NAMES.add("getClass");
        EXCLUDED_ANNTOTATION_METHOD_NAMES.add("annotationType");
        EXCLUDED_ANNTOTATION_METHOD_NAMES.add("notify");
        EXCLUDED_ANNTOTATION_METHOD_NAMES.add("notifyAll");
        EXCLUDED_ANNTOTATION_METHOD_NAMES.add("wait");
        EXCLUDED_ANNTOTATION_METHOD_NAMES.add("hashCode");
        EXCLUDED_ANNTOTATION_METHOD_NAMES.add("toString");
        EXCLUDED_ANNTOTATION_METHOD_NAMES.add("newProxyInstance");
        EXCLUDED_ANNTOTATION_METHOD_NAMES.add("newProxyClass");
        EXCLUDED_ANNTOTATION_METHOD_NAMES.add("getInvocationHandler");
    }


    /**
     * 从Type获取Class
     *
     * @param genericType Java Type类型，{@link Type}接口实例
     * @return Java类，{@link Class}类实例
     */
    public static Class<?> getClassByType(Type genericType) {
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            return ((Class<?>) pt.getRawType());
        } else if (genericType instanceof TypeVariable) {
            TypeVariable tType = (TypeVariable) genericType;
            String className = tType.getGenericDeclaration().toString();
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException ignored) {
            }
            return null;
        } else {
            return (Class<?>) genericType;
        }
    }

    /**
     * 是否是Java基本类型
     *
     * @param type Java类，{@link Class}类实例
     * @return {@code true}：是基本类型，{@code false}：不是基本类型
     */
    public static boolean isPrimaryType(Class type) {
        if (byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)) {
            return true;
        }
        if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return true;
        }
        if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return true;
        }
        if (short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type)) {
            return true;
        }
        if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
            return true;
        }
        if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
            return true;
        }
        if (BigDecimal.class.isAssignableFrom(type)) {
            return true;
        }
        if (BigInteger.class.isAssignableFrom(type)) {
            return true;
        }
        if (CharSequence.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }

    /**
     * 是否为基本数组类型
     *
     * @param type Java类，{@link Class}类实例
     * @return {@code true}：是基本数组类型，{@code false}：不是基本数组类型
     */
    public static boolean isPrimaryArrayType(Class type) {
        if (!type.isArray()) {
            return false;
        }
        if (byte[].class.isAssignableFrom(type) || Byte[].class.isAssignableFrom(type)) {
            return true;
        }
        if (int[].class.isAssignableFrom(type) || Integer[].class.isAssignableFrom(type)) {
            return true;
        }
        if (long[].class.isAssignableFrom(type) || Long[].class.isAssignableFrom(type)) {
            return true;
        }
        if (short[].class.isAssignableFrom(type) || Short[].class.isAssignableFrom(type)) {
            return true;
        }
        if (float[].class.isAssignableFrom(type) || Float[].class.isAssignableFrom(type)) {
            return true;
        }
        if (double[].class.isAssignableFrom(type) || Double[].class.isAssignableFrom(type)) {
            return true;
        }
        if (BigDecimal[].class.isAssignableFrom(type)) {
            return true;
        }
        if (BigInteger[].class.isAssignableFrom(type)) {
            return true;
        }
        if (CharSequence[].class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }


    /**
     * 从注解对象中获取所有属性
     *
     * @param ann 注解对象，{@link Annotation}接口实例
     * @return 注解对象中有属性 {@link Map}表对象，Key：属性名 Value：属性值
     */
    public static Map<String, Object> getAttributesFromAnnotation(Annotation ann) {
        Map<String, Object> results = new HashMap<>();
        Class clazz = ann.annotationType();
        Method[] methods = clazz.getMethods();
        Object[] args = new Object[0];
        for (Method method : methods) {
            String name = method.getName();
            if (EXCLUDED_ANNTOTATION_METHOD_NAMES.contains(name)) {
                continue;
            }
            if (method.getParameters().length > 0) {
                continue;
            }
            Object value = invokeAnnotationMethod(ann, clazz, name, args);
            if (value == null ||
                    (value instanceof CharSequence && StrUtil.isEmpty(String.valueOf(value)))) {
                AliasFor aliasFor = method.getAnnotation(AliasFor.class);
                if (aliasFor != null) {
                    String aliasName = aliasFor.value();
                    value = invokeAnnotationMethod(ann, clazz, aliasName, args);
                }
            }
            results.put(name, value);
        }
        return results;
    }


    private static Object invokeAnnotationMethod(Annotation ann, Class clazz, String name, Object[] args) {
        Method method = null;
        try {
            method = clazz.getMethod(name, new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        if (method != null) {
            try {
                return method.invoke(ann, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }


    public static void copyAnnotationAttributes(Annotation source, Object target) {
        if (target == null) {
            return;
        }
        Map<String, Object> attrs = getAttributesFromAnnotation(source);
        Class targetClass = target.getClass();
        for (String name : attrs.keySet()) {
            String methodName = NameUtils.setterName(name);
            try {
                Method setterMethod = null;
                for (Method method : targetClass.getMethods()) {
                    if (method.getName().equals(methodName) && method.getParameterTypes().length == 1) {
                        setterMethod = method;
                        break;
                    }
                }
                if (setterMethod != null) {
                    setterMethod.invoke(target, attrs.get(name));
                }
            } catch (IllegalAccessException | InvocationTargetException ignored) {
            }
        }
    }


    public static Map convertObjectToMap(Object srcObj) {
        try {
            return ((JsonConverter) DataType.getConverterMap().get(DataType.JSON)).convertObjectToMap(srcObj);
        } catch (Exception e) {
            if (FORM_MAP_CONVERTER == null) {
                FORM_MAP_CONVERTER = JsonConverterSelector.select();
            }
            return FORM_MAP_CONVERTER.convertObjectToMap(srcObj);
        }
    }

}
