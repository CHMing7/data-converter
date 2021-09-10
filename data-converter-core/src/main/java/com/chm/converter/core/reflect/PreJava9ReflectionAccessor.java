package com.chm.converter.core.reflect;

import java.lang.reflect.AccessibleObject;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-07
 **/
final class PreJava9ReflectionAccessor extends ReflectionAccessor {

    @Override
    public void makeAccessible(AccessibleObject ao) {
        ao.setAccessible(true);
    }
}

