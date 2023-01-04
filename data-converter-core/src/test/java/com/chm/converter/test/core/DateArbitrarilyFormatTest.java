package com.chm.converter.test.core;

import com.chm.converter.core.Converter;
import com.chm.converter.core.codec.Codec;
import com.chm.converter.core.codec.DataCodecGenerate;
import com.chm.converter.core.utils.DateUtil;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @author CHMing
 * @date 2022-12-08
 **/
public class DateArbitrarilyFormatTest {

    @Test
    public void testDateFormat() {
        String dateStr = "2020-10-10 12:12";
        Date parse = DateUtil.parseToDate(dateStr);
        dateStr = "2020-10-10";
        parse = DateUtil.parseToDate(dateStr);
        DataCodecGenerate dataCodecGenerate = DataCodecGenerate.getDataCodecGenerate(Converter.DEFAULT);
        Codec codec = dataCodecGenerate.get(Date.class);
        Object decode = codec.decode(dateStr);
    }
}
