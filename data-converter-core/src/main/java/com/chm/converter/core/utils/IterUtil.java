package com.chm.converter.core.utils;

import java.util.Iterator;

/**
 * {@link Iterable} 和 {@link Iterator} 相关工具类
 *
 * @author caihongming
 * @version v1.0
 * @since 2022-01-25
 **/
public class IterUtil {


    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterable<?> iterable) {
        return null == iterable || isEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterator<?> Iterator) {
        return null == Iterator || false == Iterator.hasNext();
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterable<?> iterable) {
        return null != iterable && isNotEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterator<?> Iterator) {
        return null != Iterator && Iterator.hasNext();
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iter 被检查的{@link Iterable}对象，如果为{@code null} 返回true
     * @return 是否包含{@code null}元素
     */
    public static boolean hasNull(Iterable<?> iter) {
        return hasNull(null == iter ? null : iter.iterator());
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iter 被检查的{@link Iterator}对象，如果为{@code null} 返回true
     * @return 是否包含{@code null}元素
     */
    public static boolean hasNull(Iterator<?> iter) {
        if (null == iter) {
            return true;
        }
        while (iter.hasNext()) {
            if (null == iter.next()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否全部元素为null
     *
     * @param iter iter 被检查的{@link Iterable}对象，如果为{@code null} 返回true
     * @return 是否全部元素为null
     */
    public static boolean isAllNull(Iterable<?> iter) {
        return isAllNull(null == iter ? null : iter.iterator());
    }

    /**
     * 是否全部元素为null
     *
     * @param iter iter 被检查的{@link Iterator}对象，如果为{@code null} 返回true
     * @return 是否全部元素为null
     */
    public static boolean isAllNull(Iterator<?> iter) {
        if (null == iter) {
            return true;
        }

        while (iter.hasNext()) {
            if (null != iter.next()) {
                return false;
            }
        }
        return true;
    }
}
