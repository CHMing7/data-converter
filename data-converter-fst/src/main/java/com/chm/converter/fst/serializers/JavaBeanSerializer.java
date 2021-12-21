package com.chm.converter.fst.serializers;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.utils.MapUtil;
import org.nustaq.serialization.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-01
 **/
public class JavaBeanSerializer<T> extends FstSerializer {

    private final JavaBeanInfo<T> javaBeanInfo;

    private final FSTSerializerRegistry serRegistry;

    private final Map<FieldInfo, FstSerializer> fieldInfoSerializerMap;

    public JavaBeanSerializer(Class<T> clazz, FSTSerializerRegistry serRegistry, Converter<?> converter) {
        this.javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz, converter != null ? converter.getClass() : null);
        this.serRegistry = serRegistry;
        this.fieldInfoSerializerMap = new ConcurrentHashMap<>();
    }

    @Override
    public void writeObject(FSTObjectOutput out, Object toWrite, FSTClazzInfo clzInfo, FSTClazzInfo.FSTFieldInfo referencedBy, int streamPosition) throws IOException {
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        for (FieldInfo fieldInfo : sortedFieldList) {
            Object o = fieldInfo.get(toWrite);
            if (o == null || !fieldInfo.isSerialize()) {
                out.getCodec().writeTag(FSTObjectOutput.NULL, null, 0, o, out);
                continue;
            }
            FstSerializer fieldSerializer = getFieldSerializer(fieldInfo);
            FSTClazzInfo.FSTFieldInfo fieldClzInfo = clzInfo.getFieldInfo(fieldInfo.getFieldName(), fieldInfo.getDeclaredClass());
            FSTClazzInfo clazzInfo = out.getConf().getClazzInfo(fieldInfo.getFieldClass());
            int pos = out.getCodec().getWritten();
            fieldSerializer.writeObject(out, o, clazzInfo, fieldClzInfo, pos);
        }
    }

    @Override
    public Object instantiate(Class objectClass, FSTObjectInput in, FSTClazzInfo serializationInfo, FSTClazzInfo.FSTFieldInfo referencee, int streamPosition) throws Exception {
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        T construct = javaBeanInfo.getObjectConstructor().construct();
        for (FieldInfo fieldInfo : sortedFieldList) {
            byte code = in.getCodec().readObjectHeaderTag();
            if (code == FSTObjectOutput.NULL) {
                continue;
            }
            FstSerializer fieldSerializer = getFieldSerializer(fieldInfo);
            FSTClazzInfo.FSTFieldInfo fieldClzInfo = serializationInfo.getFieldInfo(fieldInfo.getFieldName(), fieldInfo.getDeclaredClass());
            FSTClazzInfo clazzInfo = in.getConf().getClazzInfo(fieldInfo.getFieldClass());
            int pos = in.getCodec().getInputPos();
            Object instantiate = fieldSerializer.instantiate(fieldInfo.getFieldClass(), in, clazzInfo, fieldClzInfo, pos);
            if (fieldInfo.isDeserialize()) {
                fieldInfo.set(construct, instantiate);
            }
        }
        return construct;
    }

    private FstSerializer getFieldSerializer(FieldInfo fieldInfo) {
        return MapUtil.computeIfAbsent(fieldInfoSerializerMap, fieldInfo, info -> {
            FSTObjectSerializer ser = serRegistry.getSerializer(fieldInfo.getFieldClass());
            if (ser instanceof DefaultDateSerializer) {
                ser = ((DefaultDateSerializer) ser).withDatePattern(fieldInfo.getFormat());
            }
            if (ser instanceof Java8TimeSerializer) {
                ser = ((Java8TimeSerializer) ser).withDatePattern(fieldInfo.getFormat());
            }

            return ser instanceof FstSerializerDelegate ?
                    (FstSerializerDelegate) ser : new FstSerializerDelegate(ser);
        });
    }

}
