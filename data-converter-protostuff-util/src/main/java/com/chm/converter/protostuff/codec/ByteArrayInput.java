package com.chm.converter.protostuff.codec;

import io.protostuff.FilterInput;
import io.protostuff.ProtobufException;
import io.protostuff.Schema;
import io.protostuff.UninitializedMessageException;

import java.io.IOException;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-11-29
 **/
public class ByteArrayInput extends FilterInput<io.protostuff.ByteArrayInput> {

    public ByteArrayInput(io.protostuff.ByteArrayInput input) {
        super(input);
    }

    @Override
    public <T> T mergeObject(T value, final Schema<T> schema) throws IOException {
        if (input.decodeNestedMessageAsGroup) {
            return mergeObjectEncodedAsGroup(value, schema);
        }
        final int length = input.readRawVarint32();
        if (length < 0) {
            throw negativeSize();
        }

        // save old limit
        final int oldLimit = input.currentLimit();
        input.setBounds(input.currentOffset(), input.currentOffset() + length);
        T resultValue;
        if (schema instanceof ProtostuffCodec) {
            resultValue = ((ProtostuffCodec<T>) schema).mergeFrom(this);
        } else {
            if (value == null) {
                value = schema.newMessage();
            }
            schema.mergeFrom(this, value);
            resultValue = value;
        }

        if (!schema.isInitialized(value)) {
            throw new UninitializedMessageException(value, schema);
        }
        input.checkLastTagWas(0);

        // restore old limit
        input.setBounds(input.currentOffset(), oldLimit);
        return resultValue;
    }

    private <T> T mergeObjectEncodedAsGroup(T value, final Schema<T> schema) throws IOException {
        T resultValue;
        if (schema instanceof ProtostuffCodec) {
            resultValue = ((ProtostuffCodec<T>) schema).mergeFrom(this);
        } else {
            if (value == null) {
                value = schema.newMessage();
            }
            schema.mergeFrom(this, value);
            resultValue = value;
        }
        if (!schema.isInitialized(value)) {
            throw new UninitializedMessageException(value, schema);
        }
        // handling is in #readFieldNumber
        input.checkLastTagWas(0);
        return resultValue;
    }

    static ProtobufException negativeSize() {
        return new ProtobufException(
                "CodedInput encountered an embedded string or message " +
                        "which claimed to have negative size.");
    }
}
