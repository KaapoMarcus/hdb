package com.netease.exec;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

import com.netease.exec.option.Option;


public abstract class Plugin {
	
	protected String name;
	
	protected String description;
	
	protected Executor executor;
	
	public Plugin(String name, String description, Executor executor) {
		this.name = name;
		this.description = description;
		this.executor = executor;
	}

	public String getDescription() {
		return description;
	}

	public Executor getExecutor() {
		return executor;
	}

	public String getName() {
		return name;
	}

	
	public abstract Collection<Command> getCommands();
	
	
	public Collection<Option> getOptions() {
		return new Vector<Option>();
	}
	
	
	public String getPrompt() {
		return name;
	}
	
	
	public Collection<String> showAutoCompletes(Command commandObject, String args) {
		return new LinkedList<String>();
	}
	
	
	public void cleanUp() {	}
}

