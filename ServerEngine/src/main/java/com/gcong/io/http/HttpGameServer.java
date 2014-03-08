package com.gcong.io.http;

import java.io.IOException;
import java.net.HttpRetryException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gcong.io.packet.Packet;
import com.gcong.io.packethandler.HttpHandlerDispatch;
import com.gcong.io.packethandler.HttpHandlerDispatchManager;



public class HttpGameServer extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init() throws ServletException {
		
	}
	@Override
	public void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		//协议解析
		Packet packet = null;
		
		//session验证
		HSession session = null;
		
		HttpHandlerDispatch dispatch = 
				HttpHandlerDispatchManager.get(HttpHandlerDispatch.PLAYER);
		
		try{
			dispatch.handle(packet, session);
			
			
		} catch(Throwable e) {
			//异常处理
			//给客户端返回处理后的消息包
		}
		
		
		
		
	}
	
	
}
