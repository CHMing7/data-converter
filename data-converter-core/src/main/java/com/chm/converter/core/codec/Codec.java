package com.chm.converter.core.codec;

import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;
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
     * @param d 待编码的数据
     * @return 编码过的数据
     */
    E encode(D d);

    /**
     * 获取编码后的数据类型
     *
     * @return 编码后的数据类型
     */
    TypeToken<E> getEncodeType();

    /**
     * 写入
     *
     * @param e  待写入的数据
     * @param dw 数据写入器
     * @throws IOException
     */
    void writeData(E e, DataWriter dw) throws IOException;

    /**
     * 使用默认编码方法和默认写入方法写入数据
     *
     * @param d  待编码的数据
     * @param dw 数据写入器
     * @throws IOException
     */
    default void write(D d, DataWriter dw) throws IOException {
        if (dw == null) {
            return;
        }
        E e = encode(d);
        writeData(e, dw);
    }

    /**
     * 使用自定义编码器和默认写入方法写入数据
     *
     * @param d      待编码的数据
     * @param dw     数据写入器
     * @param encode 自定义编码器
     * @throws IOException
     */
    default void write(D d, DataWriter dw, Encode<D, E> encode) throws IOException {
        if (dw == null || encode == null) {
            return;
        }
        E e = encode.encode(d);
        writeData(e, dw);
    }

    /**
     * 使用默认编码方法和自定义写入器写入数据
     *
     * @param d  待编码的数据
     * @param dw 数据写入器
     * @param wd 自定义写入器
     * @throws IOException
     */
    default void write(D d, DataWriter dw, WriteData<E> wd) throws IOException {
        if (dw == null || wd == null) {
            return;
        }
        E e = encode(d);
        wd.writeData(e, dw);
    }

    /**
     * 使用自定义编码器和自定义写入器写入数据
     *
     * @param d      待编码的数据
     * @param dw     数据写入器
     * @param encode 自定义编码器
     * @param wd     自定义写入器
     * @throws IOException
     */
    default void write(D d, DataWriter dw, Encode<D, E> encode, WriteData<E> wd) throws IOException {
        if (dw == null || encode == null || wd == null) {
            return;
        }
        E e = encode.encode(d);
        wd.writeData(e, dw);
    }


    /**
     * 使用默认编码方法和自定义写入器写入数据
     *
     * @param d      待编码的数据
     * @param writer 自定义写入器
     * @throws IOException
     */
    default void write(D d, Writer<E> writer) throws IOException {
        if (writer == null) {
            return;
        }
        E e = encode(d);
        writer.write(e);
    }

    /**
     * 使用自定义编码器和自定义写入器写入数据
     *
     * @param d      待编码的数据
     * @param encode 自定义编码器
     * @param writer 自定义写入器
     * @throws IOException
     */
    default void write(D d, Encode<D, E> encode, Writer<E> writer) throws IOException {
        if (encode == null || writer == null) {
            return;
        }
        E e = encode.encode(d);
        writer.write(e);
    }

    /**
     * 解码
     *
     * @param e 待解码的数据
     * @return 解码过的数据
     */
    D decode(E e);

    /**
     * 获取解码后的数据类型
     *
     * @return 获取解码后的数据类型
     */
    TypeToken<D> getDecodeType();

    /**
     * 读取
     *
     * @param dr 数据读取器
     * @return 读取出待解码的数据
     * @throws IOException
     */
    E readData(DataReader dr) throws IOException;

    /**
     * 使用默认解码方法和默认读取方法读取数据
     *
     * @param dr 数据读取器
     * @return 读取出并进行解码的数据
     * @throws IOException
     */
    default D read(DataReader dr) throws IOException {
        return dr == null ? null : decode(readData(dr));
    }

    /**
     * 使用自定义解码器和默认读取方法读取数据
     *
     * @param dr     数据读取器
     * @param decode 自定义解码器
     * @return 读取出并进行解码的数据
     * @throws IOException
     */
    default D read(DataReader dr, Decode<D, E> decode) throws IOException {
        return (dr == null || decode == null) ? null : decode.decode(readData(dr));
    }

    /**
     * 使用默认解码方法和自定义读取器读取数据
     *
     * @param dr 数据读取器
     * @param rd 自定义读取器
     * @return 读取出并进行解码的数据
     * @throws IOException
     */
    default D read(DataReader dr, ReadData<E> rd) throws IOException {
        return (dr == null || rd == null) ? null : decode(rd.readData(dr));
    }

    /**
     * 使用自定义解码器和自定义读取器读取数据
     *
     * @param dr     数据读取器
     * @param decode 自定义解码器
     * @param rd     自定义读取器
     * @return 读取出并进行解码的数据
     * @throws IOException
     */
    default D read(DataReader dr, Decode<D, E> decode, ReadData<E> rd) throws IOException {
        return (dr == null || decode == null || rd == null) ? null : decode.decode(rd.readData(dr));
    }

    /**
     * 使用默认解码方法和自定义读取器读取数据
     *
     * @param reader 自定义读取器
     * @return 读取出并进行解码的数据
     * @throws IOException
     */
    default D read(Reader<E> reader) throws IOException {
        return reader == null ? null : decode(reader.read());
    }

    /**
     * 使用自定义解码器和自定义读取器读取数据
     *
     * @param decode 自定义解码器
     * @param reader 自定义读取器
     * @return 读取出并进行解码的数据
     * @throws IOException
     */
    default D read(Decode<D, E> decode, Reader<E> reader) throws IOException {
        return (decode == null || reader == null) ? null : decode.decode(reader.read());
    }

    /**
     * 优先使用此codec
     *
     * @return
     */
    default boolean isPriorityUse() {
        return false;
    }

    /**
     * 编码接口类
     *
     * @param <D>
     * @param <E>
     */
    @FunctionalInterface
    interface Encode<D, E> {

        /**
         * 编码
         *
         * @param d 待编码的数据
         * @return 编码过的数据
         */
        E encode(D d);
    }

    /**
     * 写操作接口器
     *
     * @param <E>
     */
    @FunctionalInterface
    interface WriteData<E> {

        /**
         * 写入操作
         *
         * @param e  待写入的数据
         * @param dw 数据写入器
         * @throws IOException
         */
        void writeData(E e, DataWriter dw) throws IOException;
    }

    /**
     * 写操作接口器
     *
     * @param <T>
     */
    @FunctionalInterface
    interface Writer<T> {

        /**
         * 写入操作
         *
         * @param t 待写入的数据
         * @throws IOException
         */
        void write(T t) throws IOException;
    }

    /**
     * 解码接口类
     *
     * @param <D>
     * @param <E>
     */
    @FunctionalInterface
    interface Decode<D, E> {

        /**
         * 解码
         *
         * @param e 待解码的数据
         * @return 解码过的数据
         */
        D decode(E e);
    }

    /**
     * 读操作接口类
     */
    @FunctionalInterface
    interface ReadData<E> {

        /**
         * 读取操作
         *
         * @param dr 数据读取器
         * @return 读取出的数据
         * @throws IOException
         */
        E readData(DataReader dr) throws IOException;
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
         * @return 读取出的数据
         * @throws IOException
         */
        T read() throws IOException;
    }
}
