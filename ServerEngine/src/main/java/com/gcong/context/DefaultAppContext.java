package com.gcong.context;

public class DefaultAppContext implements AppContext {

	public <T> T get(Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	public <X, Y extends X> void create(Class<Y> clazz, Class<X> inter)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public <T> void add(Object service, Class<T> inter) {
		// TODO Auto-generated method stub

	}

	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
