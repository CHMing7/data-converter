package com.chm.converter.xml.jackson;

import com.chm.converter.core.Converter;
import com.chm.converter.jackson.deserializer.JacksonDeserializers;
import com.chm.converter.xml.JacksonXmlConverter;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import java.util.Map;
import java.util.Objects;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-08
 **/
public class JacksonXmlModule extends com.fasterxml.jackson.dataformat.xml.JacksonXmlModule {

    public JacksonXmlModule(Converter<?> converter) {
        super();
        setDeserializers(new JacksonDeserializers(converter));
    }

    /*
    /**********************************************************************
    /* Life-cycle: construction
    /**********************************************************************
     */

    @SuppressWarnings("deprecation")
    @Override
    public void setupModule(SetupContext context) {
        // as well as AnnotationIntrospector
        context.insertAnnotationIntrospector(_constructIntrospector());

        // and finally inform XmlFactory about overrides, if need be:
        if (!Objects.equals(_cfgNameForTextElement, FromXmlParser.DEFAULT_UNNAMED_TEXT_PROPERTY)) {
            XmlMapper m = context.getOwner();
            m.getFactory().setXMLTextElementName(_cfgNameForTextElement);
        }

        // Usually this would be the first call; but here anything added will
        // be stuff user may has added, so do it afterwards instead.
        simpleModuleSetupModule(context);
    }

    private void simpleModuleSetupModule(SetupContext context) {
        if (_serializers != null) {
            context.addSerializers(_serializers);
        }
        if (_deserializers != null) {
            context.addDeserializers(_deserializers);
        }
        if (_keySerializers != null) {
            context.addKeySerializers(_keySerializers);
        }
        if (_keyDeserializers != null) {
            context.addKeyDeserializers(_keyDeserializers);
        }
        if (_abstractTypes != null) {
            context.addAbstractTypeResolver(_abstractTypes);
        }
        if (_valueInstantiators != null) {
            context.addValueInstantiators(_valueInstantiators);
        }
        if (_deserializerModifier != null) {
            context.addBeanDeserializerModifier(_deserializerModifier);
        }
        if (_serializerModifier != null) {
            context.addBeanSerializerModifier(_serializerModifier);
        }
        if (_subtypes != null && _subtypes.size() > 0) {
            context.registerSubtypes(_subtypes.toArray(new NamedType[_subtypes.size()]));
        }
        if (_namingStrategy != null) {
            context.setNamingStrategy(_namingStrategy);
        }
        if (_mixins != null) {
            for (Map.Entry<Class<?>, Class<?>> entry : _mixins.entrySet()) {
                context.setMixInAnnotations(entry.getKey(), entry.getValue());
            }
        }
    }

    /*
    /**********************************************************************
    /* Internal methods
    /**********************************************************************
     */

    @Override
    protected AnnotationIntrospector _constructIntrospector() {
        return new JacksonXmlAnnotationIntrospector(_cfgDefaultUseWrapper, JacksonXmlConverter::checkExistJacksonXmlAnnotation);
    }

}
