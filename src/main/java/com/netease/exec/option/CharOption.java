package com.netease.exec.option;

import com.netease.exec.Plugin;


public class CharOption extends Option {
	protected char value;
	protected char defaultValue;

	public CharOption(Plugin plugin, String name, String description, char value) {
		super(plugin, name, description);
		this.value = this.defaultValue = value;
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
		return null;
	}

	@Override
	public void setValue(String value) throws Exception {
		if (value.length() != 1)
			throw new IllegalArgumentException("Illegal value '" + value + "' for char option '" + name + "'");
		this.value = value.charAt(0);
	}

}
