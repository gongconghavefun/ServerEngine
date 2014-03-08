package com.gcong.io.packethandler;

import com.gcong.io.http.HSession;
import com.gcong.io.packet.Packet;

public interface HttpHandler {
	public void handle(Packet packet, HSession session) throws Exception;
}
