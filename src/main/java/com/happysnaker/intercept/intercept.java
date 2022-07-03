package com.happysnaker.intercept;


import java.lang.annotation.*;

/**
 * 标记该类成为以一个拦截器
 * @author happysnakers
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface intercept {

}