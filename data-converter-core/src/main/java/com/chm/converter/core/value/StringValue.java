package com.chm.converter.core.value;

/**
 * String 类型
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-12-04
 */
public interface StringValue extends Value {

    /**
     * 将该值以{@code String}形式表示返回
     * <p>
     * 如果该值包含无效的 UTF-8 字节序列，则此方法将引发异常。
     *
     * @return
     */
    String asString();

    /**
     * 将该值以{@code String}形式表示返回
     * <p>
     * 此方法用 <code>U+FFFD 替换字符 <code> 替换无效的 UTF-8 字节序列。
     *
     * @return
     */
    @Override
    String toString();
}
