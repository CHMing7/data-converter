package com.chm.converter.hessian.factory;


import com.caucho.hessian.io.*;
import com.chm.converter.core.ClassInfoStorage;
import com.chm.converter.core.Converter;
import com.chm.converter.core.FieldInfo;
import com.chm.converter.core.JavaBeanInfo;
import com.chm.converter.core.utils.MapUtil;
import com.chm.converter.hessian.UseDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-10-18
 **/
public class HessianConverterFactory extends BeanSerializerFactory {

    private final Class<? extends Converter> converterClass;

    public HessianConverterFactory(Converter<?> converter) {
        this.converterClass = converter != null ? converter.getClass() : null;
    }

    @Override
    public Serializer loadSerializer(Class cl) throws HessianProtocolException {
        Serializer serializer = super.loadSerializer(cl);
        if (serializer instanceof BeanSerializer) {
            return new HessianBeanSerializer(cl, getClassLoader(), converterClass, this);
        }
        return serializer;
    }

    @Override
    public Deserializer loadDeserializer(Class cl) throws HessianProtocolException {
        Deserializer deserializer = super.loadDeserializer(cl);
        if (deserializer instanceof BeanDeserializer) {
            return new HessianBeanDeserializer(cl, converterClass, this);
        }
        return deserializer;
    }

    public static class HessianBeanSerializer<T> extends BeanSerializer {

        private static final Logger log = LoggerFactory.getLogger(HessianBeanSerializer.class);

        private final JavaBeanInfo<T> javaBeanInfo;

        private final Map<FieldInfo, Serializer> fieldInfoSerializerMap;

        private final SerializerFactory serializerFactory;

        private Object _writeReplaceFactory;

        private Method _writeReplace;

        public HessianBeanSerializer(Class<T> cl, ClassLoader loader, Class<? extends Converter> converterClass, SerializerFactory serializerFactory) {
            super(cl, loader);
            introspectWriteReplace(cl, loader);
            this.javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(cl, converterClass);
            this.fieldInfoSerializerMap = new ConcurrentHashMap<>();
            this.serializerFactory = serializerFactory;
        }

        private void introspectWriteReplace(Class cl, ClassLoader loader) {
            try {
                String className = cl.getName() + "HessianSerializer";

                Class serializerClass = Class.forName(className, false, loader);

                Object serializerObject = serializerClass.newInstance();

                Method writeReplace = getWriteReplace(serializerClass, cl);

                if (writeReplace != null) {
                    _writeReplaceFactory = serializerObject;
                    _writeReplace = writeReplace;

                    return;
                }
            } catch (ClassNotFoundException ignored) {
            } catch (Exception e) {
                log.error(e.toString(), e);
            }

            _writeReplace = getWriteReplace(cl);
        }

        @Override
        public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
            if (out.addRef(obj)) {
                return;
            }

            try {
                if (_writeReplace != null) {
                    Object repl;

                    if (_writeReplaceFactory != null) {
                        repl = _writeReplace.invoke(_writeReplaceFactory, obj);
                    } else {
                        repl = _writeReplace.invoke(obj);
                    }
                    // out.removeRef(obj);
                    out.writeObject(repl);
                    out.replaceRef(repl, obj);
                    return;
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
            }

            int ref = out.writeObjectBegin(javaBeanInfo.getClazz().getName());
            List<FieldInfo> fieldList = javaBeanInfo.getSortedFieldList();

            if (ref < -1) {
                // Hessian 1.1 uses a map

                for (FieldInfo fieldInfo : fieldList) {
                    out.writeString(fieldInfo.getName());
                    if (!fieldInfo.isSerialize()) {
                        out.writeNull();
                        continue;
                    }
                    Object value = fieldInfo.get(obj);
                    Serializer fieldSerializer = getFieldSerializer(fieldInfo);
                    writeFieldObject(out, fieldSerializer, value);
                }
                out.writeMapEnd();
            } else {
                if (ref == -1) {
                    out.writeClassFieldLength(fieldList.size());
                    for (FieldInfo fieldInfo : fieldList) {
                        out.writeString(fieldInfo.getName());
                    }
                    out.writeObjectBegin(javaBeanInfo.getClazz().getName());
                }

                for (FieldInfo fieldInfo : fieldList) {
                    if (!fieldInfo.isSerialize()) {
                        out.writeNull();
                        continue;
                    }
                    Object value = fieldInfo.get(obj);
                    Serializer fieldSerializer = getFieldSerializer(fieldInfo);
                    writeFieldObject(out, fieldSerializer, value);
                }
            }
        }

        private void writeFieldObject(AbstractHessianOutput out, Serializer fieldSerializer, Object fieldValue) throws IOException {
            if (fieldValue == null) {
                out.writeNull();
                return;
            }

            fieldSerializer.writeObject(fieldValue, out);
        }

