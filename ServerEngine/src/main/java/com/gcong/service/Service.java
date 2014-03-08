package com.gcong.service;
/**
 * 游戏逻辑层负责实现具体游戏逻辑，可作为事件 HTTP TCP等请求的处理器
 * @author gongcong
 *
 */
public interface Service {
	String getId();
	void startup() throws Exception;
	void shutdown() throws Exception;
}
