package com.caipiao.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * 功能模块权限拦截注解
 * @author	sjq
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleAuthorityRequired 
{
	/**
	 * 模块编码,不能为空
	 * @author	sjq
	 * @return	当前模块的编码
	 */
	String[] mcode();
	/**
	 * 多个mcode之间的并列关系(0表示且的关系,1表示或的关系,默认值为0(且的关系))
	 * @author	sjq
	 */
	int mflag() default 0;
	
}