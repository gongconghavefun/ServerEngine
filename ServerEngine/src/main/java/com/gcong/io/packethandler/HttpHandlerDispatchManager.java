package com.gcong.io.packethandler;

import java.util.HashMap;
import java.util.Map;

public class HttpHandlerDispatchManager {
	private static Map<String, HttpHandlerDispatch> dispatchs = 
			new HashMap<String, HttpHandlerDispatch>();
	
	public static void add(HttpHandlerDispatch dispatch) {
		dispatchs.put(dispatch.getId(), dispatch);
	}
	
	public static HttpHandlerDispatch remove(String id) {
		return dispatchs.remove(id);
	}
	
	public static HttpHandlerDispatch get(String id) {
		return dispatchs.get(id);
	}
	
	public static boolean register(String id, int opcode, HttpHandler handler) {
		HttpHandlerDispatch dispatch = get(id);
		if(null == dispatch) {
			dispatch = new HttpHandlerDispatch(id);
			add(dispatch);
		}
		dispatch.register(opcode, handler);
		return true;
	}
	
	public static boolean register(String id, int[] opcodes, HttpHandler handler) {
		HttpHandlerDispatch dispatch = get(id);
		if(null == dispatch) {
			dispatch = new HttpHandlerDispatch(id);
			add(dispatch);
		}
		dispatch.register(opcodes, handler);
		return true;
	}
	
	public static void unRegister(String id, int opcode, HttpHandler handler) {
		HttpHandlerDispatch dispatch = get(id);
		if(null != dispatch) {
			dispatch.unRegister(opcode);
		}
	}
	
	public static void unRegister(String id, int[] opcodes, HttpHandler handler) {
		HttpHandlerDispatch dispatch = get(id);
		if(null != dispatch) {
			dispatch.unRegister(opcodes);
		}
	}
}
