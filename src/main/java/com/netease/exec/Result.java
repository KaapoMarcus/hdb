package com.netease.exec;

import java.io.Serializable;


public class Result implements Serializable {
	private static final long serialVersionUID = 4745580752713822534L;
	
	
	protected transient Command commandObject;
	
	protected String command;
	
	protected Object data;
	
	protected long executeTime;

	public Result(Command commandObject, String command, Object data, long executeTime) {
		this.commandObject = commandObject;
		this.command = command;
		this.data = data;
		this.executeTime = executeTime;
	}

	public Command getCommandObject() {
		return commandObject;
	}

	public String getCommand() {
		return command;
	}

	public Object getData() {
		return data;
	}

	public long getExecuteTime() {
		return executeTime;
	}
}
