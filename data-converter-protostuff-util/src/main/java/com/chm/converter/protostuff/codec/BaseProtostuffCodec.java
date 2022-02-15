package com.chm.converter.protostuff.codec;

import com.chm.converter.core.utils.StringUtil;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-18
 **/
public abstract class BaseProtostuffCodec<T> extends ProtostuffCodec<T> {

    protected final String name;

    protected BaseProtostuffCodec(Class<T> clazz, String name) {
        super(clazz);
        this.name = name;
    }

    @Override
    public String getFieldName(int number) {
        return number == this.fieldNumber ? this.name : null;
    }

    @Override
    public int getFieldNumber(String name) {
        return StringUtil.equals(this.name, name) ? this.fieldNumber : 0;
    }
}
