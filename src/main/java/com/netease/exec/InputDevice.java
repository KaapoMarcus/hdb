package com.netease.exec;

import java.io.IOException;


public interface InputDevice {
	
	Object askForChoice(String question, Integer defaultValue, String[] choice, Object[] values) throws IOException;

	
	int askForOneInteger(String question, Integer defaultValue) throws IOException;

	
	String askForOneString(String question, String defaultValue) throws IOException;

	
	boolean askForYesNo(String question, boolean defaultValue) throws IOException;
}
