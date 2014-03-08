package com.gcong.context;
/**
 * 业务逻辑上下文，各个service的容器，可以创建，关闭，管理各种逻辑
 * @author gongcong
 *
 */
public interface AppContext {
	public <T> T get(Class<T> clazz);
	public <X, Y extends X> void create(Class<Y> clazz, Class<X> inter) throws Exception;
	public <T> void add(Object service, Class<T> inter);
	public void shutdown();
}