package com.chm.converter.fst.serialization;

import com.chm.converter.core.Converter;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.fst.serializers.FstSerializer;
import org.nustaq.serialization.FSTObjectSerializer;
import org.nustaq.serialization.FSTSerializerRegistry;
import org.nustaq.serialization.FSTSerializerRegistryDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-30
 **/
public class FstSerializerRegistryDelegate implements FSTSerializerRegistryDelegate {

    private static FstSerializerRegistryDelegate DEFAULT;

    private final UniversalGenerate<FstSerializer> generate;

    private FSTSerializerRegistry serializerRegistry;

    private FSTSerializerRegistry skipDelegateSerializerRegistry;

    public FstSerializerRegistryDelegate() {
        this(null, null);
    }

    public FstSerializerRegistryDelegate(List<UniversalFactory<FstSerializer>> protostuffCodecFactories, Converter<?> converter) {
        generate = new UniversalGenerate<>(protostuffCodecFactories);
    }

    public static FstSerializerRegistryDelegate getDefault() {
        if (DEFAULT == null) {
            DEFAULT = newDefault();
        }
        return DEFAULT;
    }

    public static FstSerializerRegistryDelegate newDefault() {
        return newDefault(null);
    }

    public static FstSerializerRegistryDelegate newDefault(Converter<?> converter) {
        List<UniversalFactory<FstSerializer>> factories = new ArrayList<>();
        return new FstSerializerRegistryDelegate(factories, converter);
    }

    public UniversalGenerate<FstSerializer> getGenerate() {
        return generate;
    }

    @Override
    public FSTObjectSerializer getSerializer(Class cl) {
        FstSerializer serializer = generate.get(cl);
        if (serializer != null) {
            return serializer;
        }
        return skipDelegateSerializerRegistry.getSerializer(cl);
    }

    public FSTSerializerRegistry getSerializerRegistry() {
        return serializerRegistry;
    }

    public void setSerializerRegistry(FSTSerializerRegistry serializerRegistry) {
        this.serializerRegistry = serializerRegistry;
    }

    public FSTSerializerRegistry getSkipDelegateSerializerRegistry() {
        return skipDelegateSerializerRegistry;
    }

    public void setSkipDelegateSerializerRegistry(FSTSerializerRegistry skipDelegateSerializerRegistry) {
        this.skipDelegateSerializerRegistry = skipDelegateSerializerRegistry;
    }
}

