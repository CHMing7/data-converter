package com.chm.converter.fst.instantiators;

import com.chm.converter.core.creator.ConstructorFactory;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.utils.MapUtil;
import org.nustaq.serialization.FSTDefaultClassInstantiator;

import java.lang.reflect.Constructor;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-27
 **/
public class CustomFstDefaultClassInstantiator extends FSTDefaultClassInstantiator {

    public final ConstructorFactory constructorFactory = new ConstructorFactory(MapUtil.empty());

    @Override
    public Object newInstance(Class clazz, Constructor cons, boolean doesRequireInit, boolean unsafeAsLastResort) {
        try {
            Object instance = super.newInstance(clazz, cons, doesRequireInit, unsafeAsLastResort);
            if (instance != null) {
                return instance;
            }
            return constructorFactory.get(TypeToken.get(clazz)).construct();
        } catch (Exception e) {
            try {
                return constructorFactory.get(TypeToken.get(clazz)).construct();
            } catch (Exception e1) {
                throw e;
            }
        }
    }
}
