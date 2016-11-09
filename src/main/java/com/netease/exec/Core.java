package com.netease.exec;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.netease.cli.CmdHelper;
import com.netease.cli.StringTable;
import com.netease.exec.option.BooleanOption;
import com.netease.exec.option.CharOption;
import com.netease.exec.option.Option;
import com.netease.exec.option.StringOption;
import com.netease.util.DList.DLink;



public final class Core extends Plugin {
	
	BooleanOption force;
	
	BooleanOption quiet;
	
	CharOption delimiter;
	
	BooleanOption ignoreError;
	
	StringOption charset;
	
	private Command cExecfile;
	private Command cInstallPlugin;
	private Command cUninstallPlugin;
	private Command cShowPlugins;
	private Command cShowCommands;
	private Command cShowCommandsFor;
	private Command cShowOptions;
	private Command cShowOptionsFor;
	private Command cPrefer;
	private Command cUsage;
	private Command cSuggest;
	private Command cHelp;
	private Command cExit;
	private Command cTry;
	private Command cCatch;
	private Command cFinally;
	private Command cEnd;
	private Command cThrow;
	private Command cEcho;
	
	public Core(Executor executor) {
		super("core", "Provides built-in functions of the executor.", executor);
		
		force = new BooleanOption(this, "force", "Force to execute any commands without confirm.", false);
		quiet = new BooleanOption(this, "quiet", "Quiet mode, don't output any result.", false);
		ignoreError = new BooleanOption(this, "ignore_error", "Don't stop on errors.", false);
		delimiter = new CharOption(this, "delimiter", "Command delimiter", ';');
		charset = new StringOption(this, "charset", "Charset used to process scripts.", "UTF-8") {
			@Override
			public void setValue(String value) {
				if (value.equalsIgnoreCase("UTF-8"))
					this.value = "UTF-8";
				else if (value.equalsIgnoreCase("GBK"))
					this.value = "GBK";
				else
					throw new IllegalArgumentException("Charset must be UTF-8 or GBK");
			}			
		};
		
		cExecfile = new Command(this, "execfile", "Run a script.", "EXECFILE [-q] file", "TODO"){
			@Override
			public Object execute(String args) throws Exception {
				return source(args);
			}
		};
		cInstallPlugin = new Command(this, "install plugin", "Install a plugin.", "INSTALL PLUGIN factory_class args", "TODO"){
			@Override
			public Object execute(String args) throws Exception {
				return installPlugin(args);
			}
		};
		cUninstallPlugin = new Command(this, "uninstall plugin", "Uninstall a plugin.", "UNINSTALL PLUGIN name", "TODO"){
			@Override
			public Object execute(String args) throws Exception {
				return uninstallPlugin(args);
			}
		};
		cShowPlugins = new Command(this, "show plugins", "Show installed plugins", "SHOW PLUGINS", "TODO"){
			@Override
			public Object execute(String args) throws Exception {
				return showPlugins(args);
			}
		};
		cShowCommands = new Command(this, "show commands", "Show all commands.",
				"SHOW COMMANDS", "TODO"){
			@Override
			public Object execute(String args) throws Exception {
				return showCommands(args);
			}
		};
		cShowCommandsFor = new Command(this, "show commands for", "Show commands of the specified plugin.",
				"SHOW COMMANDS FOR plugin", "TODO"){
			@Override
			public Object execute(String args) throws Exception {
				return showCommandsFor(args);
			}
		};
		cShowOptions = new Command(this, "show options", "Show all options.",
				"SHOW OPTIONS", "TODO") {
			@Override
			public Object execute(String args) throws Exception {
				return showOptions(args);
			}
		};
		cShowOptionsFor = new Command(this, "show options for", "Show options of the specified plugin.",
				"SHOW OPTIONS FOR plugin", "TODO") {
			@Override
			public Object execute(String args) throws Exception {
				return showOptionsFor(args);
			}
		};
		cPrefer = new Command(this, "prefer", "Set the specified plugin as \nprefered plugin.", "PREFER plugin", "TODO"){
			@Override
			public Object execute(String args) throws Exception {
				return prefer(args);
			}
		};
		cUsage = new Command(this, "usage", "Show short usage information for \nthe specified command or option.", "USAGE [plugin.](command|option)", "TODO")
		{
			@Override
			public Object execute(String args) throws Exception {
				return usage(args);
			}
		};
		cHelp = new Command(this, "help", "Show detail information for \nthe specified command or option.", "HELP [[plugin.](command|option)]", "TODO") {
			@Override
			public Object execute(String args) throws Exception {
				return help(args);
			}
		};
		cSuggest = new Command(this, "suggest", "Show suggestions for the command.", "SUGGEST command", "TODO") {
			@Override
			public Object execute(String args) throws Exception {
				return suggest(args);
			}
		};
		cExit = new Command(this, "exit", "Exit current script.", "EXIT", "TODO") {
			@Override
			public Object execute(String args) throws Exception {
				
				return null;
			}
		};
		cTry = new Command(this, "try", "Begin a try..catch..finally..end block.", "TRY", "TODO") {
			@Override
			public Object execute(String args) throws Exception {
				return null;
			}
		};
		cCatch = new Command(this, "catch", "Begin a catch block which will be executed on error.", "CATCH", "TODO") {
			@Override
			public Object execute(String args) throws Exception {
				return null;
			}
		};
		cFinally = new Command(this, "finally", "Begin a finally block which will always be executed", "FINALLY", "TODO") {
			@Override
			public Object execute(String args) throws Exception {
				return null;
			}
		};
		cEnd = new Command(this, "end", "End a statement block.", "END", "TODO") {
			@Override
			public Object execute(String args) throws Exception {
				return null;
			}
		};
		cThrow = new Command(this, "throw", "Throw an exception", "THROW message", "TODO") {
			@Override
			public Object execute(String args) throws Exception {
				throw new RuntimeException(args);
			}
		};
		cEcho = new Command(this, "echo", "Print a string", "ECHO message", "TODO") {
			@Override
			public Object execute(String args) throws Exception {
				return echo(args);
			}
		};
	}
	

