package com.chm.converter.core.value;

import com.chm.converter.core.lang.Pair;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Value工厂类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-12-04
 **/
public interface ValueFactory {

    ImmutableNullValue newNull();

    ImmutableBooleanValue newBoolean(boolean v);

    ImmutableIntegerValue newInteger(byte v);

    ImmutableIntegerValue newInteger(short v);

    ImmutableIntegerValue newInteger(int v);

    ImmutableIntegerValue newInteger(long v);

    ImmutableIntegerValue newInteger(BigInteger v);

    ImmutableFloatValue newFloat(float v);

    ImmutableFloatValue newFloat(double v);

    ImmutableBinaryValue newBinary(byte[] b);

    ImmutableBinaryValue newBinary(byte[] b, boolean omitCopy);

    ImmutableBinaryValue newBinary(byte[] b, int off, int len);

    ImmutableBinaryValue newBinary(byte[] b, int off, int len, boolean omitCopy);

    ImmutableStringValue newString(String s);

    ImmutableStringValue newString(byte[] b);

    ImmutableStringValue newString(byte[] b, boolean omitCopy);

    ImmutableStringValue newString(byte[] b, int off, int len);

    ImmutableStringValue newString(byte[] b, int off, int len, boolean omitCopy);

    ImmutableArrayValue newArray(List<? extends Value> list);

    ImmutableArrayValue newArray(Value... array);

    ImmutableArrayValue newArray(Value[] array, boolean omitCopy);

    ImmutableArrayValue emptyArray();

    <K extends Value, V extends Value> ImmutableMapValue newMap(Map<K, V> map);

    ImmutableMapValue newMap(Value... kvs);

    ImmutableMapValue newMap(Value[] kvs, boolean omitCopy);

    ImmutableMapValue emptyMap();

    MapValue newMap(Map.Entry<? extends Value, ? extends Value>... pairs);

    MapValue newMap(Pair<? extends Value, ? extends Value>... pairs);

    Map.Entry<Value, Value> newMapEntry(Value key, Value value);

    ImmutableCollectionValue newCollection(Value... values);

    ImmutableCollectionValue newCollection(Collection<Value> c);

    <E extends Enum<E>> ImmutableEnumValue newEnum(Enum<E> e);

    <C> ImmutableClassValue newClass(Class<C> cls);

    ImmutableExtensionValue newExtension(Object obj);

    <V extends Value> V newValue(Object value);
}
