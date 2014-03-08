package com.gcong.engine;

import com.gcong.Platform;
import com.gcong.context.DefaultAppContext;
import com.gcong.game.GameServer;

public class GameEngine implements Runnable {
	
	private static GameEngine engine = null;
	//具体游戏逻辑实现此接口
	private static GameServer server;
	
	public volatile static boolean running = false;
	
	private static String game_server_name = "XJGameServer";
	
	public static void init() throws Throwable {
		if(null == engine) {
			
			engine = new GameEngine();
			engine.baseInit();
			
			
			server = (GameServer) Class.forName(game_server_name).newInstance();
			server.startup();
			
			running = true;
			Thread mainCycle = new Thread(engine);
			mainCycle.setPriority(Thread.MAX_PRIORITY);
			mainCycle.setName("MainCycle");
			mainCycle.start();
			
		} else {
			
		}
	}
	/**
	 * 初始化平台资源及配置文件等信息
	 * @throws Throwable
	 */
	public void baseInit() throws Throwable {
		
		Platform.setAppContext(new DefaultAppContext());
		
	}
	
	public void run() {
		while(running) {
			System.out.println("MainCycle running...");
			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
