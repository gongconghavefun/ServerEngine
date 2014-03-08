package com.gcong.io.packethandler;

import java.util.HashMap;
import java.util.Map;

import com.gcong.io.http.HSession;
import com.gcong.io.packet.Packet;

/**
 * http包分发管理器
 * @author gongcong
 *
 */
public class HttpHandlerDispatch {
	public static final String PLAYER = "player_dispatch";
	public static final String ADMIN = "admin_dispatch";
	
	private Map<Integer, HttpHandler> handlers = new HashMap<Integer, HttpHandler>();
	private String id;
	public HttpHandlerDispatch(String id) {
		this.id = id;
	}
	public String getId() {
		return this.id;
	}
	public void register(int opcode, HttpHandler handler) {
		handlers.put(opcode, handler);
	}
	public void register(int[] opcodes, HttpHandler handler) {
		for(int opcode : opcodes) {
			register(opcode, handler);
		}
	}
	
	public void unRegister(int opcode) {
		handlers.remove(opcode);
	}
	
	public void unRegister(int[] opcodes) {
		for(int opcode : opcodes) {
			unRegister(opcode);
		}
	}
	
	public void handle(Packet packet, HSession session) throws Exception {
		int opcode = packet.getopcode();
		HttpHandler handler = handlers.get(opcode);
		if(null != handler) {
			handler.handle(packet, session);
		} else {
			
		}
	}
}
