/*
 * Copyright (C) 2011-2021 ShenZhen iBOXCHAIN Information Technology Co.,Ltd.
 *
 * All right reserved.
 *
 * This software is the confidential and proprietary
 * information of iBOXCHAIN Company of China.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with iBOXCHAIN inc.
 *
 */
package com.chm.converter.xml;

import cn.hutool.core.util.StrUtil;
import com.chm.converter.exceptions.ConvertException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 基于JAXB实现的XML转换器
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
public class JaxbConverter implements XmlConverter {

  @Override
  public String encodeToString(Object obj) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof CharSequence) {
      return obj.toString();
    }
    if (obj instanceof Map || obj instanceof List) {
      throw new RuntimeException("[Mbp] JAXB XML converter dose not support translating instance of java.util.Map or java.util.List");
    }
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
      StringWriter writer = new StringWriter();
      createMarshaller(jaxbContext, "UTF-8").marshal(obj, writer);
      return writer.toString();
    } catch (JAXBException e) {
      throw new ConvertException("xml", e);
    }

  }

  @Override
  public <T> T convertToJavaObject(String source, Class<T> targetType) {
    JAXBContext jaxbContext = null;
    try {
      jaxbContext = JAXBContext.newInstance(targetType);
      StringReader reader = new StringReader(source);
      return (T) createUnmarshaller(jaxbContext).unmarshal(reader);
    } catch (JAXBException e) {
      throw new ConvertException("xml", e);
    }

  }


  @Override
  public <T> T convertToJavaObject(String source, Type targetType) {
    return convertToJavaObject(source, (Class<? extends T>) targetType);
  }


  public Marshaller createMarshaller(JAXBContext jaxbContext, String encoding) {
    try {
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

      if (StrUtil.isNotEmpty(encoding)) {
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
      }
      return marshaller;
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

  public Unmarshaller createUnmarshaller(JAXBContext jaxbContext) {
    try {
      return jaxbContext.createUnmarshaller();
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }
}
