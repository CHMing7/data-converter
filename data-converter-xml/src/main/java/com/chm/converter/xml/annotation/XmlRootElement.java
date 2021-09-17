package com.chm.converter.xml.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-06
 **/
@Retention(RUNTIME)
@Target({TYPE})
public @interface XmlRootElement {

    /**
     * xml根节点name
     *
     * @return
     */
    String name() default "";

    /**
     * 根节点命名空间
     *
     * @return
     */
    String namespace() default "";
}