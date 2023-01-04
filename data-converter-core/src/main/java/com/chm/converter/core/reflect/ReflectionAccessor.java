package com.chm.converter.core.reflect;

import java.lang.reflect.AccessibleObject;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-07
 **/
public abstract class ReflectionAccessor {

    private static final ReflectionAccessor INSTANCE = JavaVersion.getMajorJavaVersion() < 9 ? new PreJava9ReflectionAccessor() : new UnsafeReflectionAccessor();

    /**
     * 获取适用于当前 Java 版本的 {@link ReflectionAccessor} 实例。
     * 您可能需要在代码中抛出一个反射操作 {@link java.lang.reflect.InaccessibleObjectException}。
     * 在这种情况下，在字段、方法或构造函数上使用 {@link ReflectionAccessor#makeAccessible(AccessibleObject)}
     * （而不是基本的 {@link AccessibleObject#setAccessible(boolean)}）。
     *
     * @return {@link ReflectionAccessor}
     */
    public static ReflectionAccessor getInstance() {
        return INSTANCE;
    }

    /**
     * 跟 {@code ao.setAccessible(true)}用法相同，但不会抛异常
     * {@link java.lang.reflect.InaccessibleObjectException}
     *
     * @param ao {@link AccessibleObject}
     */
    public abstract void makeAccessible(AccessibleObject ao);
}
