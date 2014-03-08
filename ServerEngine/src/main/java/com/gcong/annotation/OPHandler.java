package com.gcong.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 注解业务类
 * @author gongcong
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OPHandler {
	short TYPE();
	public static final int TCP = 0;		//TCP包处理器
	public static final int HTTP = 1;		//HTTP包处理器
	public static final int EVENT = 2;		//事件处理器
	public static final int HTTP_EVENT = 3;	//HTTP + 事件处理器
	public static final int TCP_EVENT = 4;	//TCP + 事件处理器
	
}
