package com.gcong.event;

public interface EventListener {
	
	public int[] getEventTypes();
	
	public void handleEvent(Event event);
}