	public StringOption getCharset() {
		return charset;
	}

	@Override
	public Collection<Command> getCommands() {
		List<Command> commands = Arrays.asList(new Command[]{
			cExecfile,
			cInstallPlugin,
			cUninstallPlugin,
			cShowPlugins,
			cShowCommands,
			cShowCommandsFor,
			cShowOptions,
			cShowOptionsFor,
			cPrefer,
			cUsage,
			cHelp,
			cSuggest,
			cExit,
			cTry,
			cCatch,
			cFinally,
			cEnd,
			cThrow,
			cEcho
		});
		Collections.sort(commands);
		return commands;
	}

	public CharOption getDelimiter() {
		return delimiter;
	}

	public BooleanOption getForce() {
		return force;
	}

	public BooleanOption getIgnoreError() {
		return ignoreError;
	}

	@Override
	public Collection<Option> getOptions() {
		return Arrays.asList(new Option[] {
			force,
			quiet,
			delimiter,
			ignoreError,
			charset
		});
	}

	public BooleanOption getQuiet() {
		return quiet;
	}

	private Object help(String command) {
		if (command.length() == 0)
			return "Use 'show commands' to show a list of available commands.";
		else {
			Command commandObject = getCommand(command);
			if (commandObject == null)
				throw new IllegalArgumentException("Command '" + command + "' not found.");
			return "Usage: " + commandObject.getSyntax() + "\n" + commandObject.getDescription() + "\n\n" +  
				commandObject.getHelp();
		}
	}


	private Command getCommand(String command) {
		int dot = command.indexOf('.');
		if (dot > 0) {
			String pluginName = command.substring(0, dot);
			String commandName = command.substring(dot + 1);
			PluginInfo pi = executor.pluginMap.get(pluginName.toLowerCase());
			if (pi == null)
				throw new IllegalArgumentException("Plugin '" + pluginName + "' not found.");
			for (Command c: pi.commands) {
				if ((c.isCaseSensitive() && c.getIdString().equals(commandName)) 
						|| (!c.isCaseSensitive() && c.getIdString().equalsIgnoreCase(commandName))) {
					return c;
				}
			}
			for (Option o: pi.options) {
				if (o.getIdString().equalsIgnoreCase(commandName))
					return o;
			}
		} else {
			for (DLink<PluginInfo> link = executor.pluginList.getHeader().getNext(); link != executor.pluginList.getHeader(); link = link.getNext()) {
				PluginInfo pi = link.get();
				for (Command c: pi.commands) {
					if ((c.isCaseSensitive() && c.getIdString().equals(command)) 
							|| (!c.isCaseSensitive() && c.getIdString().equalsIgnoreCase(command))) {
						return c;
					}
				}
				for (Option o: pi.options) {
					if (o.getIdString().equalsIgnoreCase(command))
						return o;
				}
			}
		}
		return null;
	}
	
	private Object installPlugin(String args) throws Exception {
		String[] a = CmdHelper.splitCommand(args);
		if (a.length < 1)
			throw new IllegalArgumentException("Syntax error: install plugin " + args);
		String factoryClass = a[0];
		Object o = Class.forName(factoryClass).newInstance();
		if (!(o instanceof PluginFactory))
			throw new IllegalArgumentException("Class '" + factoryClass + "' is not plugin factory.");
		String[] createArgs = new String[a.length - 1];
		for (int i = 0; i < createArgs.length; i++)
			createArgs[i] = a[i + 1];
		Plugin plugin = ((PluginFactory)o).createPlugin(executor, createArgs);
		executor.installPlugin(plugin);
		
		return null;
	}

