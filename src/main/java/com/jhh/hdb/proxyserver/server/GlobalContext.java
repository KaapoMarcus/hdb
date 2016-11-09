package com.jhh.hdb.proxyserver.server;

import com.jhh.hdb.proxyserver.config.Config;


public class GlobalContext {
	private static GlobalContext instance = new GlobalContext();
	
	private Config config;
	
	public static GlobalContext getInstance(){
		return instance;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
	
	
}
