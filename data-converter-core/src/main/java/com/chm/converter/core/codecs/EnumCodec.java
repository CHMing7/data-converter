package com.chm.converter.core.codecs;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.cfg.ConvertFeature;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.pack.DataReader;
import com.chm.converter.core.pack.DataWriter;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.core.utils.NumberUtil;

import java.io.IOException;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-11
 **/
public class EnumCodec<E extends Enum<E>> implements Codec<E, String> {

    private final Class<E> enumType;

    private final Converter<?> converter;

    private final Map<String, E> nameToConstant = MapUtil.newHashMap();

    private final Map<E, String> constantToName = MapUtil.newHashMap();

    private final E[] enumConstants;


    public EnumCodec(Class<E> classOfT, Converter<?> converter) {
        this.enumType = classOfT;
        this.converter = converter;
        Class<? extends Converter> converterClass = converter != null ? converter.getClass() : null;
        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(classOfT, converterClass);
        Map<String, String> aliasMap = javaBeanInfo.getFieldNameAliasMap();
        for (E constant : classOfT.getEnumConstants()) {
            String name = constant.name();
            if (aliasMap != null && aliasMap.containsKey(name)) {
                name = aliasMap.get(name);
            }
            nameToConstant.put(name, constant);
            constantToName.put(constant, name);
        }
        this.enumConstants = classOfT.getEnumConstants();
    }

    public Class<E> getEnumType() {
        return enumType;
    }

    public Converter<?> getConverter() {
        return converter;
    }

    @Override
    public String encode(E e) {
        if (e == null) {
            return null;
        }
        if (useString()) {
            return constantToName.get(e);
        }
        return index(e);
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
    public E decode(String s) {
        if (useString()) {
            return nameToConstant.get(s);
        }
        int i = NumberUtil.parseInt(s);
        return enumConstants[i];
    }

    @Override
    public TypeToken<E> getDecodeType() {
        return TypeToken.get(enumType);
    }

    @Override
    public String readData(DataReader dr) throws IOException {
        return dr.readString();
    }

    protected boolean useString() {
        return this.converter != null && this.converter.isEnabled(ConvertFeature.ENUMS_USING_TO_STRING);
    }

    protected String index(E e) {
        return (e == null) ? "" : String.valueOf(e.ordinal());
    }
}
