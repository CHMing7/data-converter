package com.chm.converter.kryo;

import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.ClassUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-26
 **/
public class CompatibleKryo extends Kryo {

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
        return super.getDefaultSerializer(clazz);
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
            return (T) instantiator.newInstance();
        } catch (Exception e) {
            try {
                return ConstructorFactory.INSTANCE.get(TypeToken.get(type)).construct();
            } catch (Exception e1) {
                throw e;
            }
        }
    }

}
