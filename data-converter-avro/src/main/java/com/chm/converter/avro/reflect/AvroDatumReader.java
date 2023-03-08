package com.chm.converter.avro.reflect;

import com.chm.converter.avro.factorys.CoreCodecConversion;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codecs.RuntimeTypeCodec;
import com.chm.converter.core.utils.ListUtil;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.io.ResolvingDecoder;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;

import java.io.IOException;
import java.util.List;

/**
 * @author CHMing
 * @date 2023-03-06
 **/
public class AvroDatumReader<D> extends ReflectDatumReader<D> {

    public AvroDatumReader(Schema writer, Schema reader, ReflectData data) {
        super(writer, reader, data);
    }

    @Override
    protected Object readWithoutConversion(Object old, Schema expected, ResolvingDecoder in) throws IOException {
        return super.readWithoutConversion(old, expected, in);
    }

    @Override
    protected Object convert(Object datum, Schema schema, LogicalType type, Conversion<?> conversion) {
        if (conversion instanceof CoreCodecConversion &&
                ((CoreCodecConversion<?>) conversion).getCodec() instanceof RuntimeTypeCodec) {
            Codec runtimeCodec = ((RuntimeTypeCodec<?, ?>) ((CoreCodecConversion<?>) conversion).getCodec()).getRuntimeCodec(datum);
            return runtimeCodec.decode(datum);
        }
        return super.convert(datum, schema, type, conversion);
    }
}
