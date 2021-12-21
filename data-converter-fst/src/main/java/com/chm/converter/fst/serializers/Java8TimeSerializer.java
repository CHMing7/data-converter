package com.chm.converter.fst.serializers;

import com.chm.converter.codec.Java8TimeCodec;
import com.chm.converter.core.Converter;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-30
 **/
public class Java8TimeSerializer<T extends TemporalAccessor> extends FstSerializer  {

    private final Java8TimeCodec<T> java8TimeCodec;

    public Java8TimeSerializer(Class<T> clazz) {
        this(clazz, (DateTimeFormatter) null, null);
    }

    public Java8TimeSerializer(Class<T> clazz, String datePattern) {
        this(clazz, datePattern, null);
    }

    public Java8TimeSerializer(Class<T> clazz, DateTimeFormatter dateFormatter) {
        this(clazz, dateFormatter, null);
    }

    public Java8TimeSerializer(Class<T> clazz, Converter<?> converter) {
        this(clazz, (DateTimeFormatter) null, converter);
    }

    public Java8TimeSerializer(Class<T> clazz, String datePattern, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, datePattern, converter);
    }

    public Java8TimeSerializer(Class<T> clazz, DateTimeFormatter dateFormatter, Converter<?> converter) {
        this.java8TimeCodec = new Java8TimeCodec<>(clazz, dateFormatter, converter);
    }

    public Java8TimeSerializer<T> withClass(Class<T> clazz) {
        return new Java8TimeSerializer<>(clazz, this.java8TimeCodec.getDateFormatter(), this.java8TimeCodec.getConverter());
    }

    public Java8TimeSerializer<T> withDatePattern(String datePattern) {
        return new Java8TimeSerializer<>(this.java8TimeCodec.getClazz(), datePattern, this.java8TimeCodec.getConverter());
    }

    public Java8TimeSerializer<T> withDateFormatter(DateTimeFormatter dateFormatter) {
        return new Java8TimeSerializer<>(this.java8TimeCodec.getClazz(), dateFormatter, this.java8TimeCodec.getConverter());
    }

    @Override
    public void writeObject(FSTObjectOutput out, Object toWrite, FSTClazzInfo clzInfo, FSTClazzInfo.FSTFieldInfo referencedBy, int streamPosition) throws IOException {
        out.writeStringUTF(this.java8TimeCodec.encode((T) toWrite));
    }

    @Override
    public Object instantiate(Class objectClass, FSTObjectInput in, FSTClazzInfo serializationInfo, FSTClazzInfo.FSTFieldInfo referencee, int streamPosition) throws Exception {
        String s = in.readStringUTF();
        in.registerObject(s, streamPosition, serializationInfo, referencee);
        return this.java8TimeCodec.decode(s);
    }
}