        private Serializer getFieldSerializer(FieldInfo fieldInfo) {
            return MapUtil.computeIfAbsent(fieldInfoSerializerMap, fieldInfo, info -> {
                try {
                    Serializer serializer = serializerFactory.getSerializer(info.getFieldClass());
                    if (serializer instanceof Java8TimeConverterFactory.Java8TimeConverter) {
                        return ((Java8TimeConverterFactory.Java8TimeConverter<?>) serializer).withDatePattern(fieldInfo.getFormat());
                    }
                    if (serializer instanceof DefaultDateConverterFactory.DefaultDateConverter) {
                        return ((DefaultDateConverterFactory.DefaultDateConverter<?>) serializer).withDatePattern(fieldInfo.getFormat());
                    }
                    return serializer;
                } catch (HessianProtocolException e) {
                    return null;
                }
            });
        }
    }


    public static class HessianBeanDeserializer<T> extends BeanDeserializer {

        private final JavaBeanInfo<T> javaBeanInfo;

        private final Map<FieldInfo, Deserializer> fieldInfoDeserializerMap;

        private final SerializerFactory serializerFactory;

        private Method _readResolve;

        private Object[] fields;

        public HessianBeanDeserializer(Class<T> cl, Class<? extends Converter> converterClass, SerializerFactory serializerFactory) {
            super(cl);
            _readResolve = getReadResolve(cl);
            this.javaBeanInfo = ClassInfoStorage.INSTANCE.getJavaBeanInfo(cl, converterClass);
            this.fieldInfoDeserializerMap = new ConcurrentHashMap<>();
            this.serializerFactory = serializerFactory;
        }

        @Override
        public Object readObject(AbstractHessianInput in) throws IOException {
            try {
                return readObject(in, this.fields);
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new IOExceptionWrapper(e);
            }
        }

        @Override
        public Object readMap(AbstractHessianInput in) throws IOException {
            try {
                Object obj = javaBeanInfo.getObjectConstructor().construct();

                return readMap(in, obj);
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new IOExceptionWrapper(e);
            }
        }

        @Override
        public Object readMap(AbstractHessianInput in, Object obj) throws IOException {
            try {
                int ref = in.addRef(obj);
                Map<String, FieldInfo> nameFieldInfoMap = javaBeanInfo.getNameFieldInfoMap();
                while (!in.isEnd()) {
                    Object key = in.readObject();
                    FieldInfo fieldInfo = nameFieldInfoMap.get(key);
                    if (!fieldInfo.isDeserialize()) {
                        continue;
                    }
                    Object value = in.readObject(fieldInfo.getFieldClass());
                    fieldInfo.set(obj, value);
                }

                in.readMapEnd();

                Object resolve = resolve(obj);

                if (obj != resolve) {
                    in.setRef(ref, resolve);
                }

                return resolve;
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new IOExceptionWrapper(e);
            }
        }

        private Object resolve(Object obj) {
            // if there's a readResolve method, call it
            try {
                if (_readResolve != null) {
                    return _readResolve.invoke(obj, new Object[0]);
                }
            } catch (Exception ignored) {
            }

            return obj;
        }

        @Override
        public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
            if (this.fields == null) {
                this.fields = fields;
            }

            String[] fieldNames = (String[]) fields;

            Object obj = javaBeanInfo.getObjectConstructor().construct();
            Map<String, FieldInfo> nameFieldInfoMap = javaBeanInfo.getNameFieldInfoMap();

            int ref = in.addRef(obj);

            for (String fieldName : fieldNames) {
                FieldInfo fieldInfo = nameFieldInfoMap.get(fieldName);
                if (!fieldInfo.isDeserialize()) {
                    continue;
                }
                Deserializer fieldDeserializer = getFieldDeserializer(fieldInfo);
                Object value = readeField(in, fieldDeserializer);
                fieldInfo.set(obj, value);
            }

            Object resolve = resolve(obj);

            if (obj != resolve) {
                in.setRef(ref, resolve);
            }

            return resolve;
        }

        private Object readeField(AbstractHessianInput in, Deserializer fieldDeserializer) throws IOException {
            if (fieldDeserializer == null) {
                return in.readObject();
            }

            if (fieldDeserializer instanceof UseDeserializer) {
                return fieldDeserializer.readObject(in);
            }

            return in.readObject();
        }

        private Deserializer getFieldDeserializer(FieldInfo fieldInfo) {
            return MapUtil.computeIfAbsent(fieldInfoDeserializerMap, fieldInfo, info -> {
                try {
                    Deserializer deserializer = serializerFactory.getDeserializer(info.getFieldClass());
                    if (deserializer instanceof Java8TimeConverterFactory.Java8TimeConverter) {
                        return ((Java8TimeConverterFactory.Java8TimeConverter<?>) deserializer).withDatePattern(fieldInfo.getFormat());
                    }
                    if (deserializer instanceof DefaultDateConverterFactory.DefaultDateConverter) {
                        return ((DefaultDateConverterFactory.DefaultDateConverter<?>) deserializer).withDatePattern(fieldInfo.getFormat());
                    }
                    return deserializer;
                } catch (HessianProtocolException e) {
                    return null;
                }
            });
        }
    }

}
