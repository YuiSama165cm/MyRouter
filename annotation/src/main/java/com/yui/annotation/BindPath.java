package com.yui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) //声明注解的作用域
@Retention(RetentionPolicy.CLASS) //声明注解的生命周期  存在时间
public @interface BindPath {
    String value();//这儿携带一个参数
}
