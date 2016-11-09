package com.netease.exec.option;

import com.netease.exec.Plugin;



public class IntOption extends Option {
	protected int defaultValue;
	protected int value;
	protected String valueRange;
	
	public IntOption(Plugin plugin, String name, String description, int value) {
		super(plugin, name, description);
		this.value = this.defaultValue = value;
	}

	public int getIntValue() {
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
		return valueRange;
	}

	@Override
	public void setValue(String value) throws Exception {
		try {
			this.value = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Illegal value '" + value + "' for int option '" + name + "'");
		}
	}

}
