package com.netease.exec;

import java.io.IOException;

import com.netease.cli.ConsoleHelper;


public class ConsoleInput implements InputDevice {

	public Object askForChoice(String question, Integer defaultValue, String[] choice, Object[] values)
			throws IOException {
		return ConsoleHelper.askForChoice(question, defaultValue, choice, values);
	}

	public int askForOneInteger(String question, Integer defaultValue) throws IOException {
		return ConsoleHelper.askForOneInteger(question, defaultValue);
	}

	public String askForOneString(String question, String defaultValue) throws IOException {
		return ConsoleHelper.askForOneString(question, defaultValue);
	}

	public boolean askForYesNo(String question, boolean defaultValue) throws IOException {
		return ConsoleHelper.askForYesNo(question, defaultValue);
	}

}
