package com.chm.converter.kryo;

import com.esotericsoftware.kryo.Kryo;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-26
 **/
@FunctionalInterface
public interface KryoSerializerRegister {

    /**
     * 注册kryo解析器
     *
     * @param kryo
     */
    void registerSerializers(Kryo kryo);
}
