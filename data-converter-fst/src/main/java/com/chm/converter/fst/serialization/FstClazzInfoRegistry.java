package com.chm.converter.fst.serialization;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.UseRawJudge;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.core.utils.ReflectUtil;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTClazzInfoRegistry;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.util.FSTMap;

import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-02
 **/
public class FstClazzInfoRegistry extends FSTClazzInfoRegistry {

    protected FSTMap mInfos = new FSTMap(97);

    private final Class<? extends Converter> converterClass;

    private final UseRawJudge useRawJudge;

    public FstClazzInfoRegistry(Converter<?> converter, UseRawJudge useRawJudge) {
        this.converterClass = converter != null ? converter.getClass() : null;
        this.useRawJudge = useRawJudge;
    }

    @Override
    public FSTClazzInfo getCLInfo(Class c, FSTConfiguration conf) {
        FSTClazzInfo clInfo = super.getCLInfo(c, conf);
        if (useRawJudge.useRawImpl(c)) {
            return clInfo;
        }

        if (getSerializerRegistry().getSerializer(c) != null) {
            return clInfo;
        }

        FSTClazzInfo res = (FSTClazzInfo) mInfos.get(c);
        if (res != null) {
            return res;
        }
        FSTClazzInfo newClInfo = new FSTClazzInfo(conf, c, this, this.isIgnoreAnnotations());
        mInfos.put(c, newClInfo);
        // 枚举直接返回
        if (c.isEnum()) {
            return newClInfo;
        }

        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(c, converterClass);
        Map<String, FieldInfo> fieldNameFieldInfoMap = javaBeanInfo.getFieldNameFieldInfoMap();
        if (fieldNameFieldInfoMap.isEmpty()) {
            return newClInfo;
        }
        // init fieldInfo[]
        FSTClazzInfo.FSTFieldInfo[] fieldInfos = newClInfo.getFieldInfo();
        List<FSTClazzInfo.FSTFieldInfo> fstFieldInfoList = ListUtil.toList(fieldInfos);
        if (fieldInfos.length != fieldNameFieldInfoMap.size()) {
            return newClInfo;
        }
        // 排序
        CollUtil.sort(fstFieldInfoList, (o1, o2) -> {
            FieldInfo fieldInfo1 = fieldNameFieldInfoMap.get(o1.getName());
            FieldInfo fieldInfo2 = fieldNameFieldInfoMap.get(o2.getName());
            return FieldInfo.FIELD_INFO_COMPARATOR.compare(fieldInfo1, fieldInfo2);
        });
        ReflectUtil.setFieldValue(newClInfo, "fieldInfo", fstFieldInfoList.toArray(new FSTClazzInfo.FSTFieldInfo[0]));

        // init fieldMap
        FSTMap<String, FSTClazzInfo.FSTFieldInfo> fieldMap = new FSTMap<>(fstFieldInfoList.size());
        for (FSTClazzInfo.FSTFieldInfo fstFieldInfo : fstFieldInfoList) {
            FieldInfo fieldInfo = fieldNameFieldInfoMap.get(fstFieldInfo.getName());
            fieldMap.put(fieldInfo.getDeclaredClass().getName() + "#" + fieldInfo.getName(), fstFieldInfo);
            fieldMap.put(fieldInfo.getName(), fstFieldInfo);
        }
        ReflectUtil.setFieldValue(newClInfo, "fieldMap", fieldMap);
        return newClInfo;
    }

}
