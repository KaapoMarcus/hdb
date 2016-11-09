package com.netease.backend.db.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;


public class Log4jConfigurator {

	
	public static final String	LOG4J_PROPERTY	= "log4j.conf";

	
	public static final String	LOG4J_DEFAULT	= "log4j.properties";

	
	public static void configureLog4j() {
		final String cfg = System.getProperty(LOG4J_PROPERTY);
		if ((cfg != null) && (cfg.length() > 0)) {
			PropertyConfigurator.configure(cfg);
		} else {
			
			Properties prop = null;

			final InputStream in;
			if ((in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("classpath:" + LOG4J_DEFAULT)) != null) {
				prop = new Properties();
				try {
					prop.load(in);
				} catch (final IOException ex) {
				} finally {
					try {
						in.close();
					} catch (final IOException ex) {
					}
				}
			}

			if (prop == null)
				throw new Log4jConfigError("Log4j configurations not found");
			else {
				PropertyConfigurator.configure(prop);
			}
		}
	}

}
