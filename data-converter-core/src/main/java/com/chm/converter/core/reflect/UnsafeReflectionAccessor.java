package com.chm.converter.core.reflect;


import com.chm.converter.core.exception.ConvertException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-07
 **/
@SuppressWarnings({"unchecked", "rawtypes"})
final class UnsafeReflectionAccessor extends ReflectionAccessor {

    private static Class unsafeClass;
    private final Object theUnsafe = getUnsafeInstance();
    private final Field overrideField = getOverrideField();

    private static Object getUnsafeInstance() {
        try {
            unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            return unsafeField.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static Field getOverrideField() {
        try {
            return AccessibleObject.class.getDeclaredField("override");
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeAccessible(AccessibleObject ao) {
        boolean success = makeAccessibleWithUnsafe(ao);
        if (!success) {
            try {
                // unsafe couldn't be found, so try using accessible anyway
                ao.setAccessible(true);
            } catch (SecurityException e) {
                throw new ConvertException("Converter couldn't modify fields for " + ao
                        + "\nand sun.misc.Unsafe not found.\nEither write a custom type adapter,"
                        + " or make fields accessible, or include sun.misc.Unsafe.", e);
            }
        }
    }

    /**
     * 仅用于测试可见
     *
     * @param ao
     * @return
     */
    boolean makeAccessibleWithUnsafe(AccessibleObject ao) {
        if (theUnsafe != null && overrideField != null) {
            try {
                Method method = unsafeClass.getMethod("objectFieldOffset", Field.class);
                long overrideOffset = (Long) method.invoke(theUnsafe, overrideField);
                Method putBooleanMethod = unsafeClass.getMethod("putBoolean", Object.class, long.class, boolean.class);
                putBooleanMethod.invoke(theUnsafe, ao, overrideOffset, true);
                return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}
