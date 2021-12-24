package com.chm.converter.protostuff.codec.factory;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.reflect.TypeToken;
import com.chm.converter.core.universal.UniversalFactory;
import com.chm.converter.core.universal.UniversalGenerate;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.core.utils.StringUtil;
import com.chm.converter.protostuff.codec.ProtostuffCodec;
import io.protostuff.Input;
import io.protostuff.Output;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用java bean 编解码
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-11-12
 **/
public class JavaBeanCodecFactory implements UniversalFactory<ProtostuffCodec> {

    private final Converter<?> converter;

    public JavaBeanCodecFactory(Converter<?> converter) {
        this.converter = converter;
    }

    @Override
    public ProtostuffCodec create(UniversalGenerate<ProtostuffCodec> generate, TypeToken<?> typeToken) {
        return new JavaBeanCodec<>(typeToken.getRawType(), generate, converter);
    }


    public static final class JavaBeanCodec<T> extends ProtostuffCodec<T> {

        private final JavaBeanInfo<T> javaBeanInfo;

        private final UniversalGenerate<ProtostuffCodec> codecGenerate;

        private final Map<FieldInfo, ProtostuffCodec> fieldInfoProtostuffCodecMap;

        public JavaBeanCodec(Class<T> clazz, UniversalGenerate<ProtostuffCodec> codecGenerate, Converter<?> converter) {
            super(clazz);
            this.javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(clazz, converter != null ? converter.getClass() : null);
            this.codecGenerate = codecGenerate;
            this.fieldInfoProtostuffCodecMap = new ConcurrentHashMap<>();
        }

        @Override
        public String getFieldName(int number) {
            return javaBeanInfo.getSortedFieldList().get(number - 1).getName();
        }

        @Override
        public int getFieldNumber(String name) {
            List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
            for (int i = 0; i < sortedFieldList.size(); i++) {
                if (StringUtil.equals(sortedFieldList.get(i).getName(), name)) {
                    return i + 1;
                }
            }
            return 0;
        }

        @Override
        public T newMessage() {
            return javaBeanInfo.getObjectConstructor().construct();
        }

        @Override
        public void writeTo(Output output, T message) throws IOException {
            List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
            for (int i = 0; i < sortedFieldList.size(); i++) {
                FieldInfo fieldInfo = sortedFieldList.get(i);
                Object o = fieldInfo.get(message);
                if (o == null) {
                    continue;
                }
                ProtostuffCodec codec = getFieldProtostuffCodec(fieldInfo);
                output.writeObject(i + 1, o, codec, false);
            }
        }

        @Override
        public T mergeFrom(Input input) throws IOException {
            List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
            T construct = javaBeanInfo.getObjectConstructor().construct();
            for (int n = input.readFieldNumber(this); n != 0; n = input.readFieldNumber(this)) {
                final FieldInfo fieldInfo = sortedFieldList.get(n - 1);
                if (fieldInfo == null) {
                    input.handleUnknownField(n, this);
                } else {
                    ProtostuffCodec codec = getFieldProtostuffCodec(fieldInfo);
                    Object o = input.mergeObject(null, codec);
                    fieldInfo.set(construct, o);
                }
            }
            return construct;
        }

        private ProtostuffCodec<?> getFieldProtostuffCodec(FieldInfo fieldInfo) {
            return MapUtil.computeIfAbsent(fieldInfoProtostuffCodecMap, fieldInfo, info -> {
                ProtostuffCodec<?> codec = codecGenerate.get(fieldInfo.getTypeToken());
                if (codec instanceof Java8TimeCodecFactory.Java8TimeCodec) {
                    codec = ((Java8TimeCodecFactory.Java8TimeCodec<?>) codec).withDatePattern(fieldInfo.getFormat());
                }
                if (codec instanceof DefaultDateCodecFactory.DefaultDateCodec) {
                    codec = ((DefaultDateCodecFactory.DefaultDateCodec<?>) codec).withDatePattern(fieldInfo.getFormat());
                }
                return codec;
            });
        }
    }
}
