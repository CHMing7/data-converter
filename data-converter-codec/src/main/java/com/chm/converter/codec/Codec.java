package com.chm.converter.codec;

import com.chm.converter.core.universal.UniversalInterface;

import java.io.IOException;

/**
 * 编解码器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-09-02
 **/
public interface Codec<D, E> extends UniversalInterface {

    /**
     * 编码
     *
     * @param d
     * @return
     */
    E encode(D d);

    /**
     * 写入
     *
     * @param d
     * @param encode
     * @param writer
     * @throws IOException
     */
    default void write(D d, Encode<D, E> encode, Writer<E> writer) throws IOException {
        E e = encode.encode(d);
        if (writer == null) {
            return;
        }
        writer.write(e);
    }

    /**
     * 写入
     *
     * @param d
     * @param writer
     * @throws IOException
     */
    default void write(D d, Writer<E> writer) throws IOException {
        E e = encode(d);
        if (writer == null) {
            return;
        }
        writer.write(e);
    }

    /**
     * 解码
     *
     * @param e
     * @return
     */
    D decode(E e);

    /**
     * 读取
     *
     * @param decode
     * @param reader
     * @return
     * @throws IOException
     */
    default D read(Decode<D, E> decode, Reader<E> reader) throws IOException {
        return reader == null ? null : decode.decode(reader.read());
    }

    /**
     * 读取
     *
     * @param reader
     * @return
     * @throws IOException
     */
    default D read(Reader<E> reader) throws IOException {
        return reader == null ? null : decode(reader.read());
    }

    /**
     * 内部编码接口类
     *
     * @param <D>
     * @param <E>
     */
    @FunctionalInterface
    interface Encode<D, E> {

        /**
         * 编码
         *
         * @param d
         * @return
         */
        E encode(D d);
    }

    /**
     * 写操作接口类
     *
     * @param <T>
     */
    @FunctionalInterface
    interface Writer<T> {

        /**
         * 写入操作
         *
         * @param t
         * @throws IOException
         */
        void write(T t) throws IOException;
    }

    /**
     * 内部解码接口类
     *
     * @param <D>
     * @param <E>
     */
    @FunctionalInterface
    interface Decode<D, E> {

        /**
         * 解码
         *
         * @param e
         * @return
         */
        D decode(E e);
    }

    /**
     * 读操作接口类
     *
     * @param <T>
     */
    @FunctionalInterface
    interface Reader<T> {

        /**
         * 读取操作
         *
         * @return
         * @throws IOException
         */
        T read() throws IOException;
    }

}
