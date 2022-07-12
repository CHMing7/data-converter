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
import com.chm.converter.core.UseOriginalJudge;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.core.utils.MapUtil;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-05
 **/
public class Fastjson2ObjectReaderCreator extends ObjectReaderCreator {

    private final ObjectReaderCreator creator;

    private final Class<? extends Converter> converterClass;

    private final UseOriginalJudge useOriginalJudge;

    private final static Map<Class<?>, List<FieldReader>> CLASS_FIELD_READER_LIST_MAP = MapUtil.newConcurrentHashMap();

    public Fastjson2ObjectReaderCreator(ObjectReaderCreator creator, Converter<?> converter, UseOriginalJudge useOriginalJudge) {
        this.creator = creator;
        this.converterClass = converter != null ? converter.getClass() : null;
        this.useOriginalJudge = useOriginalJudge;
    }

    /*@Override
    public <T> ObjectReader<T> createObjectReader(Class<T> objectClass, Type objectType, boolean fieldBased, List<ObjectReaderModule> modules) {
        return this.creator.createObjectReader(objectClass, objectType, fieldBased, modules);
    }*/

    @Override
    public <T> FieldReader[] createFieldReaders(Class<T> objectClass, Type objectType, BeanInfo beanInfo, boolean fieldBased, List<ObjectReaderModule> modules) {
        FieldReader[] fieldReaders = this.creator.createFieldReaders(objectClass, objectType);
        if (useOriginalJudge.useOriginalImpl(objectClass)) {
            return fieldReaders;
        }
        List<FieldReader> readerList = MapUtil.computeIfAbsent(CLASS_FIELD_READER_LIST_MAP, (Class<?>) objectType, cls -> ListUtil.list(true));
        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(objectClass, converterClass);
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
