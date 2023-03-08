package com.chm.converter.xml.jackson;

import com.chm.converter.core.UseRawJudge;
import com.chm.converter.xml.annotation.XmlRootElement;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;

/**
 * @author caihongming
 * @version v1.0
 * @date 2021-09-10
 **/
public class JacksonXmlAnnotationIntrospector extends com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector {

    private final UseRawJudge useRawJudge;

    public JacksonXmlAnnotationIntrospector(boolean defaultUseWrapper, UseRawJudge useRawJudge) {
        super(defaultUseWrapper);
        this.useRawJudge = useRawJudge;
    }

    @Override
    public PropertyName findRootName(AnnotatedClass ac) {
        Class<?> rawClass = ac.getRawType();
        if (useRawJudge.useRawImpl(rawClass)) {
            return super.findRootName(ac);
        }

        XmlRootElement xmlRootElement = rawClass.getAnnotation(XmlRootElement.class);
        if (xmlRootElement != null) {
            String name = xmlRootElement.name();
            String ns = xmlRootElement.namespace();
            if (name.length() == 0 && ns.length() == 0) {
                return PropertyName.USE_DEFAULT;
            }
            return new PropertyName(name, ns);
        }
        return PropertyName.USE_DEFAULT;
    }
}
