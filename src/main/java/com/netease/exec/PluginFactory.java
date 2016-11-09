package com.netease.exec;


public interface PluginFactory {
	
	Plugin createPlugin(Executor executor, String[] args);
}
