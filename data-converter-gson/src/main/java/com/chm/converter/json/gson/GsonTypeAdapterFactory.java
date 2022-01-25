package com.chm.converter.json.gson;

import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.UseOriginalJudge;
import com.chm.converter.core.utils.CollUtil;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.core.utils.ObjectUtil;
import com.chm.converter.core.utils.ReflectUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class GsonTypeAdapterFactory implements TypeAdapterFactory {

    private final ConstructorConstructor constructorConstructor = new ConstructorConstructor(MapUtil.empty());

    private final Class<? extends Converter> converterClass;

    private final UseOriginalJudge useOriginalJudge;

    public GsonTypeAdapterFactory(Converter<?> converter, UseOriginalJudge useOriginalJudge) {
        this.converterClass = converter != null ? converter.getClass() : null;
        this.useOriginalJudge = useOriginalJudge;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, type);

        if (!(delegateAdapter instanceof ReflectiveTypeAdapterFactory.Adapter)) {
            return null;
        }
        // 校验制定类或其父类集中是否存在Gson框架注解
        if (useOriginalJudge.useOriginalImpl(type.getRawType())) {
            return delegateAdapter;
        }
        return new TypeAdapter<T>() {

            private final Map<FieldInfo, TypeAdapter<?>> FIELD_ADAPTER_MAP = new ConcurrentHashMap<>();

            JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(type.getRawType(), converterClass);

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
                if (CollUtil.isNotEmpty(sortedFieldList)) {
                    out.beginObject();
                    for (FieldInfo fieldInfo : sortedFieldList) {
                        if (!fieldInfo.isSerialize()) {
                            continue;
                        }
                        out.name(fieldInfo.getName());
                        TypeAdapter adapter = getFieldAdapter(fieldInfo);
                        adapter.write(out, fieldInfo.get(value));
                    }
                    out.endObject();
                } else {
                    delegateAdapter.write(out, value);
                }
            }

            @Override
            public T read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                Map<String, FieldInfo> fieldInfoMap = javaBeanInfo.getNameFieldInfoMap();
                if (in.hasNext() && CollUtil.isNotEmpty(fieldInfoMap)) {
                    ObjectConstructor<T> objectConstructor = constructorConstructor.get(type);
                    T instance = objectConstructor.construct();
                    try {
                        in.beginObject();
                        while (in.hasNext()) {
                            String name = in.nextName();
                            FieldInfo fieldInfo = fieldInfoMap.get(name);
                            if (fieldInfo == null) {
                                continue;
                            }
                            TypeAdapter adapter = getFieldAdapter(fieldInfo);
                            Object fieldValue = adapter.read(in);
                            if (!fieldInfo.isDeserialize()) {
                                continue;
                            }
                            fieldInfo.set(instance, fieldValue);
                        }
                    } catch (IllegalStateException e) {
                        throw new JsonSyntaxException(e);
                    }
                    in.endObject();
                    return instance;
                }
                return delegateAdapter.read(in);
            }

            private TypeAdapter<?> getFieldAdapter(FieldInfo fieldInfo) {
                TypeAdapter<?> typeAdapter = FIELD_ADAPTER_MAP.get(fieldInfo);
                if (typeAdapter != null) {
                    return typeAdapter;
                }
                typeAdapter = gson.getAdapter(TypeToken.get(fieldInfo.getFieldClass()));
                if (typeAdapter instanceof GsonJava8TimeAdapter) {
                    String format = fieldInfo.getFormat();
                    String gsonFormat = (String) ReflectUtil.getFieldValue(gson, "datePattern");
                    typeAdapter = ((GsonJava8TimeAdapter<?>) typeAdapter).withDatePattern(ObjectUtil.defaultIfBlank(format, gsonFormat));
                }
                if (typeAdapter instanceof GsonDefaultDateTypeAdapter) {
                    String format = fieldInfo.getFormat();
                    String gsonFormat = (String) ReflectUtil.getFieldValue(gson, "datePattern");
                    typeAdapter = ((GsonDefaultDateTypeAdapter<?>) typeAdapter).withDatePattern(ObjectUtil.defaultIfBlank(format, gsonFormat));
                }
                if (typeAdapter != null) {
                    FIELD_ADAPTER_MAP.put(fieldInfo, typeAdapter);
                    return typeAdapter;
                }
                return gson.getAdapter(fieldInfo.getFieldClass());
            }
        }.nullSafe();
    }

}
