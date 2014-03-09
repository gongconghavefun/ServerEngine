package com.gcong.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.gcong.annotation.OP;
import com.gcong.annotation.OPEvent;
import com.gcong.annotation.OPHandler;
import com.gcong.event.EventListener;
import com.gcong.io.http.HSession;
import com.gcong.io.packet.Packet;
import com.gcong.io.packethandler.HttpHandler;
import com.gcong.io.packethandler.HttpHandlerDispatch;
import com.gcong.io.packethandler.HttpHandlerDispatchManager;
import com.gcong.service.Service;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class DefaultAppContext implements AppContext {
	
	
	private static Map<Class, Object> services;
	public boolean isFake;
	
	
	public DefaultAppContext() {
		services = new LinkedHashMap<Class, Object>();
		//j2ee下需要设置
		ClassPool.getDefault().insertClassPath(new ClassClassPath(Service.class));
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz) {
		return (T)services.get(clazz);
				
	}
	
	@SuppressWarnings("unchecked")
	public <X, Y extends X> void create(Class<Y> clazz, Class<X> inter)
			throws Exception {
		List<Short> opcodes = new ArrayList<Short>();
		OPHandler opHandler = clazz.getAnnotation(OPHandler.class);
		if(null != opHandler) {
			//如果service带有注解，利用javassist工具，让service类动态实现HttpHandler接口，并实现接口的handle方法
			if(opHandler.TYPE() == OPHandler.HTTP) {
				clazz = this.generateHttpHandlerClass(clazz, opcodes);
			} else if(opHandler.TYPE() == OPHandler.EVENT) {
				clazz = this.generateEventListenerClass(clazz, OPHandler.EVENT);
			} else if(opHandler.TYPE() == OPHandler.HTTP_EVENT) {
				clazz = this.generateHttpHandlerClass(clazz, opcodes);
				clazz = this.generateEventListenerClass(clazz, OPHandler.HTTP_EVENT);
			}
		}
		
		Object o = clazz.newInstance();
		if(o instanceof Service) {
			//启动service
			((Service)o).startup();
		}
		//将service添加到上下文对象中进行管理
		add(o, inter);
		//注册事件监听器
		//......
		if(opcodes.size() > 0) {//如果opcodes不为空，将opcodes注册到包处理分发器中
			for(int i = 0; i < opcodes.size(); i++) {
				short opcode = opcodes.get(i);
				if(opHandler.TYPE() == OPHandler.HTTP || opHandler.TYPE() == OPHandler.HTTP_EVENT) {
					HttpHandlerDispatchManager.register(HttpHandlerDispatch.PLAYER, opcode, (HttpHandler)o);
				}
			}
//			if(opHandler.TYPE() == OPHandler.HTTP || opHandler.TYPE() == OPHandler.HTTP_EVENT) {
//				HttpHandlerDispatchManager.register(HttpHandlerDispatch.PLAYER, opcodes.toArray(), (HttpHandler)o);
//			}
			
		}
	}
	
	public <T> void add(Object service, Class<T> inter) {
		if(service.getClass() != inter && 
				inter.isAssignableFrom(service.getClass())) {//接口和实现类必须相等或者是继承关系
			throw new IllegalArgumentException();
		}
		services.put(inter, service);
	}
	/**
	 * 添加HTTPHandler增强
	 */
	@SuppressWarnings("rawtypes")
	private Class generateHttpHandlerClass(Class clazz, List<Short> opcodes) throws Exception{
		Map<Short, String> opMethods = new TreeMap<Short, String>();
		Method[] methods = clazz.getDeclaredMethods();
		//遍历所有service方法，将其中有OP注解的处理方法放到opMethods中
		for(Method method : methods) {
			OP op = method.getAnnotation(OP.class);
			if(null != op) {
				Class[] params = method.getParameterTypes();
				//校验有OP注解的方法参数是否匹配，不匹配抛出异常
				if(params.length != 2) {
					throw new IllegalStateException("Method " + method.getName() + " Parameter number Error!!");
				}
				if(params[0] != Packet.class || params[1] != HSession.class) {
					throw new IllegalStateException("Method " + method.getName() + " Parameter type Error!!");
				}
				
				opMethods.put(op.code(), method.getName());
			}
		}
		
		if(opMethods.size() != 0) {
			ClassPool pool = ClassPool.getDefault();
			CtClass oldClass = pool.get(clazz.getName());
			
			CtClass proxyClass = pool.makeClass(oldClass.getName()+"$Proxy", oldClass);//动态生成代理类，继承原来的service类，并为新类添加接口及实现接口方法
			CtClass httpHandlerInterface = pool.get(HttpHandler.class.getName());//代理类需要实现的接口
			proxyClass.addInterface(httpHandlerInterface);
			
			//手写一个handle方法
			StringBuilder sb = 
					new StringBuilder("public void handle(com.gcong.io.packet.Packet packet, com.gcong.io.http.HSession session) throws Exception {");
			sb.append("short opcode = $1.getopcode();");
			sb.append("switch (opcode) {");
			Iterator<Map.Entry<Short, String>> it = opMethods.entrySet().iterator();
			Map.Entry<Short, String> entry = null;
			while(it.hasNext()) {
				entry = it.next();
				if(null != entry) {
					sb.append("case").append(entry.getKey()).append(":");
					sb.append(entry.getValue()).append("($$);");//由于代理类继承自原来的service，要让代理类能调用到处理函数，要求处理函数必须对代理类可见
					sb.append("break;");
					opcodes.add(entry.getKey());
				}
			}
			sb.append("}");
			sb.append("}");
			
			CtMethod method_handle = CtMethod.make(sb.toString(), proxyClass);
			proxyClass.addMethod(method_handle);
			return proxyClass.toClass();
		} else {
			return clazz;
		}
	}
	/**
	 * 添加EventListener增强
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Class generateEventListenerClass(Class clazz, int OpType) throws Exception{
		
		Map<Integer, String> eventMethods = new TreeMap<Integer, String>();
		Class targetClazz = null;
		if(OpType == OPHandler.EVENT) {
			targetClazz = clazz;
		} else if(OpType == OPHandler.HTTP_EVENT) {
			targetClazz = (Class)clazz.getGenericSuperclass();
		}
		//提取class中的事件处理方法
		Method[] methods = targetClazz.getDeclaredMethods();
		for(Method method : methods) {
			OPEvent event = method.getAnnotation(OPEvent.class);
			if(event != null) {
				int eventCode = event.eventCode();
				eventMethods.put(eventCode, method.getName());
			}
		}
		
		if(eventMethods.size() != 0) {
			ClassPool pool = ClassPool.getDefault();
			CtClass oldClass = pool.get(clazz.getName());
			CtClass eventListener = pool.makeClass(
					oldClass.getName()+"$ProxyEvent", oldClass);
			CtClass eventInterface = pool.get(EventListener.class.getName());
			//实现接口
			eventListener.addInterface(eventInterface);
			//实现接口方法
			StringBuilder sb1 = new StringBuilder("public int[] getEventTypes() {");
			sb1.append("return new int[]{");
			int i = 0;
			for(int key : eventMethods.keySet()) {
				i++;
				if(eventMethods.keySet().size() == i) {
					sb1.append(key);
				} else {
					sb1.append(key+",");
				}
			}
			sb1.append("};}");
			
			StringBuilder sb2 = new StringBuilder(
					"public void handleEvent(com.gcong.event.Event event) {");
			sb2.append("switch(event.type) {");
			Iterator<Map.Entry<Integer, String>> it = eventMethods.entrySet().iterator();
			Map.Entry<Integer, String> entry = null;
			while(it.hasNext()) {
				entry = it.next();
				if(entry != null) {
					sb2.append("case ").append(entry.getKey()).append(":");
					sb2.append(entry.getValue()).append("(event);");
					sb2.append("break;");
				}
			}
			sb2.append("}");
			sb2.append("}");
			
			CtMethod method1 = CtMethod.make(sb1.toString(), eventListener);
			eventListener.addMethod(method1);
			CtMethod method2 = CtMethod.make(sb2.toString(), eventListener);
			eventListener.addMethod(method2);
			return eventListener.toClass();
		} else {
			return clazz;
		}
	}
	
	public void shutdown() {
		Object[] ss = new Object[services.size()];
		services.values().toArray(ss);
		for(int i = 0; i < ss.length; i++) {
			if(ss[i] instanceof Service) {
				try {
					((Service)ss[i]).shutdown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	}

}
