package com.netease.exec.option;

import com.netease.exec.Plugin;



public class StringOption extends Option {
	protected String defaultValue;
	protected String value;
	
	public StringOption(Plugin plugin, String name, String description, String value) {
		super(plugin, name, description);
		this.value = defaultValue = value;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getValueRange() {
		return null;
	}

	@Override
	public void setValue(String value) throws Exception {
		this.value = value;
	}

}
