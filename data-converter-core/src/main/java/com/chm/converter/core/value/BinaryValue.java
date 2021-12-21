package com.chm.converter.core.value;

import java.nio.ByteBuffer;

/**
 * Binary 类型
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-12-04
 */
public interface BinaryValue extends Value {

    /**
     * 将该值以{@code byte[]}形式表示返回
     *
     * @return
     */
    byte[] asByteArray();

    /**
     * 以 {@code ByteBuffer} 形式返回该值
     * 返回的 ByteBuffer 是只读的。另请参阅 {@link ByteBuffer#asReadOnlyBuffer()}
     * 此方法不会尽可能多地复制字节数组。
     *
     * @return
     */
    ByteBuffer asByteBuffer();
}
