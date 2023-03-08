package com.chm.converter.fastjson2.reader;

import com.alibaba.fastjson2.codec.BeanInfo;
import com.alibaba.fastjson2.modules.ObjectReaderModule;
import com.alibaba.fastjson2.reader.FieldReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.reader.ObjectReaderCreator;
import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.UseRawJudge;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.codec.UniversalCodecAdapterCreator;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.fastjson2.Fastjson2CoreCodec;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-07-05
 **/
public class Fastjson2ObjectReaderCreator extends ObjectReaderCreator {

    private final ObjectReaderCreator creator;

    private final Class<? extends Converter> converterClass;

    private final UniversalGenerate<Codec> generate;

    private final UseRawJudge useRawJudge;

    private final static Map<Class<?>, List<FieldReader>> CLASS_FIELD_READER_LIST_MAP = MapUtil.newConcurrentHashMap();

    public Fastjson2ObjectReaderCreator(ObjectReaderCreator creator, Converter<?> converter, UseRawJudge useRawJudge) {
        this(creator, converter, null, useRawJudge);
    }

    public Fastjson2ObjectReaderCreator(ObjectReaderCreator creator, Converter<?> converter, UniversalGenerate<Codec> generate, UseRawJudge useRawJudge) {
        this.creator = creator;
        this.converterClass = converter != null ? converter.getClass() : null;
        this.generate = generate != null ? generate : DataCodecGenerate.getDataCodecGenerate(converter);
        this.useRawJudge = useRawJudge;
    }

    @Override
    public <T> ObjectReader<T> createObjectReader(Class<T> objectClass, Type objectType, boolean fieldBased, List<ObjectReaderModule> modules) {
        Class<?> clazz = ClassUtil.getClassByType(objectType);
        // 使用原始实现
        if (useRawJudge.useRawImpl(clazz)) {
            return null;
        }
        ObjectReader priorityUse = UniversalCodecAdapterCreator.createPriorityUse(this.generate, objectType,
                (type, codec) -> new Fastjson2CoreCodec<>(codec));
        if (priorityUse != null) {
            return priorityUse;
        }
        return super.createObjectReader(objectClass, objectType, fieldBased, modules);
    }

    @Override
    public <T> FieldReader[] createFieldReaders(Class<T> objectClass, Type objectType, BeanInfo beanInfo, boolean fieldBased, List<ObjectReaderModule> modules) {
        FieldReader[] fieldReaders = this.creator.createFieldReaders(objectClass, objectType);
        if (useRawJudge.useRawImpl(objectClass)) {
            return fieldReaders;
        }
        List<FieldReader> readerList = MapUtil.computeIfAbsent(CLASS_FIELD_READER_LIST_MAP, objectClass, cls -> ListUtil.list(true));
        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(objectType, converterClass);
        Map<String, FieldInfo> fieldNameFieldInfoMap = javaBeanInfo.getFieldNameFieldInfoMap();
        if (CollUtil.isEmpty(readerList) && CollUtil.isNotEmpty(fieldNameFieldInfoMap)) {
            for (FieldReader fieldReader : fieldReaders) {
                FieldInfo fieldInfo = fieldNameFieldInfoMap.get(fieldReader.getFieldName());
                if (fieldInfo == null || !fieldInfo.isDeserialize()) {
                    continue;
                }
                readerList.add(new Fastjson2FieldReader<>(fieldInfo, fieldReader.getSchema()));
            }
            return readerList.toArray(new FieldReader[0]);
        } else if (CollUtil.isNotEmpty(readerList)) {
            return readerList.toArray(new FieldReader[0]);
        }
        return fieldReaders;
    }

}
