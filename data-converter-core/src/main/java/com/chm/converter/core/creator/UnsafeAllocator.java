package com.chm.converter.core.creator;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-07
 **/
public abstract class UnsafeAllocator {

    public static UnsafeAllocator create() {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field f = unsafeClass.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            final Object unsafe = f.get(null);
            final Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
            return new UnsafeAllocator() {
                @Override
                @SuppressWarnings("unchecked")
                public <T> T newInstance(Class<T> c) throws Exception {
                    assertInstantiable(c);
                    return (T) allocateInstance.invoke(unsafe, c);
                }
            };
        } catch (Exception ignored) {
        }

        try {
            Method getConstructorId = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
            getConstructorId.setAccessible(true);
            final int constructorId = (Integer) getConstructorId.invoke(null, Object.class);
            final Method newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, int.class);
            newInstance.setAccessible(true);
            return new UnsafeAllocator() {
                @Override
                @SuppressWarnings("unchecked")
                public <T> T newInstance(Class<T> c) throws Exception {
                    assertInstantiable(c);
                    return (T) newInstance.invoke(null, c, constructorId);
                }
            };
        } catch (Exception ignored) {
        }

        try {
            final Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
            newInstance.setAccessible(true);
            return new UnsafeAllocator() {
                @Override
                @SuppressWarnings("unchecked")
                public <T> T newInstance(Class<T> c) throws Exception {
                    assertInstantiable(c);
                    return (T) newInstance.invoke(null, c, Object.class);
                }
            };
        } catch (Exception ignored) {
        }

        // give up
        return new UnsafeAllocator() {
            @Override
            public <T> T newInstance(Class<T> c) {
                throw new UnsupportedOperationException("Cannot allocate " + c);
            }
        };
    }

    /**
     * Check if the class can be instantiated by unsafe allocator. If the instance has interface or abstract modifiers
     * throw an {@link java.lang.UnsupportedOperationException}
     *
     * @param c instance of the class to be checked0
     */
    static void assertInstantiable(Class<?> c) {
        int modifiers = c.getModifiers();
        if (Modifier.isInterface(modifiers)) {
            throw new UnsupportedOperationException("Interface can't be instantiated! Interface name: " + c.getName());
        }
        if (Modifier.isAbstract(modifiers)) {
            throw new UnsupportedOperationException("Abstract class can't be instantiated! Class name: " + c.getName());
        }
    }

    /**
     * 创建实例
     *
     * @param c
     * @param <T>
     * @return
     * @throws Exception
     */
    public abstract <T> T newInstance(Class<T> c) throws Exception;
}
