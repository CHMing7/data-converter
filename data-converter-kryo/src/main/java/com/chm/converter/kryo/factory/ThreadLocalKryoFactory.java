package com.chm.converter.kryo.factory;

import com.esotericsoftware.kryo.Kryo;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-26
 **/
public class ThreadLocalKryoFactory extends AbstractKryoFactory {

    private final ThreadLocal<Kryo> holder = ThreadLocal.withInitial(this::create);

    @Override
    public Kryo getKryo() {
        return holder.get();
    }
}
