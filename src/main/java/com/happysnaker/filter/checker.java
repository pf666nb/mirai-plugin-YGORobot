package com.happysnaker.filter;


import java.lang.annotation.*;

/**
 * 标记该类成为以一个过滤器
 * @author happysnakers
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface checker {

}