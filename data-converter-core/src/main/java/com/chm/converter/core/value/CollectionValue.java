package com.chm.converter.core.value;

import java.util.Collection;
import java.util.Iterator;

/**
 * Collection 类型
 *
 * @author caihongming
 * @version v1.0
 * @see Value
 * @since 2021-12-04
 */
public interface CollectionValue extends Value, Iterable<Value> {

    /**
     * 返回集合大小
     *
     * @return
     */
    int size();

    /**
     * 如果集合为空则返回{@code true}
     *
     * @return
     */
    boolean isEmpty();

    /**
     * 如果集合中存在元素o则返回{@code true}
     *
     * @param o
     * @return
     */
    boolean contains(Value o);

    /**
     * 返回该集合的迭代器
     *
     * @return
     */
    @Override
    Iterator<Value> iterator();

    /**
     * 将集合以{@link ArrayValue}形式返回
     *
     * @return
     */
    ArrayValue toArray();

    /**
     * 新增元素
     *
     * @param e
     * @return
     */
    boolean add(Value e);

    /**
     * 删除元素
     *
     * @param e
     * @return
     */
    boolean remove(Value e);

    /**
     * 判断{@code CollectionValue}中元素是否全部在本集合中存在
     *
     * @param c
     * @return
     */
    boolean containsAll(CollectionValue c);

    /**
     * 将{@code CollectionValue}集合中的元素全部增加到本集合中
     *
     * @param c
     * @return
     */
    boolean addAll(CollectionValue c);

    /**
     * 将{@code CollectionValue}集合中的元素从本集合中删除
     *
     * @param c
     * @return
     */
    boolean removeAll(CollectionValue c);

    /**
     * 将{@code CollectionValue}集合中不存在的元素从本集合中删除
     *
     * @param c
     * @return
     */
    boolean retainAll(CollectionValue c);

    /**
     * 返回该集合
     *
     * @return
     */
    Collection<Value> collection();

    /**
     * 清空集合中所有元素
     */
    void clear();
}
