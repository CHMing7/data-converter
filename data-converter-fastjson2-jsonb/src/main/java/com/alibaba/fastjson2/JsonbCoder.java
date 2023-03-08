package com.alibaba.fastjson2;

/**
 * @author caihongming
 * @version v1.0
 * @date 2022-07-07
 **/
public interface JsonbCoder {

    static JSONWriter ofJsonbWriter(JSONWriter.Context writeContext) {
        return new JSONWriterJSONB(writeContext, null);
    }

    static JSONReader ofJsonbReader(JSONReader.Context readContext, byte[] jsonbBytes) {
        return new JSONReaderJSONB(readContext, jsonbBytes, 0, jsonbBytes.length, null);
    }
}
