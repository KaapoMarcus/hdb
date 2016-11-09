package com.netease.backend.db.common.config;

import java.util.Properties;

public interface PropertiesConfig {

	
	String getDescription();

	
	String toPropertiesString();

	
	Properties toProperties();

	
	PropertiesConfig update(
		Properties props);

}
