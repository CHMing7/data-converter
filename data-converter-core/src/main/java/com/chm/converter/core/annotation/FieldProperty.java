package com.chm.converter.core.annotation;

import java.lang.annotation.*;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-13
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface FieldProperty {

    /**
     * 排序
     */
    int ordinal() default 0;

    /**
     * 序列化key
     *
     * @return
     */
    String name() default "";

    /**
     * 序列化格式
     */
    String format() default "";

    /**
     * 是否序列化
     */
    boolean serialize() default true;

    /**
     * 是否反序列化
     */
    boolean deserialize() default true;
}