	Object prefer(String pluginName) {
		PluginInfo pi = executor.pluginMap.get(pluginName.toLowerCase());
		if (pi == null)
			throw new IllegalArgumentException("Plugin '" + pluginName + "' not found.");
		pi.link.unLink();
		executor.pluginList.getHeader().getNext().addAfter(pi.link);
		return null;
	}

	private Object showCommands(String unused) {
		StringTable t = new StringTable("Commands", new String[]{"PLUGIN", "NAME", "SYNTAX", "DESCRIPTION"});
		for (DLink<PluginInfo> link = executor.pluginList.getHeader().getNext(); link != executor.pluginList.getHeader(); link = link.getNext()) {
			PluginInfo pi = link.get();
			for (Command c: pi.commands) {
				t.addRow(new String[]{
						pi.plugin.getName(),
						c.getIdString(),
						c.getSyntax(),
						c.getDescription()
				});
			}
		}
		return t;
	}
	
	private Object showCommandsFor(String pluginName) {
		PluginInfo pi = executor.pluginMap.get(pluginName.toLowerCase());
		if (pi == null)
			throw new IllegalArgumentException("Plugin '" + pluginName + "' not found.");
		StringTable t = new StringTable("Commands for " + pi.plugin.getName(), new String[]{"NAME", "SYNTAX", "DESCRIPTION"});
		for (Command c: pi.commands) {
			t.addRow(new String[]{
					c.getIdString(),
					c.getSyntax(),
					c.getDescription()
			});
		}
		return t;
	}
	
	private Object showOptions(String unused) {
		StringTable t = new StringTable("Options", new String[]{"PLUGIN", "NAME", "VALUE", "DESCRIPTION"});
		for (DLink<PluginInfo> link = executor.pluginList.getHeader().getNext(); link != executor.pluginList.getHeader(); link = link.getNext()) {
			PluginInfo pi = link.get();
			for (Option o: pi.options) {
				t.addRow(new String[]{
						pi.plugin.getName(),
						o.getName(),
						o.getValue(),
						o.getDescription()
				});
			}
		}
		return t;
	}
	
	private Object showOptionsFor(String pluginName) {
		PluginInfo pi = executor.pluginMap.get(pluginName.toLowerCase());
		if (pi == null)
			throw new IllegalArgumentException("Plugin '" + pluginName + "' not found.");
		StringTable t = new StringTable("Options for " + pi.plugin.getName(), new String[]{"NAME", "VALUE", "DESCRIPTION"});
		for (Option o: pi.options) {
			t.addRow(new String[]{
					o.getName(),
					o.getValue(),
					o.getDescription()
			});
		}
		return t;
	}
	
	private Object showPlugins(String unused) {
		StringTable t = new StringTable("Plugins", new String[]{"NAME", "#COMMANDS", "#OPTIONS", "DESCRIPTION"});
		for (DLink<PluginInfo> link = executor.pluginList.getHeader().getNext(); link != executor.pluginList.getHeader(); link = link.getNext()) {
			PluginInfo pi = link.get();
			t.addRow(new String[]{pi.plugin.getName(),
					"" + pi.commands.size(),
					"" + pi.options.size(),
					pi.plugin.getDescription()
			});
		}
		return t;
	}

	private Object source(String args) throws Exception {
		String[] a = CmdHelper.splitCommand(args);
		if (a.length < 1 || a.length > 2)
			throw new IllegalArgumentException("Syntax error: " + args);
		boolean quiet = false;
		String file;
		if (a[0].equals("-q")) {
			quiet = true;
			if (a.length != 2)
				throw new IllegalArgumentException("Syntax error: " + args);
			file = a[1];
		} else {
			if (a.length != 1)
				throw new IllegalArgumentException("Syntax error: " + args);
			file = a[0];
		}
		
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset.getValue()));
		String oldQuiet = this.quiet.getValue();
		try {
			if (quiet)
				this.quiet.setValue("true");
			executor.executeCommands(input);
		} finally {
			this.quiet.setValue(oldQuiet);
			input.close();
		}
		return null;
	}

	private Object uninstallPlugin(String pluginName) {
		executor.uninstallPlugin(pluginName);
		return null;
	}

	private Object usage(String command) {
		if (command.length() == 0)
			return "Use 'show commands' to show a list of available commands.";
		else {
			Command commandObject = getCommand(command);
			if (commandObject == null)
				throw new IllegalArgumentException("Command '" + command + "' not found.");
			return commandObject.getSyntax();
		}
	}
	
	private Object suggest(String command) {
		StringTable t = new StringTable("Suggestions", new String[]{"Suggestion"});
		Collection<String> suggests = executor.showAutoCompletes(command);
		for (String s: suggests)
			t.addRow(new String[]{s});
		return t;
	}
	
	private Object echo(String msg) throws IOException {
		executor.puts(cEcho, "echo " + msg, msg);
		return null;
	}
}
