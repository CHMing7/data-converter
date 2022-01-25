package com.chm.converter.core.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-10
 **/
public class ArrayUtil extends PrimitiveArrayUtil {

    // ---------------------------------------------------------------------- isEmpty

    /**
     * 数组是否为空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为空
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 如果给定数组为空，返回默认数组
     *
     * @param <T>          数组元素类型
     * @param array        数组
     * @param defaultArray 默认数组
     * @return 非空（empty）的原数组或默认数组
     */
    public static <T> T[] defaultIfEmpty(T[] array, T[] defaultArray) {
        return isEmpty(array) ? defaultArray : array;
    }

    /**
     * 数组是否为空<br>
     * 此方法会匹配单一对象，如果此对象为{@code null}则返回true<br>
     * 如果此对象为非数组，理解为此对象为数组的第一个元素，则返回false<br>
     * 如果此对象为数组对象，数组长度大于0情况下返回false，否则返回true
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(Object array) {
        if (array != null) {
            if (isArray(array)) {
                return 0 == Array.getLength(array);
            }
            return false;
        }
        return true;
    }

    // ---------------------------------------------------------------------- isNotEmpty

    /**
     * 数组是否为非空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为非空
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return (null != array && array.length != 0);
    }

    /**
     * 数组是否为非空<br>
     * 此方法会匹配单一对象，如果此对象为{@code null}则返回false<br>
     * 如果此对象为非数组，理解为此对象为数组的第一个元素，则返回true<br>
     * 如果此对象为数组对象，数组长度大于0情况下返回true，否则返回false
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(Object array) {
        return !isEmpty(array);
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     * @return 是否包含{@code null}元素
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean hasNull(T... array) {
        if (isNotEmpty(array)) {
            for (T element : array) {
                if (null == element) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 多个字段是否全为null
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     * @return 多个字段是否全为null
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean isAllNull(T... array) {
        return null == firstNonNull(array);
    }

    /**
     * 返回数组中第一个非空元素
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 非空元素，如果不存在非空元素或数组为空，返回{@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> T firstNonNull(T... array) {
        return firstMatch(Objects::nonNull, array);
    }

    /**
     * 返回数组中第一个匹配规则的值
     *
     * @param <T>     数组元素类型
     * @param matcher 匹配接口，实现此接口自定义匹配规则
     * @param array   数组
     * @return 非空元素，如果不存在非空元素或数组为空，返回{@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> T firstMatch(Predicate<T> matcher, T... array) {
        if (isNotEmpty(array)) {
            for (final T val : array) {
                if (matcher.test(val)) {
                    return val;
                }
            }
        }
        return null;
    }

    /**
     * 新建一个空数组
     *
     * @param <T>           数组元素类型
     * @param componentType 元素类型
     * @param newSize       大小
     * @return 空数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<?> componentType, int newSize) {
        return (T[]) Array.newInstance(componentType, newSize);
    }

    /**
     * 新建一个空数组
     *
     * @param newSize 大小
     * @return 空数组
     */
    public static Object[] newArray(int newSize) {
        return new Object[newSize];
    }

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        return null != obj && obj.getClass().isArray();
    }

    /**
     * 数组或集合转String
     *
     * @param obj 集合或数组对象
     * @return 数组字符串，与集合转字符串格式相同
     */
    public static String toString(Object obj) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        } else if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        } else if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        } else if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        } else if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        } else if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        } else if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        } else if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        } else if (ArrayUtil.isArray(obj)) {
            // 对象数组
            try {
                return Arrays.deepToString((Object[]) obj);
            } catch (Exception ignore) {
                //ignore
            }
        }

        return obj.toString();
    }

    // ------------------------------------------------------------------- indexOf and lastIndexOf and contains

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param <T>   数组类型
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static <T> int indexOf(T[] array, Object value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (ObjectUtil.equal(value, array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，忽略大小写，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOfIgnoreCase(CharSequence[] array, CharSequence value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (StringUtil.equalsIgnoreCase(array[i], value)) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param <T>   数组类型
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static <T> int lastIndexOf(T[] array, Object value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (ObjectUtil.equal(value, array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static <T> boolean contains(T[] array, T value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含指定元素中的任意一个
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param values 被检查的多个元素
     * @return 是否包含指定元素中的任意一个
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean containsAny(T[] array, T... values) {
        for (T value : values) {
            if (contains(array, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数组中是否包含指定元素中的全部
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param values 被检查的多个元素
     * @return 是否包含指定元素中的全部
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean containsAll(T[] array, T... values) {
        for (T value : values) {
            if (!contains(array, value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 数组中是否包含元素，忽略大小写
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean containsIgnoreCase(CharSequence[] array, CharSequence value) {
        return indexOfIgnoreCase(array, value) > INDEX_NOT_FOUND;
    }


    /**
     * 将集合转为数组
     *
     * @param <T>           数组元素类型
     * @param iterator      {@link Iterator}
     * @param componentType 集合元素类型
     * @return 数组
     */
    public static <T> T[] toArray(Iterator<T> iterator, Class<T> componentType) {
        return toArray(CollUtil.newArrayList(iterator), componentType);
    }

    /**
     * 将集合转为数组
     *
     * @param <T>           数组元素类型
     * @param iterable      {@link Iterable}
     * @param componentType 集合元素类型
     * @return 数组
     */
    public static <T> T[] toArray(Iterable<T> iterable, Class<T> componentType) {
        return toArray(CollUtil.toCollection(iterable), componentType);
    }

    /**
     * 将集合转为数组
     *
     * @param <T>           数组元素类型
     * @param collection    集合
     * @param componentType 集合元素类型
     * @return 数组
     */
    public static <T> T[] toArray(Collection<T> collection, Class<T> componentType) {
        return collection.toArray(newArray(componentType, 0));
    }

    /**
     * 获取数组长度<br>
     * 如果参数为{@code null}，返回0
     *
     * <pre>
     * ArrayUtil.length(null)            = 0
     * ArrayUtil.length([])              = 0
     * ArrayUtil.length([null])          = 1
     * ArrayUtil.length([true, false])   = 2
     * ArrayUtil.length([1, 2, 3])       = 3
     * ArrayUtil.length(["a", "b", "c"]) = 3
     * </pre>
     *
     * @param array 数组对象
     * @return 数组长度
     * @throws IllegalArgumentException 如果参数不为数组，抛出此异常
     * @see Array#getLength(Object)
     */
    public static int length(Object array) throws IllegalArgumentException {
        if (null == array) {
            return 0;
        }
        return Array.getLength(array);
    }

    /**
     * 将新元素添加到已有数组中<br>
     * 添加新元素会生成一个新的数组，不影响原数组
     *
     * @param <T>         数组元素类型
     * @param buffer      已有数组
     * @param newElements 新元素
     * @return 新数组
     */
    @SafeVarargs
    public static <T> T[] append(T[] buffer, T... newElements) {
        if (isEmpty(buffer)) {
            return newElements;
        }
        return insert(buffer, buffer.length, newElements);
    }

    /**
     * 将新元素添加到已有数组中<br>
     * 添加新元素会生成一个新的数组，不影响原数组
     *
     * @param <T>         数组元素类型
     * @param array       已有数组
     * @param newElements 新元素
     * @return 新数组
     */
    @SafeVarargs
    public static <T> Object append(Object array, T... newElements) {
        if (isEmpty(array)) {
            return newElements;
        }
        return insert(array, length(array), newElements);
    }

    /**
     * 将元素值设置为数组的某个位置，当给定的index大于数组长度，则追加
     *
     * @param <T>    数组元素类型
     * @param buffer 已有数组
     * @param index  位置，大于长度追加，否则替换
     * @param value  新值
     * @return 新数组或原有数组
     */
    public static <T> T[] setOrAppend(T[] buffer, int index, T value) {
        if (index < buffer.length) {
            Array.set(buffer, index, value);
            return buffer;
        } else {
            return append(buffer, value);
        }
    }

    /**
     * 将元素值设置为数组的某个位置，当给定的index大于数组长度，则追加
     *
     * @param array 已有数组
     * @param index 位置，大于长度追加，否则替换
     * @param value 新值
     * @return 新数组或原有数组
     */
    public static Object setOrAppend(Object array, int index, Object value) {
        if (index < length(array)) {
            Array.set(array, index, value);
            return array;
        } else {
            return append(array, value);
        }
    }


    /**
     * 将新元素插入到到已有数组中的某个位置<br>
     * 添加新元素会生成一个新的数组，不影响原数组<br>
     * 如果插入位置为为负数，从原数组从后向前计数，若大于原数组长度，则空白处用null填充
     *
     * @param <T>         数组元素类型
     * @param buffer      已有数组
     * @param index       插入位置，此位置为对应此位置元素之前的空档
     * @param newElements 新元素
     * @return 新数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] insert(T[] buffer, int index, T... newElements) {
        return (T[]) insert((Object) buffer, index, newElements);
    }

    /**
     * 将新元素插入到到已有数组中的某个位置<br>
     * 添加新元素会生成一个新的数组，不影响原数组<br>
     * 如果插入位置为为负数，从原数组从后向前计数，若大于原数组长度，则空白处用null填充
     *
     * @param <T>         数组元素类型
     * @param array       已有数组
     * @param index       插入位置，此位置为对应此位置元素之前的空档
     * @param newElements 新元素
     * @return 新数组
     */
    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    public static <T> Object insert(Object array, int index, T... newElements) {
        if (isEmpty(newElements)) {
            return array;
        }
        if (isEmpty(array)) {
            return newElements;
        }

        final int len = length(array);
        if (index < 0) {
            index = (index % len) + len;
        }

        final T[] result = newArray(array.getClass().getComponentType(), Math.max(len, index) + newElements.length);
        System.arraycopy(array, 0, result, 0, Math.min(len, index));
        System.arraycopy(newElements, 0, result, index, newElements.length);
        if (index < len) {
            System.arraycopy(array, index, result, index + newElements.length, len - index);
        }
        return result;
    }

    /**
     * 过滤<br>
     * 过滤过程通过传入的Filter实现来过滤返回需要的元素内容，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Predicate#test(Object)}方法返回true的对象将被加入结果集合中
     * </pre>
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param filter 过滤器接口，用于定义过滤规则，null表示不过滤，返回原数组
     * @return 过滤后的数组
     */
    public static <T> T[] filter(T[] array, Predicate<T> filter) {
        if (null == filter) {
            return array;
        }

        final ArrayList<T> list = new ArrayList<>(array.length);
        for (T t : array) {
            if (filter.test(t)) {
                list.add(t);
            }
        }
        final T[] result = newArray(array.getClass().getComponentType(), list.size());
        return list.toArray(result);
    }

}
