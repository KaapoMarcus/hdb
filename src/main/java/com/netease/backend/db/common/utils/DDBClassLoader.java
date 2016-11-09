package com.netease.backend.db.common.utils;

import com.netease.backend.db.common.exceptions.LoadClassException;


public class DDBClassLoader {
	private static class MyClassLoader extends ClassLoader {
		private byte[] classData = null;
		
		private MyClassLoader() {
			super();
		}

		@SuppressWarnings("unchecked")
		protected Class findClass(String name) {
			return defineClass(name, this.classData, 0, this.classData.length);
		}
		
		private Object newInstance(String name) throws InstantiationException,
				IllegalAccessException, ClassNotFoundException {
			return loadClass(name, true).newInstance();
		}
	}
	
	
	private static MyClassLoader loader = new MyClassLoader();
	
	
	public static Object newObjectInstance(String name) throws LoadClassException {
		try {
			return loader.newInstance(name);
		} catch (Throwable e) {
			throw new LoadClassException(e.getMessage());
		}
	}

	
	public static void loadClassDefinition(String name, byte[] classData)
			throws LoadClassException {
		try {
			loader.classData = classData;
			
			loader.loadClass(name);
		} catch (Throwable e) {
			throw new LoadClassException(e.getMessage());
		}
	}
}
