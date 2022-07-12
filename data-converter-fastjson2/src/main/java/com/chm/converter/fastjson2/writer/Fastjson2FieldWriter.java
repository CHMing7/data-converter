package com.chm.converter.fastjson2.writer;

import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.util.Fnv;
import com.alibaba.fastjson2.writer.FieldWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.codec.WithFormat;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.core.utils.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-06-30
 **/
public class Fastjson2FieldWriter implements FieldWriter {

    private static final Map<FieldInfo, ObjectWriter> FIELD_INFO_OBJECT_WRITER_MAP = MapUtil.newConcurrentHashMap();

    private final Converter<?> converter;

    private final FieldInfo fieldInfo;

    private final byte[] nameWithColonUTF8;

    private final char[] nameWithColonUTF16;

    private final byte[] nameJSONB;

    private final long hashCode;

    public Fastjson2FieldWriter(Converter<?> converter, FieldInfo fieldInfo) {
        this.converter = converter;
        this.fieldInfo = fieldInfo;
        String name = fieldInfo.getName();
        int nameLength = name.length();
        int utflen = nameLength + 3;
        for (int i = 0; i < nameLength; ++i) {
            char c = name.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                // skip
            } else if (c > 0x07FF) {
                utflen += 2;
            } else {
                utflen += 1;
            }
        }
        byte[] bytes = new byte[utflen];
        int off = 0;
        bytes[off++] = '"';
        for (int i = 0; i < nameLength; ++i) {
            char c = name.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                bytes[off++] = (byte) c;
            } else if (c > 0x07FF) {
                // 2 bytes, 11 bits
                bytes[off++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytes[off++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                bytes[off++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            } else {
                bytes[off++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                bytes[off++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
        }
        bytes[off++] = '"';
        bytes[off++] = ':';

        this.nameWithColonUTF8 = bytes;

        this.nameWithColonUTF16 = new char[nameLength + 3];
        this.nameWithColonUTF16[0] = '"';
        name.getChars(0, name.length(), nameWithColonUTF16, 1);
        this.nameWithColonUTF16[nameWithColonUTF16.length - 2] = '"';
        this.nameWithColonUTF16[nameWithColonUTF16.length - 1] = ':';
        this.nameJSONB = JSONB.toBytes(name);
        this.hashCode = Fnv.hashCode64(name);
    }

    @Override
    public boolean isFieldClassSerializable() {
        return fieldInfo.isSerialize();
    }

    @Override
    public int ordinal() {
        return fieldInfo.getOrdinal();
    }

    @Override
    public String getFormat() {
        return fieldInfo.getFormat();
    }

    @Override
    public String getFieldName() {
        return fieldInfo.getName();
    }

    @Override
    public Class getFieldClass() {
        return fieldInfo.getFieldClass();
    }

    @Override
    public Type getFieldType() {
        return fieldInfo.getFieldType();
    }

    @Override
    public Field getField() {
        return fieldInfo.getField();
    }

    @Override
    public Method getMethod() {
        return fieldInfo.getMethod();
    }

    @Override
    public Member getFieldOrMethod() {
        return fieldInfo.getMember();
    }

    @Override
    public Object getFieldValue(Object object) {
        return fieldInfo.get(object);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Fastjson2FieldWriter) {
            Fastjson2FieldWriter fieldWriter = (Fastjson2FieldWriter) o;
            return this.fieldInfo.compareTo(fieldWriter.fieldInfo);
        }
        return FieldWriter.super.compareTo(o);
    }

    @Override
    public void writeDate(JSONWriter jsonWriter, boolean writeFieldName, long millis) {
        if (writeFieldName) {
            writeFieldName(jsonWriter);
        }
        if (StringUtil.isNotBlank(fieldInfo.getFormat())) {
            ZoneId zoneId = converter.getTimeZone().toZoneId();
            ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), zoneId);
            ObjectWriter objectWriter = jsonWriter.getObjectWriter(ZonedDateTime.class);
            if (objectWriter instanceof WithFormat) {
                objectWriter = (ObjectWriter) ((WithFormat) objectWriter).withDatePattern(fieldInfo.getFormat());
            }
            objectWriter.write(jsonWriter, zdt);
        } else {
            ObjectWriter objectWriter = jsonWriter.getObjectWriter(fieldInfo.getFieldClass());
            objectWriter.write(jsonWriter, new Date(millis));
        }
    }

    @Override
    public final void writeFieldName(JSONWriter jsonWriter) {
        if (jsonWriter.isJSONB()) {
            jsonWriter.writeNameRaw(nameJSONB, hashCode);
            return;
        }

        boolean ueSingleQuotes = jsonWriter.isUseSingleQuotes();

        if (!ueSingleQuotes) {
            if (jsonWriter.isUTF8()) {
                jsonWriter.writeNameRaw(nameWithColonUTF8);
                return;
            }

            if (jsonWriter.isUTF16()) {
                jsonWriter.writeNameRaw(nameWithColonUTF16);
                return;
            }
        }

        jsonWriter.writeName(fieldInfo.getName());
        jsonWriter.writeColon();
    }

    @Override
    public void writeEnumJSONB(JSONWriter jsonWriter, Enum e) {
        if (fieldInfo.getFieldClass().isEnum()) {
            writeFieldName(jsonWriter);
            ObjectWriter writer = getWriter(jsonWriter);
            writer.write(jsonWriter, e);
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeEnum(JSONWriter jsonWriter, Enum e) {
        if (fieldInfo.getFieldClass().isEnum()) {
            writeFieldName(jsonWriter);
            ObjectWriter writer = getWriter(jsonWriter);
            writer.write(jsonWriter, e);
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeValue(JSONWriter jsonWriter, Object object) {
        ObjectWriter writer = getWriter(jsonWriter);
        writer.write(jsonWriter, object);
    }

    @Override
    public boolean write(JSONWriter jsonWriter, Object o) {
        Object value = getFieldValue(o);
        if (value == null) {
            long features = this.getFeatures() | jsonWriter.getFeatures();
            if ((features & JSONWriter.Feature.WriteNulls.mask) != 0) {
                writeFieldName(jsonWriter);
                jsonWriter.writeNull();
                return true;
            } else {
                return false;
            }
        }
        writeFieldName(jsonWriter);
        ObjectWriter writer = getWriter(jsonWriter);
        writer.write(jsonWriter, value);
        return true;
    }

    @Override
    public ObjectWriter getObjectWriter(JSONWriter jsonWriter, Class valueClass) {
        if (fieldInfo.getFieldClass().isAssignableFrom(valueClass)) {
            return getWriter(jsonWriter);
        }
        return FieldWriter.super.getObjectWriter(jsonWriter, valueClass);
    }

    private ObjectWriter getWriter(JSONWriter jsonWriter) {
        return MapUtil.computeIfAbsent(FIELD_INFO_OBJECT_WRITER_MAP, fieldInfo, info -> {
            ObjectWriter objectWriter = jsonWriter.getObjectWriter(fieldInfo.getFieldType(), fieldInfo.getFieldClass());
            if (objectWriter instanceof WithFormat) {
                objectWriter = (ObjectWriter) ((WithFormat) objectWriter).withDatePattern(fieldInfo.getFormat());
            }
            return objectWriter;
        });
    }

    @Override
    public void writeList(JSONWriter jsonWriter, boolean writeFieldName, List list) {
        if (writeFieldName) {
            writeFieldName(jsonWriter);
        }

        ObjectWriter writer = getWriter(jsonWriter);
        writer.write(jsonWriter, list);
    }

    @Override
    public String toString() {
        return fieldInfo.getName();
    }
}
