package com.chm.converter.core.universal;

import com.chm.converter.core.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 通用接口工厂
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-12
 **/
@FunctionalInterface
public interface UniversalFactory<T extends UniversalInterface> {

    static <T extends UniversalInterface, D> UniversalFactory<T> newFactory(final TypeToken<D> type,
                                                                            final T codec) {
        // we use a runtime check to make sure the 'T's equal
        return (generate, typeToken) -> typeToken.equals(type) ? codec : null;
    }

    static <T extends UniversalInterface, D> UniversalFactory<T> newFactory(final Class<D> type,
                                                                            final T t) {
        return new UniversalFactory<T>() {
            @Override
            public T create(UniversalGenerate<T> generate, TypeToken<?> typeToken) {
                return typeToken.getRawType() == type ? t : null;
            }

            @Override
            public String toString() {
                return "Factory[type=" + type.getName() + ", universal=" + t + "]";
            }
        };
    }

    static <T extends UniversalInterface, D> UniversalFactory<T> newFactory(final Class<D> unboxed,
                                                                            final Class<D> boxed,
                                                                            final T t) {
        return new UniversalFactory<T>() {
            @Override
            public T create(UniversalGenerate<T> generate, TypeToken<?> typeToken) {
                Class<?> rawType = typeToken.getRawType();
                return (rawType == unboxed || rawType == boxed) ? t : null;
            }

            @Override
            public String toString() {
                return "Factory[type=" + boxed.getName()
                        + "+" + unboxed.getName() + ", universal=" + t + "]";
            }
        };
    }

    static <T extends UniversalInterface, D> UniversalFactory<T> newFactoryForMultipleTypes(final Class<D> base,
                                                                                            final Class<? extends D> sub,
                                                                                            final T t) {
        return new UniversalFactory<T>() {
            @Override
            public T create(UniversalGenerate<T> generate, TypeToken<?> typeToken) {
                Class<?> rawType = typeToken.getRawType();
                return (rawType == base || rawType == sub) ? t : null;
            }

            @Override
            public String toString() {
                return "Factory[type=" + base.getName()
                        + "+" + sub.getName() + ", universal=" + t + "]";
            }
        };
    }

    /**
     * Returns a factory for all subtypes of {@code codec}. We do a runtime check to confirm
     * that the deserialized type matches the type requested.
     */
    static <T extends UniversalInterface, D> UniversalFactory<T> newTypeHierarchyFactory(final Class<D> clazz,
                                                                                         final T t) {
        return new UniversalFactory<T>() {
            @Override
            public T create(UniversalGenerate<T> generate, TypeToken<?> typeToken) {
                final Class<?> requestedType = typeToken.getRawType();
                if (!clazz.isAssignableFrom(requestedType)) {
                    return null;
                }
                return t;
            }

            @Override
            public String toString() {
                return "Factory[typeHierarchy=" + clazz.getName() + ", universal=" + t + "]";
            }
        };
    }

    /**
     * 创建
     *
     * @param generate
     * @param type
     * @return
     */
    default T create(UniversalGenerate<T> generate, Type type) {
        TypeToken<?> typeToken = TypeToken.get(type);
        return create(generate, typeToken);
    }

    /**
     * 创建
     *
     * @param generate
     * @param cls
     * @return
     */
    default T create(UniversalGenerate<T> generate, Class<?> cls) {
        TypeToken<?> typeToken = TypeToken.get(cls);
        return create(generate, typeToken);
    }

    /**
     * 创建
     *
     * @param generate
     * @param typeToken
     * @return
     */
    T create(UniversalGenerate<T> generate, TypeToken<?> typeToken);
}
