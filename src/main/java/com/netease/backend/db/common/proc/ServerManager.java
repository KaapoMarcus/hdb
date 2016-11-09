package com.netease.backend.db.common.proc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import com.netease.backend.db.common.Disposable;


public class ServerManager<T extends ServerProcBase> implements Disposable {

	
	private final Class<T>	managedServerType;

	
	public ServerManager(
		Class<T> managedServerType)
	{
		if (managedServerType == null)
			throw new NullPointerException(managedServerType
					+ " type should not be null!");
		final Method mod = getGetInstanceMethod(managedServerType);
		final Method mod2 = getSetInstanceMethod(managedServerType);
		if ((mod == null) || (mod2 == null))
			throw new IllegalArgumentException(managedServerType.getName()
					+ " is not in singleton style!");
		final Constructor<?> c = getConstructor(managedServerType);
		if (c == null)
			throw new IllegalArgumentException(managedServerType.getName()
					+ " has no empty constructor or not accessable!");
		this.managedServerType = managedServerType;
	}

	@SuppressWarnings("unchecked")
	private static Constructor getConstructor(
		Class managedServerType)
	{
		Constructor c = null;
		try {
			c = managedServerType.getDeclaredConstructor();
			
			if (Modifier.isPrivate(c.getModifiers())) {
				c = null;
			}
		} catch (final NoSuchMethodException ex) {
		}
		return c;
	}

	@SuppressWarnings("unchecked")
	private Constructor<T> getConstructor() {
		return getConstructor(this.getManagedServerType());
	}

	private static Method getGetInstanceMethod(
		Class<?> managedServerType)
	{
		Method mod = null;
		try {
			mod = managedServerType.getMethod("getInstance");
			if (!Modifier.isStatic(mod.getModifiers())) {
				
				mod = null;
			}
		} catch (final NoSuchMethodException ex) {
		}
		return mod;
	}

	
	private Method getGetInstanceMethod() {
		return getGetInstanceMethod(this.getManagedServerType());
	}

	private static Method getSetInstanceMethod(
		Class<?> managedServerType)
	{
		Method mod = null;
		try {
			final Class<?>[] c = { managedServerType };
			mod = managedServerType.getDeclaredMethod("setInstance", c);
			if (!Modifier.isStatic(mod.getModifiers())
					|| Modifier.isPrivate(mod.getModifiers())
					|| (mod.getExceptionTypes().length > 0)) {
				
				mod.setAccessible(true);
				mod = null;
			}
		} catch (final NoSuchMethodException ex) {
		}
		return mod;
	}

	private Method getSetInstanceMethod() {
		return getSetInstanceMethod(this.getManagedServerType());
	}

	
	private Class<T> getManagedServerType() {
		return managedServerType;
	}

	
	private String getManagedServerTypeName() {
		return this.getManagedServerType().getName();
	}

	
	@SuppressWarnings("unchecked")
	private T getInstance() {
		final T instance = null;
		try {
			final Method mod;
			if ((mod = this.getGetInstanceMethod()) != null)
				return (T) mod.invoke(null);
		} catch (final InvocationTargetException ex) {
			throw new RuntimeException(ex.getCause()); 
		} catch (final IllegalArgumentException ex) {
			ex.printStackTrace(); 
		} catch (final IllegalAccessException ex) {
			ex.printStackTrace(); 
		}
		return instance;
	}

	
	public T getServer() {
		
		final T server = this.getInstance();
		if (server == null)
			throw new IllegalStateException(this.getManagedServerTypeName()
					+ "instance is null!");
		
		return server;
	}

	
	public boolean isServerActive() {
		return this.getServer().isRunning();
	}

	
	public void restartServer() {
		for (;;) {
			final T server = this.getServer();
			if ((server != null) && server.isRunning()) {
				
				try {
					stopServer(server, true);
				} catch (final InterruptedException never) {
				}
			}
			
			
			final T newServer = this.createServer();
			
			if (this.compareAndSetServerInstance(server, newServer)) {
				
				startServer(newServer);
				break;
			}
		}
	}

	
	private T createServer() {
		final T server = null;
		try {
			final Constructor<T> c = this.getConstructor();
			if (!c.isAccessible()) {
				c.setAccessible(true);
			}
			try {
				return c.newInstance();
			} finally {
				if (!c.isAccessible()) {
					c.setAccessible(false);
				}
			}
		} catch (final InstantiationException ex) {
			ex.printStackTrace(); 
		} catch (final IllegalAccessException ex) {
			ex.printStackTrace(); 
		} catch (final IllegalArgumentException ex) {
			ex.printStackTrace(); 
		} catch (final InvocationTargetException ex) {
			throw new RuntimeException(ex.getCause()); 
		}
		
		return server;
	}

	
	public void startServer() {
		startServer(this.getServer());
	}

	
	private static void startServer(
		ServerProcBase server)
	{
		
		if (!server.isRunning()) {
			
			server.startup();
		}
	}

	
	public void stopServer(
		boolean join)
		throws InterruptedException
	{
		final T instance = this.getInstance();
		if (instance != null) {
			stopServer(instance, join);
		}
	}

	
	private static void stopServer(
		ServerProcBase server, boolean join)
		throws InterruptedException
	{
		
		if (!server.isRunning())
			return;
		
		server.shutdown();
		
		if (join) {
			
			server.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
			
			
		}
	}

	
	public T getOrCreateServer() {
		for (;;) {
			
			final T server = this.getInstance();
			if (server != null)
				return server;
			
			final T newServer = this.createServer();
			
			this.compareAndSetServerInstance(server, newServer);
		}
	}

	
	public boolean compareAndSetServerInstance(
		T instance, T newInstance)
	{
		
		final T oldInstance = this.getInstance();
		if (oldInstance != instance)
			return false;
		
		if ((oldInstance != null) && oldInstance.isRunning())
			throw new IllegalStateException(this.getManagedServerTypeName()
					+ " is still active, can NOT be replaced!");
		
		synchronized (this.getManagedServerType()) {
			
			if (this.getInstance() != oldInstance)
				return false;
			
			try {
				final Method mod = this.getSetInstanceMethod();
				if (!mod.isAccessible()) {
					mod.setAccessible(true);
				}
				try {
					mod.invoke(null, newInstance);
				} finally {
					if (!mod.isAccessible()) {
						mod.setAccessible(false);
					}
				}
			} catch (final InvocationTargetException ex) {
				throw new RuntimeException(ex.getCause()); 
			} catch (final IllegalArgumentException ex) {
				ex.printStackTrace(); 
			} catch (final IllegalAccessException ex) {
				ex.printStackTrace(); 
			}
		}
		
		return true;
	}

	public void dispose() {
		try {
			this.stopServer(true);
		} catch (InterruptedException ex) {
		}

	}
}
