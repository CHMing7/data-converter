package com.chm.converter.core.universal;

import com.chm.converter.core.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-12
 **/
public class UniversalGenerate<T extends UniversalInterface> {

    private final Map<TypeToken<?>, T> typeCache = new ConcurrentHashMap<>();

    private final List<UniversalFactory<T>> factories;

    public UniversalGenerate() {
        this(null);
    }

    public UniversalGenerate(List<UniversalFactory<T>> factories) {
        if (factories != null) {
            Collections.reverse(factories);
        }
        this.factories = Collections.unmodifiableList(factories != null ? factories : Collections.emptyList());
    }


    /**
     * 获取编解码器
     *
     * @param type
     * @return
     */
    public T get(Type type) {
        if (type == null) {
            return null;
        }
        TypeToken<?> typeToken = TypeToken.get(type);
        return get(typeToken);
    }

    /**
     * 获取编解码器
     *
     * @param type
     * @return
     */
    public T get(TypeToken<?> type) {
        if (type == null) {
            return null;
        }
        T cached = typeCache.get(type);
        if (cached != null) {
            return cached;
        }
        for (UniversalFactory<T> factory : factories) {
            T candidate = factory.create(this, type);
            if (candidate != null) {
                put(type, candidate);
                return candidate;
            }
        }
        return null;
    }

    /**
     * 获取编解码器
     *
     * @param skipPast 跳过工厂类
     * @param type
     * @return
     */
    public T getDelegate(UniversalFactory<T> skipPast, Type type) {
        if (type == null) {
            return null;
        }
        TypeToken<?> typeToken = TypeToken.get(type);
        return getDelegate(skipPast, typeToken);
    }

    /**
     * 获取编解码器
     *
     * @param skipPast  跳过工厂类
     * @param typeToken
     * @return
     */
    public T getDelegate(UniversalFactory<T> skipPast, TypeToken<?> typeToken) {
        boolean skipPastFound = false;
        for (UniversalFactory<T> factory : factories) {
            if (!skipPastFound) {
                if (factory == skipPast) {
                    skipPastFound = true;
                }
                continue;
            }

            T candidate = factory.create(this, typeToken);
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * 获取编解码器
     *
     * @param type
     * @return
     */
    public T get(Class<T> type) {
        return get((Type) type);
    }

    /**
     * 新增编解码器
     *
     * @param type
     * @param t
     * @return
     */
    public boolean put(Type type, T t) {
        TypeToken<?> typeToken = TypeToken.get(type);
        return this.typeCache.put(typeToken, t) != null;
    }

    /**
     * 新增编解码器
     *
     * @param type
     * @param t
     * @return
     */
    public boolean put(TypeToken<?> type, T t) {
        return this.typeCache.put(type, t) != null;
    }

    public boolean containsByType(Type type) {
        if (this.typeCache.containsKey(type)) {
            return true;
        }
        return get(type) != null;
    }

}
