package com.netease.backend.db.common.config;

import java.util.Properties;


public interface PropertiesConfigHelper<T extends PropertiesConfig> {

	
	Class<T> getTypeClass();

	
	T fromProperties(
		String fileName, Properties props, String desc);

	
	String toPropertiesString(
		T config);

	
	Properties toProperties(
		T config);

	
	void updateConfig(
		T config, T from);

	
	void updateConfig(
		T config, Properties props);
}
