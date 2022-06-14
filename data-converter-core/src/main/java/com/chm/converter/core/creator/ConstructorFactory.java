package com.chm.converter.core.creator;

import com.chm.converter.core.reflect.ReflectionAccessor;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.MapUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-07
 **/
public final class ConstructorFactory {

    public final static ConstructorFactory INSTANCE = new ConstructorFactory(MapUtil.empty());

    private final Map<Type, InstanceCreator<?>> instanceCreatorMap;

    private final ReflectionAccessor accessor = ReflectionAccessor.getInstance();

    public ConstructorFactory() {
        this.instanceCreatorMap = MapUtil.empty();
    }

    public ConstructorFactory(Map<Type, InstanceCreator<?>> instanceCreatorMap) {
        this.instanceCreatorMap = instanceCreatorMap;
    }

    public <T> ObjectConstructor<T> get(Class<T> cls) {
        return get(TypeToken.get(cls));
    }

    public <T> ObjectConstructor<T> get(Type type) {
        return get(TypeToken.get(type));
    }

    @SuppressWarnings("unchecked")
    public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
        final Type type = typeToken.getType();
        final Class<? super T> rawType = typeToken.getRawType();
        // 尝试一个实例创建者

        // 类型必须一致
        final InstanceCreator<T> typeCreator = (InstanceCreator<T>) instanceCreatorMap.get(type);
        if (typeCreator != null) {
            return () -> typeCreator.createInstance(type);
        }

        // 接下来尝试实例创建者的原始类型匹配
        // 类型必须一致
        final InstanceCreator<T> rawTypeCreator = (InstanceCreator<T>) instanceCreatorMap.get(rawType);
        if (rawTypeCreator != null) {
            return () -> rawTypeCreator.createInstance(type);
        }

        ObjectConstructor<T> defaultConstructor = newDefaultConstructor(rawType);
        if (defaultConstructor != null) {
            return defaultConstructor;
        }

        ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
        if (defaultImplementation != null) {
            return defaultImplementation;
        }

        // finally try unsafe
        return newUnsafeAllocator(type, rawType);
    }

    private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
        try {
            final Constructor<? super T> constructor = rawType.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                accessor.makeAccessible(constructor);
            }

            return () -> {
                try {
                    Object[] args = null;
                    return (T) constructor.newInstance(args);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Failed to invoke " + constructor + " with no args", e.getTargetException());
                } catch (IllegalAccessException e) {
                    throw new AssertionError(e);
                }
            };
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 常见接口类型（如 Map 和 List 及其子类型）的构造函数。
     *
     * @param type
     * @param rawType
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> ObjectConstructor<T> newDefaultImplementationConstructor(final Type type, Class<? super T> rawType) {
        if (Collection.class.isAssignableFrom(rawType)) {
            if (SortedSet.class.isAssignableFrom(rawType)) {
                return () -> (T) new TreeSet<>();
            } else if (EnumSet.class.isAssignableFrom(rawType)) {
                return () -> {
                    if (type instanceof ParameterizedType) {
                        Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
                        if (elementType instanceof Class) {
                            return (T) EnumSet.noneOf((Class) elementType);
                        } else {
                            throw new CreatorException("Invalid EnumSet type: " + type);
                        }
                    } else {
                        throw new CreatorException("Invalid EnumSet type: " + type.toString());
                    }
                };
            } else if (Set.class.isAssignableFrom(rawType)) {
                return () -> (T) new LinkedHashSet<>();
            } else if (Queue.class.isAssignableFrom(rawType)) {
                return () -> (T) new ArrayDeque<>();
            } else {
                return () -> (T) new ArrayList<>();
            }
        }

        if (Map.class.isAssignableFrom(rawType)) {
            if (ConcurrentNavigableMap.class.isAssignableFrom(rawType)) {
                return () -> (T) new ConcurrentSkipListMap<>();
            } else if (ConcurrentMap.class.isAssignableFrom(rawType)) {
                return () -> (T) new ConcurrentHashMap<>();
            } else if (SortedMap.class.isAssignableFrom(rawType)) {
                return () -> (T) new TreeMap<>();
            } else if (type instanceof ParameterizedType && !(String.class.isAssignableFrom(
                    TypeToken.get(((ParameterizedType) type).getActualTypeArguments()[0]).getRawType()))) {
                return () -> (T) new LinkedHashMap<>();
            } else {
                return () -> (T) new HashMap<String, Object>();
            }
        }

        return null;
    }

    private <T> ObjectConstructor<T> newUnsafeAllocator(final Type type, final Class<? super T> rawType) {
        return new ObjectConstructor<T>() {
            private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();

            @SuppressWarnings("unchecked")
            @Override
            public T construct() {
                try {
                    Object newInstance = unsafeAllocator.newInstance(rawType);
                    return (T) newInstance;
                } catch (Exception e) {
                    throw new RuntimeException(("Unable to invoke no-args constructor for " + type + ". "
                            + "Registering an InstanceCreator with Converter for this type may fix this problem."), e);
                }
            }
        };
    }

    @Override
    public String toString() {
        return instanceCreatorMap.toString();
    }
}
