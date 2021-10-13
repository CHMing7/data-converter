package com.chm.converter.core.annotation;

import java.lang.annotation.*;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-10-13
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface FieldPropertys {

    FieldProperty[] value();
}
