package com.chm.converter.json.fastjson.serializer;

import com.alibaba.fastjson.serializer.*;
import com.alibaba.fastjson.util.FieldInfo;

import java.util.Collection;

/**
 * 自定义属性序列化类
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-08-18
 **/
public class FastjsonFieldSerializer extends FieldSerializer {

    private final ObjectSerializer fieldSerializer;

    private final Class<?> runtimeFieldClass;

    public FastjsonFieldSerializer(Class<?> beanType, FieldInfo fieldInfo, ObjectSerializer fieldSerializer) {
        super(beanType, fieldInfo);
        Class<?> runtimeFieldClass;
        runtimeFieldClass = this.fieldInfo.fieldClass;

        this.fieldSerializer = fieldSerializer;
        this.runtimeFieldClass = runtimeFieldClass;

    }

    @Override
    public void writeValue(JSONSerializer serializer, Object propertyValue) throws Exception {

        final int fieldFeatures
                = (disableCircularReferenceDetect
                ? (fieldInfo.serialzeFeatures | SerializerFeature.DisableCircularReferenceDetect.mask)
                : fieldInfo.serialzeFeatures) | features;

        if (propertyValue == null) {
            SerializeWriter out = serializer.out;

            if (fieldInfo.fieldClass == Object.class
                    && out.isEnabled(SerializerFeature.WRITE_MAP_NULL_FEATURES)) {
                out.writeNull();
                return;
            }

            if (Number.class.isAssignableFrom(runtimeFieldClass)) {
                out.writeNull(features, SerializerFeature.WriteNullNumberAsZero.mask);
                return;
            } else if (String.class == runtimeFieldClass) {
                out.writeNull(features, SerializerFeature.WriteNullStringAsEmpty.mask);
                return;
            } else if (Boolean.class == runtimeFieldClass) {
                out.writeNull(features, SerializerFeature.WriteNullBooleanAsFalse.mask);
                return;
            } else if (Collection.class.isAssignableFrom(runtimeFieldClass)
                    || runtimeFieldClass.isArray()) {
                out.writeNull(features, SerializerFeature.WriteNullListAsEmpty.mask);
                return;
            }


            if ((out.isEnabled(SerializerFeature.WRITE_MAP_NULL_FEATURES))
                    && fieldSerializer instanceof JavaBeanSerializer) {
                out.writeNull();
                return;
            }

            fieldSerializer.write(serializer, null, fieldInfo.name, fieldInfo.fieldType, fieldFeatures);
            return;
        }

        if (fieldInfo.isEnum) {
            if (writeEnumUsingName) {
                serializer.out.writeString(((Enum<?>) propertyValue).name());
                return;
            }

            if (writeEnumUsingToString) {
                serializer.out.writeString(propertyValue.toString());
                return;
            }
        }

        Class<?> valueClass = propertyValue.getClass();
        ObjectSerializer valueSerializer;
        if (valueClass == runtimeFieldClass || serializeUsing) {
            valueSerializer = fieldSerializer;
        } else {
            valueSerializer = serializer.getObjectWriter(valueClass);
        }

        if ((features & SerializerFeature.WriteClassName.mask) != 0
                && valueClass != fieldInfo.fieldClass
                && valueSerializer instanceof JavaBeanSerializer) {
            valueSerializer.write(serializer, propertyValue, fieldInfo.name, fieldInfo.fieldType, fieldFeatures);
            return;
        }

        if (browserCompatible && (fieldInfo.fieldClass == long.class || fieldInfo.fieldClass == Long.class)) {
            long value = (Long) propertyValue;
            if (value > 9007199254740991L || value < -9007199254740991L) {
                serializer.getWriter().writeString(Long.toString(value));
                return;
            }
        }

        valueSerializer.write(serializer, propertyValue, fieldInfo.name, fieldInfo.fieldType, fieldFeatures);
    }
}
