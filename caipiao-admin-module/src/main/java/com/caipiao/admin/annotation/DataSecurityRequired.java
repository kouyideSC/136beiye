package com.caipiao.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.caipiao.admin.enums.DataSecurityEnums;

import java.lang.annotation.Retention;

/**
 * 数据访问拦截注解
 * @author	sjq
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSecurityRequired 
{
	/**
	 * 数据访问类型(枚举类型,至允许枚举中出现的值)
	 */
	DataSecurityEnums dtype() default DataSecurityEnums.general;
}