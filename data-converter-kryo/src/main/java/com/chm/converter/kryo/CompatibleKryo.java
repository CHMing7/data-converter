package com.chm.converter.kryo;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.ClassUtil;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.ListUtil;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.core.utils.ReflectUtil;
import com.chm.converter.kryo.serializers.CustomizeSerializer;
import com.chm.converter.kryo.serializers.KryoEnumSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import org.objenesis.instantiator.ObjectInstantiator;

import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-26
 **/
public class CompatibleKryo extends Kryo {

    private final Converter<?> converter;

    private final Class<? extends Converter> converterClass;

    protected Map<Class, Serializer> serializerMap = MapUtil.newConcurrentHashMap();

    public CompatibleKryo(Converter<?> converter) {
        this.converter = converter;
        this.converterClass = converter != null ? converter.getClass() : null;
    }

    @Override
    public Serializer<?> getDefaultSerializer(final Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }

        /**
         * Kryo requires every class to provide a zero argument constructor. For any class does not match this condition, kryo have two ways:
         * 1. Use JavaSerializer,
         * 2. Set 'kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));', StdInstantiatorStrategy can generate an instance bypassing the constructor.
         *
         * In practice, it's not possible for Dubbo users to register kryo Serializer for every customized class. So in most cases, customized classes with/without zero argument constructor will
         * default to the default serializer.
         * It is the responsibility of kryo to handle with every standard jdk classes, so we will just escape these classes.
         */
        if (!ClassUtil.isJdk(clazz) && !clazz.isArray() && !clazz.isEnum() && !ClassUtil.checkZeroArgConstructor(clazz)) {
            return new JavaSerializer();
        }
        Serializer res = serializerMap.get(clazz);
        if (res != null) {
            return res;
        }
        Serializer defaultSerializer = super.getDefaultSerializer(clazz);
        if (defaultSerializer instanceof DefaultSerializers.EnumSerializer) {
            return new KryoEnumSerializer(clazz, converter);
        }


        if (defaultSerializer instanceof FieldSerializer) {
            serializerMap.put(clazz, defaultSerializer);
            JavaBeanInfo<?> javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz, converterClass);
            Map<String, FieldInfo> fieldNameFieldInfoMap = javaBeanInfo.getFieldNameFieldInfoMap();
            if (fieldNameFieldInfoMap.isEmpty()) {
                return defaultSerializer;
            }
            // init fieldInfo[]
            FieldSerializer.CachedField[] fields = ((FieldSerializer<?>) defaultSerializer).getFields();
            if (fields.length != fieldNameFieldInfoMap.size()) {
                return defaultSerializer;
            }
            List<FieldSerializer.CachedField> cachedFieldList = ListUtil.toList(fields);
            // 设置ser
            for (FieldSerializer.CachedField cachedField : cachedFieldList) {
                FieldInfo fieldInfo = fieldNameFieldInfoMap.get(cachedField.getField().getName());
                if (fieldInfo == null) {
                    continue;
                }
                Serializer fieldSerializer = getFieldSerializer(this, fieldInfo);
                if (fieldSerializer instanceof CustomizeSerializer) {
                    cachedField.setSerializer(fieldSerializer);
                }
            }
            // 排序
            CollUtil.sort(cachedFieldList, (o1, o2) -> {
                FieldInfo fieldInfo1 = fieldNameFieldInfoMap.get(o1.getField().getName());
                FieldInfo fieldInfo2 = fieldNameFieldInfoMap.get(o2.getField().getName());
                return FieldInfo.FIELD_INFO_COMPARATOR.compare(fieldInfo1, fieldInfo2);
            });
            ReflectUtil.setFieldValue(defaultSerializer, "fields", cachedFieldList.toArray(new FieldSerializer.CachedField[0]));

            return defaultSerializer;
        }
        return defaultSerializer;
    }

    private Serializer<?> getFieldSerializer(Kryo kryo, FieldInfo fieldInfo) {
        Serializer<?> serializer = kryo.getSerializer(fieldInfo.getFieldClass());
        if (serializer instanceof WithFormat) {
            return (Serializer<?>) ((WithFormat) serializer).withDatePattern(fieldInfo.getFormat());
        }
        return serializer;

    }

    /**
     * 重写newInstance方法，使用框架通用方式构建示例
     *
     * @param type
     * @param <T>
     * @return
     */
    @Override
    public <T> T newInstance(Class<T> type) {
        Registration registration = getRegistration(type);
        ObjectInstantiator instantiator = registration.getInstantiator();
        try {
            if (instantiator == null) {
                instantiator = newInstantiator(type);
                registration.setInstantiator(instantiator);
            }
            T t = (T) instantiator.newInstance();
            if (t != null) {
                return t;
            }
            return ConstructorFactory.INSTANCE.get(TypeToken.get(type)).construct();
        } catch (Exception e) {
            try {
                return ConstructorFactory.INSTANCE.get(TypeToken.get(type)).construct();
            } catch (Exception e1) {
                throw e;
            }
        }
    }

}
