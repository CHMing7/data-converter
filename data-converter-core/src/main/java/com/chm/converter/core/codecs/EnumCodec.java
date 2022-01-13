package com.chm.converter.core.codecs;

import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.MapUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-11
 **/
public class EnumCodec<T extends Enum<T>> implements Codec<T, String> {

    private final Class<T> enumType;

    private final Map<String, T> nameToConstant = new HashMap<String, T>();

    private final Map<T, String> constantToName = new HashMap<T, String>();

    public EnumCodec(Class<T> classOfT) {
        this(classOfT, MapUtil.newHashMap());
    }

    public EnumCodec(Class<T> classOfT, Map<String, String> aliasMap) {
        this.enumType = classOfT;
        for (T constant : classOfT.getEnumConstants()) {
            String name = constant.name();
            if (aliasMap != null && aliasMap.containsKey(name)) {
                name = aliasMap.get(name);
            }
            nameToConstant.put(name, constant);
            constantToName.put(constant, name);
        }
    }

    @Override
    public String encode(T t) {
        return t == null ? null : constantToName.get(t);
    }

    @Override
    public TypeToken<String> getEncodeType() {
        return TypeToken.get(String.class);
    }

    @Override
    public void writeData(String s, DataWriter dw) throws IOException {
        if (s == null) {
            dw.writeNull();
            return;
        }
        dw.writeString(s);
    }

    @Override
    public T decode(String s) {
        return nameToConstant.get(s);
    }

    @Override
    public TypeToken<T> getDecodeType() {
        return TypeToken.get(enumType);
    }

    @Override
    public String readData(DataReader dr) throws IOException {
        return dr.readString();
    }
}
