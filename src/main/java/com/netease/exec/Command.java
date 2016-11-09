package com.netease.exec;


public abstract class Command implements Comparable<Command> {
	
	protected Plugin plugin;
	
	protected String hint;
	
	protected String idString;
	
	protected String description;
	
	protected String syntax;
	
	protected String help;
	
	protected boolean caseSensitive;
	
	protected int idStringLength;

	public Command(Plugin plugin, String idString, String description, String syntax, String help) {
		this.plugin = plugin;
		this.idString = idString;
		this.description = description;
		this.syntax = syntax;
		this.help = help;
		idStringLength = idString.split(" ").length;
		this.hint = "";
	}

	public Plugin getPlugin() {
		return plugin;
	};
	
	public String getHelp() {
		return help;
	}

	public String getIdString() {
		return idString;
	}

	public int getIdStringLength() {
		return idStringLength;
	}

	public String getDescription() {
		return description;
	}
	
	public String getSyntax() {
		return syntax;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	public String getHint() {
		return hint;
	}
	
	public void setHint(String hint) {
		this.hint = hint;
	}

	
	public abstract Object execute(String args) throws Exception;
	
	public void cancel() {}

	public int compareTo(Command o) {
		if (o == null)
			return -1;
		return idString.compareTo(o.idString);
	}
}
