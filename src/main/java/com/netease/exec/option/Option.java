package com.netease.exec.option;

import com.netease.exec.Command;
import com.netease.exec.Plugin;


public abstract class Option extends Command {
	
	protected String name;
	
	public Option(Plugin plugin, String name, String description) {
		super(plugin, "SET " + name, description, "SET " + name + " VALUE", "");
		this.name = name;
	}
	
	public Option(Plugin plugin, String name, String description, String help) {
		super(plugin, "SET " + name, description, "SET " + name + " VALUE", help);
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public Object execute(String args) throws Exception {
		setValue(args);
		return null;
	}

	
	public abstract void setValue(String value) throws Exception;
	
	
	public abstract String getValueRange();
	
	
	public abstract String getDefaultValue();
	
	
	public abstract String getValue();
}
