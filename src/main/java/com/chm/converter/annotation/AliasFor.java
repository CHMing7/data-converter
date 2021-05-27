package com.chm.converter.annotation;

import java.lang.annotation.*;

/**
 * 注解属性别名
 * <p>为其他注解中定义的属性定义别名，当该属性为空值时将引用其别名对应的属性值</p>
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-01-04
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AliasFor {
    String value();
}