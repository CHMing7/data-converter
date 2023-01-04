package com.chm.converter.core.reflect;

import com.chm.converter.core.utils.MapUtil;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * 仿照gson的TypeToken
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-07
 **/
public class TypeToken<T> {

    private static final Map<Type, TypeToken<?>> TYPE_TOKEN_MAP = MapUtil.newConcurrentHashMap();

    final Class<? super T> rawType;

    final Type type;

    final int hashCode;

    @SuppressWarnings("unchecked")
    protected TypeToken() {
        this.type = getSuperclassTypeParameter(getClass());
        this.rawType = (Class<? super T>) ConverterTypes.getRawType(type);
        this.hashCode = type.hashCode();
    }

    /**
     * 不安全。手动构造类型。
     *
     * @param type
     */
    @SuppressWarnings("unchecked")
    TypeToken(Type type) {
        this.type = ConverterTypes.canonicalize(ConverterPreconditions.checkNotNull(type));
        this.rawType = (Class<? super T>) ConverterTypes.getRawType(this.type);
        this.hashCode = this.type.hashCode();
    }

    /**
     * 从父类的类型参数返回类型
     *
     * @param subclass
     * @return
     */
    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return ConverterTypes.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    /**
     * 递归检查from是否为to的子类型
     *
     * @param from
     * @param to
     * @return
     */
    private static boolean isAssignableFrom(Type from, GenericArrayType to) {
        Type toGenericComponentType = to.getGenericComponentType();
        if (toGenericComponentType instanceof ParameterizedType) {
            Type t = from;
            if (from instanceof GenericArrayType) {
                t = ((GenericArrayType) from).getGenericComponentType();
            } else if (from instanceof Class<?>) {
                Class<?> classType = (Class<?>) from;
                while (classType.isArray()) {
                    classType = classType.getComponentType();
                }
                t = classType;
            }
            return isAssignableFrom(t, (ParameterizedType) toGenericComponentType,
                    new HashMap<>());
        }
        // No generic defined on "to"; therefore, return true and let other
        // checks determine assignability
        return true;
    }

    /**
     * 递归检查from是否为to的子类型
     *
     * @param from
     * @param to
     * @param typeVarMap
     * @return
     */
    private static boolean isAssignableFrom(Type from, ParameterizedType to,
                                            Map<String, Type> typeVarMap) {

        if (from == null) {
            return false;
        }

        if (to.equals(from)) {
            return true;
        }

        // 首先获取类和任何类型信息。
        Class<?> clazz = ConverterTypes.getRawType(from);
        ParameterizedType ptype = null;
        if (from instanceof ParameterizedType) {
            ptype = (ParameterizedType) from;
        }

        // 如果类型已参数化，则加载参数化变量信息。
        if (ptype != null) {
            Type[] tArgs = ptype.getActualTypeArguments();
            TypeVariable<?>[] tParams = clazz.getTypeParameters();
            for (int i = 0; i < tArgs.length; i++) {
                Type arg = tArgs[i];
                TypeVariable<?> var = tParams[i];
                while (arg instanceof TypeVariable<?>) {
                    TypeVariable<?> v = (TypeVariable<?>) arg;
                    arg = typeVarMap.get(v.getName());
                }
                typeVarMap.put(var.getName(), arg);
            }

            // 检查两种类型在当前的映射下是否等效。
            if (typeEquals(ptype, to, typeVarMap)) {
                return true;
            }
        }

        for (Type itype : clazz.getGenericInterfaces()) {
            if (isAssignableFrom(itype, to, new HashMap<>(typeVarMap))) {
                return true;
            }
        }

        // 接口不起作用，尝试使用父类
        Type sType = clazz.getGenericSuperclass();
        return isAssignableFrom(sType, to, new HashMap<>(typeVarMap));
    }

