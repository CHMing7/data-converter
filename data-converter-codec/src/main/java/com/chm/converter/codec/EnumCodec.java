package com.chm.converter.codec;

import com.chm.converter.core.utils.MapUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-11
 **/
public class EnumCodec<T extends Enum<T>> implements Codec<T, String> {

    private final Map<String, T> nameToConstant = new HashMap<String, T>();

    private final Map<T, String> constantToName = new HashMap<T, String>();

    public EnumCodec(Class<T> classOfT) {
        this(classOfT, MapUtil.newHashMap());
    }

    public EnumCodec(Class<T> classOfT, Map<String, String> aliasMap) {
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
    public T decode(String s) {
        return nameToConstant.get(s);
    }
}
