package com.chm.converter.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-08
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface XmlProperty {

    boolean isCData() default false;

    boolean isAttribute() default false;

    boolean isText() default false;

    String namespace() default "";
}
