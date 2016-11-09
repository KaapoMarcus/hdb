package com.netease.exec.option;

import com.netease.exec.Plugin;



public class BooleanOption extends Option {
	protected boolean defaultValue;
	protected boolean value;
	
	public BooleanOption(Plugin plugin, String name, String description, boolean value) {
		super(plugin, name, description);
		this.value = this.defaultValue = value;
	}

	public boolean getBooleanValue() {
		return value;
	}
	
	@Override
	public String getDefaultValue() {
		return "" + defaultValue;
	}

	@Override
	public String getValue() {
		return "" + value;
	}

	@Override
	public String getValueRange() {
		return "true|false";
	}

	@Override
	public void setValue(String value) throws Exception {
		if (value != null)
			value = value.trim();
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1"))
			this.value = true;
		else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no") || value.equalsIgnoreCase("0"))
			this.value = false;
		else
			throw new IllegalArgumentException("Illegal value '" + value + "' for boolean option '" + name + "'");
	}
}
