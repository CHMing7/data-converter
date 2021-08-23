package com.chm.converter.json.gson;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.chm.converter.ClassInfoStorage;
import com.chm.converter.FieldInfo;
import com.chm.converter.JavaBeanInfo;
import com.google.gson.*;
import com.google.gson.internal.bind.JsonTreeReader;
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

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> jsonElementAdapter = gson.getAdapter(JsonElement.class);

        if (!(delegateAdapter instanceof ReflectiveTypeAdapterFactory.Adapter)) {
            return null;
        }
        return new TypeAdapter<T>() {

            private final Map<FieldInfo, TypeAdapter<?>> FIELD_ADAPTER_MAP = new ConcurrentHashMap<>();

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                Class<?> rawClass = ClassUtil.getClass(value);
                JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(rawClass);
                List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
                if (CollectionUtil.isNotEmpty(sortedFieldList)) {
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
                JsonElement jsonElement = jsonElementAdapter.read(in);
                JavaBeanInfo javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(type.getRawType());
                List<FieldInfo> sortedFieldList = javaBeanInfo.getSortedFieldList();
                if (jsonElement.isJsonObject() && CollectionUtil.isNotEmpty(sortedFieldList)) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    for (FieldInfo fieldInfo : sortedFieldList) {
                        JsonElement value = jsonObject.get(fieldInfo.getName());
                        if (!fieldInfo.isDeserialize() || value == null) {
                            continue;
                        }
                        jsonObject.remove(fieldInfo.getName());
                        TypeAdapter adapter = getFieldAdapter(fieldInfo);
                        JsonReader jsonReader = new JsonTreeReader(value);
                        adapter.read(jsonReader);
                        jsonObject.add(fieldInfo.getFieldName(), value);
                    }
                }
                return delegateAdapter.fromJsonTree(jsonElement);
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
