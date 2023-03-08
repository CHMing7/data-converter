package com.chm.converter.core.codec;

import com.chm.converter.core.codecs.JavaBeanCodec;
import com.chm.converter.core.codecs.ObjectCodec;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalGenerate;

import java.lang.reflect.Type;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-08-11
 **/
public interface UniversalCodecAdapterCreator {

    /**
     * 创建codec适配器
     *
     * @param generate
     * @param type
     * @param creator
     * @param <T>
     * @return
     */
    static <T> T create(UniversalGenerate<Codec> generate,
                        Type type,
                        CodecAdapterCreator<T> creator) {
        return create(generate, TypeToken.get(type), creator);
    }


    /**
     * 创建codec适配器
     *
     * @param generate
     * @param type
     * @param creator
     * @param checkType 是否需要encodeType跟rawType不一致
     * @param <T>
     * @return
     */
    static <T> T create(UniversalGenerate<Codec> generate,
                        Type type,
                        CodecAdapterCreator<T> creator,
                        boolean checkType) {
        return create(generate, TypeToken.get(type), creator, checkType);
    }

    /**
     * 创建codec适配器
     *
     * @param generate
     * @param type
     * @param creator
     * @param <T>
     * @return
     */
    static <T> T create(UniversalGenerate<Codec> generate,
                        TypeToken type,
                        CodecAdapterCreator<T> creator) {
        return create(generate, type, creator, true);
    }


    /**
     * 创建codec适配器
     *
     * @param generate
     * @param type
     * @param creator
     * @param checkType 是否需要encodeType跟rawType不一致
     * @param <T>
     * @return
     */
    static <T> T create(UniversalGenerate<Codec> generate,
                        TypeToken type,
                        CodecAdapterCreator<T> creator,
                        boolean checkType) {
        Codec codec = generate.get(type.getType());
        if (codec != null &&
                !(codec instanceof JavaBeanCodec) &&
                !(codec instanceof ObjectCodec)) {
            if (!checkType || !codec.getEncodeType().getRawType().isAssignableFrom(type.getRawType())) {
                return creator.createInstance(type, codec);
            }
        }
        return null;
    }

    /**
     * 创建优先使用codec适配器
     *
     * @param generate
     * @param type
     * @param creator
     * @param <T>
     * @return
     */
    static <T> T createPriorityUse(UniversalGenerate<Codec> generate,
                                   Type type,
                                   CodecAdapterCreator<T> creator) {
        return createPriorityUse(generate, TypeToken.get(type), creator);
    }


    /**
     * 创建优先使用codec适配器
     *
     * @param generate
     * @param type
     * @param creator
     * @param checkType 是否需要encodeType跟rawType不一致
     * @param <T>
     * @return
     */
    static <T> T createPriorityUse(UniversalGenerate<Codec> generate,
                                   Type type,
                                   CodecAdapterCreator<T> creator,
                                   boolean checkType) {
        return createPriorityUse(generate, TypeToken.get(type), creator, checkType);
    }


    /**
     * 创建优先使用codec适配器
     *
     * @param generate
     * @param type
     * @param creator
     * @param <T>
     * @return
     */
    static <T> T createPriorityUse(UniversalGenerate<Codec> generate,
                                   TypeToken type,
                                   CodecAdapterCreator<T> creator) {
        return createPriorityUse(generate, type, creator, true);
    }

    /**
     * 创建优先使用codec适配器
     *
     * @param generate
     * @param type
     * @param creator
     * @param checkType 是否需要encodeType跟rawType不一致
     * @param <T>
     * @return
     */
    static <T> T createPriorityUse(UniversalGenerate<Codec> generate,
                                   TypeToken type,
                                   CodecAdapterCreator<T> creator,
                                   boolean checkType) {
        Codec codec = generate.get(type.getType());
        if (codec != null &&
                !(codec instanceof JavaBeanCodec) &&
                !(codec instanceof ObjectCodec) &&
                codec.isPriorityUse()) {
            if (!checkType || !codec.getEncodeType().getRawType().isAssignableFrom(type.getRawType())) {
                return creator.createInstance(type, codec);
            }
        }
        return null;
    }

    /**
     * 创建适合使用的codec适配器
     *
     * @param generate
     * @param type
     * @param creator
     * @param isCreateBeanImpl
     * @param beanImplCreator
     * @param <T>
     * @return
     */
    static <T> T createSuitable(UniversalGenerate<Codec> generate,
                                Type type,
                                CodecAdapterCreator<T> creator,
                                boolean isCreateBeanImpl,
                                CodecAdapterCreator<T> beanImplCreator) {
        return createSuitable(generate, TypeToken.get(type), creator, isCreateBeanImpl, beanImplCreator, true);
    }


    /**
     * 创建适合使用的codec适配器
     *
     * @param generate
     * @param type
     * @param creator
     * @param isCreateBeanImpl
     * @param beanImplCreator
     * @param checkType        是否需要encodeType跟rawType不一致
     * @param <T>
     * @return
     */
    static <T> T createSuitable(UniversalGenerate<Codec> generate,
                                Type type,
                                CodecAdapterCreator<T> creator,
                                boolean isCreateBeanImpl,
                                CodecAdapterCreator<T> beanImplCreator,
                                boolean checkType) {
        return createSuitable(generate, TypeToken.get(type), creator, isCreateBeanImpl, beanImplCreator, checkType);
    }


    /**
     * 创建适合使用的codec适配器
     *
     * @param generate
     * @param type
     * @param creator
     * @param isCreateBeanImpl
     * @param beanImplCreator
     * @param <T>
     * @return
     */
    static <T> T createSuitable(UniversalGenerate<Codec> generate,
                                TypeToken type,
                                CodecAdapterCreator<T> creator,
                                boolean isCreateBeanImpl,
                                CodecAdapterCreator<T> beanImplCreator) {
        return createSuitable(generate, type, creator, isCreateBeanImpl, beanImplCreator, true);
    }

    /**
     * 创建适合使用的codec适配器
     *
     * @param generate
     * @param type
     * @param creator
     * @param isCreateBeanImpl
     * @param beanImplCreator
     * @param checkType        是否需要encodeType跟rawType不一致
     * @param <T>
     * @return
     */
    static <T> T createSuitable(UniversalGenerate<Codec> generate,
                                TypeToken type,
                                CodecAdapterCreator<T> creator,
                                boolean isCreateBeanImpl,
                                CodecAdapterCreator<T> beanImplCreator,
                                boolean checkType) {
        Codec codec = generate.get(type.getType());

        if (codec != null &&
                !(codec instanceof ObjectCodec)) {
            boolean sameType = codec.getEncodeType().getRawType().isAssignableFrom(type.getRawType());
            // 优先使用的codec
            if (!(codec instanceof JavaBeanCodec) &&
                    codec.isPriorityUse() &&
                    (!checkType || !sameType)) {
                return creator.createInstance(type, codec);
            }
            // bean Codec
            if (isCreateBeanImpl && codec instanceof JavaBeanCodec) {
                return beanImplCreator.createInstance(type, codec);
            }
            // 使用codec
            if (!(codec instanceof JavaBeanCodec) && (!checkType || !sameType)) {
                return creator.createInstance(type, codec);
            }
        }
        return null;
    }

    /**
     * 适配器实例创建接口
     *
     * @author caihongming
     * @version v1.0
     * @date 2021-09-07
     **/
    interface CodecAdapterCreator<T> {

        /**
         * 创建适配器
         *
         * @param type
         * @param codec
         * @return
         */
        T createInstance(TypeToken type, Codec codec);
    }
}
