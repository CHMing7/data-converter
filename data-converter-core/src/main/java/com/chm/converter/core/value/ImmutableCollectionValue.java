package com.chm.converter.core.value;

/**
 * Collection 类型的不可变表示
 *
 * @author caihongming
 * @version v1.0
 * @see ClassValue
 * @see ImmutableValue
 * @since 2021-12-04
 **/
public interface ImmutableCollectionValue extends CollectionValue, ImmutableValue {

    /**
     * 新增元素
     *
     * @param e
     * @return
     */
    @Override
    default boolean add(Value e) {
        throw new UnsupportedOperationException();
    }

    /**
     * 删除元素
     *
     * @param e
     * @return
     */
    @Override
    default boolean remove(Value e) {
        throw new UnsupportedOperationException();
    }

    /**
     * 判断{@code CollectionValue}中元素是否全部在本集合中存在
     *
     * @param c
     * @return
     */
    @Override
    default boolean containsAll(CollectionValue c) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将{@code CollectionValue}集合中的元素全部增加到本集合中
     *
     * @param c
     * @return
     */
    @Override
    default boolean addAll(CollectionValue c) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将{@code CollectionValue}集合中的元素从本集合中删除
     *
     * @param c
     * @return
     */
    @Override
    default boolean removeAll(CollectionValue c) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将{@code CollectionValue}集合中不存在的元素从本集合中删除
     *
     * @param c
     * @return
     */
    @Override
    default boolean retainAll(CollectionValue c) {
        throw new UnsupportedOperationException();
    }

    /**
     * 清空集合中所有元素
     */
    @Override
    default void clear() {
        throw new UnsupportedOperationException();
    }
}
