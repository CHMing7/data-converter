package com.chm.converter.fst.serializers;

import com.chm.converter.codec.DefaultDateCodec;
import com.chm.converter.core.Converter;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-30
 **/
public class DefaultDateSerializer<T extends Date> extends FstSerializer {

    private final DefaultDateCodec<T> defaultDateCodec;

    public DefaultDateSerializer() {
        this((DateTimeFormatter) null, null);
    }

    public DefaultDateSerializer(String datePattern) {
        this(datePattern, null);
    }

    public DefaultDateSerializer(DateTimeFormatter dateFormat) {
        this(dateFormat, null);
    }

    public DefaultDateSerializer(Converter<?> converter) {
        this((DateTimeFormatter) null, converter);
    }

    public DefaultDateSerializer(String datePattern, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec(Date.class, datePattern, converter);
    }

    public DefaultDateSerializer(DateTimeFormatter dateFormat, Converter<?> converter) {
        this.defaultDateCodec = new DefaultDateCodec(Date.class, dateFormat, converter);
    }

    public DefaultDateSerializer<T> withDatePattern(String datePattern) {
        return new DefaultDateSerializer<>(datePattern, this.defaultDateCodec.getConverter());
    }

    public DefaultDateSerializer<T> withDateFormat(DateTimeFormatter dateFormatter) {
        return new DefaultDateSerializer<>(dateFormatter, this.defaultDateCodec.getConverter());
    }

    @Override
    public void writeObject(FSTObjectOutput out, Object toWrite, FSTClazzInfo clzInfo, FSTClazzInfo.FSTFieldInfo referencedBy, int streamPosition) throws IOException {
        out.writeStringUTF(this.defaultDateCodec.encode((T) toWrite));
    }

    @Override
    public Object instantiate(Class objectClass, FSTObjectInput in, FSTClazzInfo serializationInfo, FSTClazzInfo.FSTFieldInfo referencee, int streamPosition) throws Exception {
        String s = in.readStringUTF();
        in.registerObject(s, streamPosition, serializationInfo, referencee);
        return this.defaultDateCodec.decode(s);
    }
}
