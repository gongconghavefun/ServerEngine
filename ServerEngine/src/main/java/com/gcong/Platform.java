package com.gcong;

import com.gcong.context.AppContext;

/**
 * 平台类 负责游戏服务器各资源协调与调度
 * @author gongcong
 *
 */
public class Platform {
	
	static AppContext appContext;

	public static AppContext getAppContext() {
		return appContext;
	}

	public static void setAppContext(AppContext appContext) {
		Platform.appContext = appContext;
	}
	
	
	public static void shutdown() {
		appContext.shutdown();
		
		
	}
}
