package com.chm.converter.xml;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.xml.annotation.XmlProperty;
import com.chm.converter.xml.annotation.XmlRootElement;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-06
 **/
public class XmlClassInfoStorage implements ClassInfoStorage {

    public static final Map<Class<?>, Map<Class<? extends Converter>, Boolean>> INIT_TABLE = MapUtil.newHashMap();

    public static final ClassInfoStorage INSTANCE = new XmlClassInfoStorage();

    @Override
    public <T> void initClassInfo(Class<T> clazz, Class<? extends Converter> converterClass) {
        ClassInfoStorage.super.initClassInfo(clazz, converterClass);
        JavaBeanInfo<T> javaBeanInfo = ClassInfoStorage.get(BEAN_INFO_MAP, clazz, converterClass);
        XmlRootElement xmlRootElement = clazz.getAnnotation(XmlRootElement.class);
        if (xmlRootElement != null) {
            String xmlRootName = xmlRootElement.name();
            String namespace = xmlRootElement.namespace();
            javaBeanInfo.putExpandProperty("xmlRootName", xmlRootName);
            javaBeanInfo.putExpandProperty("namespace", namespace);
        }
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        for (FieldInfo fieldInfo : sortedFieldList) {
            Field field = fieldInfo.getField();
            XmlProperty xmlProperty = null;
            if (field != null) {
                xmlProperty = field.getAnnotation(XmlProperty.class);
            }

            if (xmlProperty == null) {
                Method method = fieldInfo.getMethod();
                if (method != null) {
                    xmlProperty = method.getAnnotation(XmlProperty.class);
                }
            }
            if (xmlProperty != null) {
                boolean isCData = xmlProperty.isCData();
                boolean isAttribute = xmlProperty.isAttribute();
                boolean isText = xmlProperty.isText();
                String namespace = xmlProperty.namespace();
                fieldInfo.putExpandProperty("isCData", isCData);
                fieldInfo.putExpandProperty("isAttribute", isAttribute);
                fieldInfo.putExpandProperty("isText", isText);
                fieldInfo.putExpandProperty("namespace", namespace);
            }
        }
        ClassInfoStorage.put(INIT_TABLE, clazz, converterClass, true);
    }

    @Override
    public boolean isInit(Class<?> clazz, Class<? extends Converter> converterClass) {
        return Boolean.TRUE.equals(ClassInfoStorage.get(INIT_TABLE, clazz, converterClass));
    }
}
