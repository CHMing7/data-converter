package com.chm.converter.spearal.impl.introspector;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.UseRawJudge;
import com.chm.converter.spearal.impl.property.PropertyDelegate;
import org.spearal.SpearalContext;
import org.spearal.configuration.PropertyFactory.Property;

import java.util.List;

import static org.spearal.configuration.PropertyFactory.ZERO_PROPERTIES;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-23
 **/
public class IntrospectorImpl extends org.spearal.impl.introspector.IntrospectorImpl {

    private final Class<? extends Converter> converterClass;

    private final UseRawJudge useRawJudge;

    public IntrospectorImpl(Converter<?> converter, UseRawJudge useRawJudge) {
        this.converterClass = converter != null ? converter.getClass() : null;
        this.useRawJudge = useRawJudge;
    }

    @Override
    protected Property[] introspectBeanProperties(SpearalContext context, Class<?> cls) {
        return introspectProperties(context, cls, true);
    }

    @Override
    protected Property[] introspectProxyProperties(SpearalContext context, Class<?> cls) {
        return introspectProperties(context, cls, false);
    }

    private Property[] introspectProperties(SpearalContext context, Class<?> cls, boolean isAddSuperClass) {
        if (useRawJudge.useRawImpl(cls)) {
            return super.introspectBeanProperties(context, cls);
        }
        if (cls == Object.class || cls == null) {
            return ZERO_PROPERTIES;
        }

        JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(cls, converterClass);
        List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
        Property[] properties = new Property[sortedFieldList.size()];

        for (int i = 0; i < sortedFieldList.size(); i++) {
            FieldInfo fieldInfo = sortedFieldList.get(i);
            Property property = getProperty(context, fieldInfo);
            properties[i] = property;
        }

        Class<?> superCls = cls.getSuperclass();
        if (superCls == Object.class || superCls == null || !isAddSuperClass) {
            return properties;
        }
        Property[] superProperties = getProperties(context, superCls);
        return concat(superProperties, properties);
    }

    private Property getProperty(SpearalContext context, FieldInfo fieldInfo) {
        Property property = context.createProperty(fieldInfo.getName(), fieldInfo.getField(), fieldInfo.getGetter(), fieldInfo.getSetter());
        return property != null && !(property instanceof PropertyDelegate) ? new PropertyDelegate(fieldInfo, property) : property;
    }
}