    /**
     * 在 typeVarMap 中描述的变量替换下，检查两个参数化类型是否完全相等。
     *
     * @param from
     * @param to
     * @param typeVarMap
     * @return
     */
    private static boolean typeEquals(ParameterizedType from,
                                      ParameterizedType to, Map<String, Type> typeVarMap) {
        if (from.getRawType().equals(to.getRawType())) {
            Type[] fromArgs = from.getActualTypeArguments();
            Type[] toArgs = to.getActualTypeArguments();
            for (int i = 0; i < fromArgs.length; i++) {
                if (!matches(fromArgs[i], toArgs[i], typeVarMap)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static AssertionError buildUnexpectedTypeError(
            Type token, Class<?>... expected) {

        // Build exception message
        StringBuilder exceptionMessage =
                new StringBuilder("Unexpected type. Expected one of: ");
        for (Class<?> clazz : expected) {
            exceptionMessage.append(clazz.getName()).append(", ");
        }
        exceptionMessage.append("but got: ").append(token.getClass().getName())
                .append(", for type token: ").append(token.toString()).append('.');

        return new AssertionError(exceptionMessage.toString());
    }

    /**
     * 检查两种类型是否相同
     *
     * @param from
     * @param to
     * @param typeMap
     * @return
     */
    private static boolean matches(Type from, Type to, Map<String, Type> typeMap) {
        return to.equals(from)
                || (from instanceof TypeVariable
                && to.equals(typeMap.get(((TypeVariable<?>) from).getName())));

    }

    /**
     * 获取给定 {@code Type} 实例的类型
     *
     * @param type
     * @return
     */
    public static <T> TypeToken<T> get(Type type) {
        return (TypeToken<T>) MapUtil.computeIfAbsent(TYPE_TOKEN_MAP, type, TypeToken::new);
    }

    /**
     * 获取给定 {@code Class} 实例的类型
     *
     * @param type
     * @param <T>
     * @return
     */
    public static <T> TypeToken<T> get(Class<T> type) {
        return get((Type) type);
    }

    /**
     * 获取通过将 {@code typeArguments} 应用于 {@code rawType} 表示的参数化类型
     *
     * @param rawType
     * @param typeArguments
     * @return
     */
    public static <T> TypeToken<T> getParameterized(Type rawType, Type... typeArguments) {
        return new TypeToken<>(ConverterTypes.newParameterizedTypeWithOwner(null, rawType, typeArguments));
    }

    /**
     * 获取{@code componentType}实例的数组类型
     *
     * @param componentType
     * @return
     */
    public static <T> TypeToken<T> getArray(Type componentType) {
        return new TypeToken<>(ConverterTypes.arrayOf(componentType));
    }

    /**
     * 返回此类型的原始（非泛型）类型。
     *
     * @return
     */
    public final Class<? super T> getRawType() {
        return rawType;
    }

    /**
     * 获取底层 {@code Type} 实例。
     *
     * @return
     */
    public final Type getType() {
        return type;
    }

    public final boolean isInstance(Object obj) {
        return rawType.isInstance(obj);
    }

    /**
     * 检查给定类型是否为本类型的子类型
     *
     * @param cls
     * @return
     */
    @Deprecated
    public boolean isAssignableFrom(Class<?> cls) {
        return isAssignableFrom((Type) cls);
    }

    /**
     * 检查给定类型是否为本类型的子类型
     *
     * @param from
     * @return
     */
    @Deprecated
    public boolean isAssignableFrom(Type from) {
        if (from == null) {
            return false;
        }

        if (type.equals(from)) {
            return true;
        }

        if (type instanceof Class<?>) {
            return rawType.isAssignableFrom(ConverterTypes.getRawType(from));
        } else if (type instanceof ParameterizedType) {
            return isAssignableFrom(from, (ParameterizedType) type, new HashMap<>());
        } else if (type instanceof GenericArrayType) {
            return rawType.isAssignableFrom(ConverterTypes.getRawType(from))
                    && isAssignableFrom(from, (GenericArrayType) type);
        } else {
            throw buildUnexpectedTypeError(
                    type, Class.class, ParameterizedType.class, GenericArrayType.class);
        }
    }

    /**
     * 检查给定类型是否为本类型的子类型
     *
     * @param token
     * @return
     */
    @Deprecated
    public boolean isAssignableFrom(TypeToken<?> token) {
        return isAssignableFrom(token.getType());
    }

    @Override
    public final int hashCode() {
        return this.hashCode;
    }

    @Override
    public final boolean equals(Object o) {
        return o instanceof TypeToken<?>
                && ConverterTypes.equals(type, ((TypeToken<?>) o).type);
    }

    @Override
    public final String toString() {
        return ConverterTypes.typeToString(type);
    }
}
