package com.chm.converter.fastjson2.writer;

import com.alibaba.fastjson2.modules.ObjectWriterModule;
import com.alibaba.fastjson2.writer.FieldWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.alibaba.fastjson2.writer.ObjectWriterCreator;
import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.UseOriginalJudge;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.fastjson2.Fastjson2EnumCodec;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-06-30
 **/
public class Fastjson2ObjectWriterModule implements ObjectWriterModule {

    private final Converter<?> converter;

    private final Class<? extends Converter> converterClass;

    private final UseOriginalJudge useOriginalJudge;

    private final static Map<Class<?>, List<FieldWriter>> CLASS_FIELD_WRITER_LIST_MAP = MapUtil.newConcurrentHashMap();

    public Fastjson2ObjectWriterModule(Converter<?> converter, UseOriginalJudge useOriginalJudge) {
        this.converter = converter;
        this.converterClass = converter != null ? converter.getClass() : null;
        this.useOriginalJudge = useOriginalJudge;
    }

    @Override
    public ObjectWriter getObjectWriter(Type objectType, Class objectClass) {
        // 使用原始实现
        if (useOriginalJudge.useOriginalImpl(objectClass)) {
            return null;
        }
        if (Enum.class.isAssignableFrom(objectClass) && objectClass != Enum.class) {
            if (!objectClass.isEnum()) {
                objectClass = objectClass.getSuperclass();
            }
            return new Fastjson2EnumCodec(objectClass, converter);
        }
        return null;
    }

    @Override
    public boolean createFieldWriters(ObjectWriterCreator creator, Class objectType, List<FieldWriter> fieldWriters) {
        // 校验制定类或其父类集中是否存在Fastjson2框架注解
        if (useOriginalJudge.useOriginalImpl(objectType)) {
            return false;
        }
        List<FieldWriter> writerList = MapUtil.computeIfAbsent(CLASS_FIELD_WRITER_LIST_MAP, (Class<?>) objectType, cls -> fieldWriters);
        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(objectType, converterClass);
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        if (CollUtil.isEmpty(writerList) && CollUtil.isNotEmpty(sortedFieldList)) {
            for (FieldInfo fieldInfo : sortedFieldList) {
                fieldWriters.add(new Fastjson2FieldWriter(converter, fieldInfo));
            }
            return true;
        } else if (CollUtil.isNotEmpty(writerList)) {
            fieldWriters.addAll(writerList);
            return true;
        }
        return false;
    }
}
