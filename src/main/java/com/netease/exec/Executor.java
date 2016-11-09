package com.netease.exec;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.netease.cli.CmdHelper;
import com.netease.cli.CmdWordReader;
import com.netease.exec.option.Option;
import com.netease.util.DList;
import com.netease.util.Pair;
import com.netease.util.DList.DLink;


public class Executor {
	
	InputDevice inputDevice;
	
	OutputDevice outputDevice;
	
	Map<String, PluginInfo> pluginMap;
	
	DList<PluginInfo> pluginList;
	
	Core corePlugin;
	
	
	private Command currentCommandObject;
	
	private String lastCommand;
	
	private boolean canceled;
	
	
	enum TryBlock {
		Try,
		Catch,
		Finally
	}
	
	class TryState {
		
		TryBlock block = TryBlock.Try;
		
		boolean errorInTry;
		
		boolean errorInBlock;
		
		Exception exception;
	}
	private LinkedList<TryState> tryStack;
	
	
	
	public Executor(InputDevice inputDevice, OutputDevice outputDevice) {
		this.inputDevice = inputDevice;
		this.outputDevice = outputDevice;
		pluginMap = new HashMap<String, PluginInfo>();
		pluginList = new DList<PluginInfo>();
		
		corePlugin = new Core(this);
		installPlugin(corePlugin);
		tryStack = new LinkedList<TryState>();
	}
	
	
	public void installPlugin(Plugin p) {
		if (pluginMap.get(p.getName().toLowerCase()) != null)
			throw new IllegalArgumentException("Plugin '" + p.getName() + "' has already been installed.");
		PluginInfo pi = new PluginInfo(p, p.getCommands(), p.getOptions());
		pluginMap.put(p.getName().toLowerCase(), pi);
		pi.link = pluginList.addLast(pi);
	}
	
	
	public void uninstallPlugin(String name) {
		if (name.equalsIgnoreCase(corePlugin.getName()))
			throw new IllegalArgumentException("Can not uninstall core plugin.");
		PluginInfo pi = pluginMap.remove(name.toLowerCase());
		if (pi == null)
			throw new IllegalArgumentException("Plugin '" + name + "' not found.");
		pi.link.unLink();
		pi.plugin.cleanUp();
	}
	
	
	public Core getCorePlugin() {
		return corePlugin;
	}
	
	
	public Plugin getPreferedPlugin() {
		if (pluginList.size() > 1)
			return pluginList.getHeader().getNext().getNext().get().plugin;
		else
			return pluginList.getHeader().getNext().get().plugin;
	}
	
	
	public void setPreferedPlugin(String pluginName) {
		corePlugin.prefer(pluginName);
	}
	
	
	public Plugin getPluginByName(String name) {
		PluginInfo pi = pluginMap.get(name.toLowerCase());
		if (pi == null)
			return null;
		return pi.plugin;
	}
	
	public InputDevice getInputDevice() {
		return inputDevice;
	}

	public OutputDevice getOutputDevice() {
		return outputDevice;
	}

	
	public String getLastCommand() {
		return lastCommand;
	}
	
	
	public void execute(String command) throws Exception {
		
		canceled = false;
		
		StringReader input = new StringReader(command);
		try {
			executeCommands(input);
		} finally {
			input.close();
		}
	}

	
	public void cancel() throws Exception {
		if (currentCommandObject != null)
			currentCommandObject.cancel();
		canceled = true;
	}

	
	public Collection<String> showAutoCompletes(String command) {
		
		StringReader input = new StringReader(command);
		String lastCommand = "", thisCommand;
		try {
			while ((thisCommand = CmdHelper.readCommand(input, corePlugin.getDelimiter().getValue(), '\'')) != null) {
				lastCommand = thisCommand;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		String commandId = "";
		CKeyReader kr = new CKeyReader(lastCommand);
		CKey key;
		int wordCount = 0;
		while (kr.hasNext()) {
			key = kr.next();
			commandId += " " + key.key;
			wordCount++;
		}
		commandId = commandId.trim();
		
		
		Set<String> suggests = new HashSet<String>();
		for (DLink<PluginInfo> link = pluginList.getHeader().getNext(); link != pluginList.getHeader(); link = link.getNext()) {
			PluginInfo pi = link.get();
			String thisSuggest;
			for (Command c: pi.commands) {
				if ((thisSuggest = getSuggest(c, commandId, wordCount)) != null)
					suggests.add(thisSuggest);
			}
			for (Option c: pi.options) {
				if ((thisSuggest = getSuggest(c, commandId, wordCount)) != null)
					suggests.add(thisSuggest);
			}
		}
		
		try {
			Pair<Command, String> dispatchResult = dispatchCommand(lastCommand);
			suggests.addAll(dispatchResult.getFirst().getPlugin().showAutoCompletes(dispatchResult.getFirst(), dispatchResult.getSecond()));
		} catch (Exception e) { }
		return suggests;
	}
	
	
	public void puts(Result result) throws IOException {
		if (!corePlugin.quiet.getBooleanValue())
			outputDevice.puts(result);
	}
	
	
	public void puts(Command commandObject, String command, Exception e) throws IOException {
		if (!corePlugin.quiet.getBooleanValue())
			outputDevice.puts(commandObject, command, e);
	}
	
	
	public void puts(Command commandObject, String command, String msg) throws IOException {
		if (!corePlugin.quiet.getBooleanValue())
			outputDevice.puts(commandObject, command, msg);
	}
	
	private String getSuggest(Command c, String commandId, int wordCount) {
		if ((c.isCaseSensitive() && c.getIdString().startsWith(commandId))
				|| (!c.isCaseSensitive() && c.getIdString().toLowerCase().startsWith(commandId.toLowerCase()))) {
			if (c.getIdString().length() == commandId.length())
				return null;
			int skip;
			if (c.getIdString().charAt(commandId.length()) == ' ')
				skip = wordCount;
			else
				skip = wordCount - 1;
			CKeyReader kr = new CKeyReader(c.getIdString());
			while (skip-- > 0)
				kr.next();
			return kr.next().key;
		}
		return null;
	}
	
	void executeCommands(Reader input) throws IOException, Exception {
		
		
		String thisCommand;
		while ((thisCommand = CmdHelper.readCommand(input, corePlugin.getDelimiter().getValue(), '\'')) != null) {
			if (canceled)
				break;
			thisCommand = thisCommand.trim();
			
			if (thisCommand.length() == 0)
				continue;
			if (thisCommand.startsWith("#") || thisCommand.startsWith("--"))
				continue;
			if (thisCommand.equalsIgnoreCase("exit"))
				break;
			
			try {
				Pair<Command, String> dispatchResult = dispatchCommand(thisCommand);
				currentCommandObject = dispatchResult.getFirst();
				lastCommand = thisCommand;
				
				if (thisCommand.equalsIgnoreCase("try")) {
					tryStack.add(new TryState());
				} else if (thisCommand.equalsIgnoreCase("catch")) {
					if (tryStack.isEmpty() || tryStack.getLast().block != TryBlock.Try)
						throw new IllegalArgumentException("Syntax error, try/catch mismatch.");
					tryStack.getLast().block = TryBlock.Catch;
					tryStack.getLast().errorInBlock = false;
					tryStack.getLast().exception = null;
				} else if (thisCommand.equalsIgnoreCase("finally")) {
					if (tryStack.isEmpty() || (tryStack.getLast().block != TryBlock.Try
						&& tryStack.getLast().block != TryBlock.Catch))
						throw new IllegalArgumentException("Syntax error, try/catch/finally mismatch.");
					tryStack.getLast().block = TryBlock.Finally;
					tryStack.getLast().errorInBlock = false;
				} else if (thisCommand.equalsIgnoreCase("end")) {
					if (tryStack.size() == 0)
						throw new IllegalArgumentException("Syntax error, not in try block.");
					Exception e = tryStack.getLast().exception;
					tryStack.removeLast();
					if (e != null)
						throw e;
				} else {
					
					boolean shouldExec = true;
					for (int i = 0; i < tryStack.size(); i++) {
						TryState state = tryStack.get(i);
						if (state.errorInBlock) {
							
							shouldExec = false;
							break;
						} else if (state.block == TryBlock.Catch && !state.errorInTry) {
							
							shouldExec = false;
							break;
						}
					}
					if (shouldExec) {
						long before = System.currentTimeMillis();
						Object data = dispatchResult.getFirst().execute(dispatchResult.getSecond());
						Result r = new Result(currentCommandObject, thisCommand, data, System.currentTimeMillis() - before);
						if (!corePlugin.quiet.getBooleanValue())
							outputDevice.puts(r);
						lastCommand = null;
					}
				}
			} catch (Exception e) {
				
				
				if (corePlugin.ignoreError.getBooleanValue())
					outputDevice.puts(currentCommandObject, thisCommand, e);
				else if (tryStack.size() > 0) {
					TryState state = tryStack.getLast();
					if (state.block == TryBlock.Try) 
						state.errorInTry = true;
					state.errorInBlock = true;
					state.exception = new Exception("Command '" + thisCommand + "' execute error: " +  e.getMessage(), e.getCause());
				} else
					throw e;
			} finally {
				currentCommandObject = null;
			}
		}
	}

	
	private Pair<Command, String> dispatchCommand(String command) {
		Pair<PluginInfo, String> r = getPlugin(command);
		if (r != null) {	
			PluginInfo pi = r.getFirst();
			Pair<String, String> hintCommand = checkCommandHint(r.getSecond());
			CKeyReader keyReader = new CKeyReader(hintCommand.getSecond());
			Command c = pi.commandTrie.get(keyReader);
			if (c != null) {
				c.setHint(hintCommand.getFirst());
				return new Pair<Command, String>(c, getCommandArgs(c, hintCommand.getSecond()));
			}
			if (pi.genericCommand != null) {
				pi.genericCommand.setHint(hintCommand.getFirst());
				return new Pair<Command, String>(pi.genericCommand, hintCommand.getSecond());
			}
		} else {
			Pair<String, String> hintCommand = checkCommandHint(command);
			for (DLink<PluginInfo> link = pluginList.getHeader().getNext(); link != pluginList.getHeader(); link = link.getNext()) {
				PluginInfo pi = link.get();
				CKeyReader keyReader = new CKeyReader(hintCommand.getSecond());
				Command c = pi.commandTrie.get(keyReader);
				if (c != null) {
					c.setHint(hintCommand.getFirst());
					return new Pair<Command, String>(c, getCommandArgs(c, hintCommand.getSecond()));
				}
			}
			if (pluginList.size() >= 2) {
				PluginInfo preferedPlugin = pluginList.getHeader().getNext().getNext().get();
				if (preferedPlugin.genericCommand != null) {
					preferedPlugin.genericCommand.setHint(hintCommand.getFirst());
					return new Pair<Command, String>(preferedPlugin.genericCommand, hintCommand.getSecond());
				}
			}
		}
		throw new IllegalArgumentException("Unknown command: " + command);
	}
	
	private Pair<String, String> checkCommandHint(String command) {
		if (command.startsWith("");
			
			
			boolean hasNext = true;
			int startIndex = 2;
			int end = 2;
			while(hasNext){
				end = command.indexOf("*/", startIndex);
				if (-1 == end)
					throw new IllegalArgumentException("Invalid hint command: " + command);
				startIndex = end + 2;
				while(command.charAt(startIndex) == ' ')
					startIndex++;
				if(command.charAt(startIndex) == '/')
					hasNext = true;
				else
					hasNext = false;
			}
			return new Pair<String, String>(command.substring(0, end + 2), command.substring(end + 2).trim());
		} else
			return new Pair<String, String>("", command);
	}

	
	private Pair<PluginInfo, String> getPlugin(String command) {
		for (int i = 0; i < command.length(); i++) {
			char c = command.charAt(i);
			if (Character.isWhitespace(c))
				return null;
			else if (c == ':') {
				String pluginName = command.substring(0, i);
				PluginInfo pi = pluginMap.get(pluginName);
				if (pi == null)
					throw new IllegalArgumentException("Plugin '" + pluginName + "' not found.");
				
				i++;
				while (i < command.length() && Character.isWhitespace(command.charAt(i)))
					i++;
				if (i == command.length())
					throw new IllegalArgumentException("Invalid empty command: " + command);
				return new Pair<PluginInfo, String>(pi, command.substring(i));
			}
		}
		return null;
	}

	
	private String getCommandArgs(Command c, String command) {
		if (c.getIdString().length() == 0)
			return command;
			
		CKeyReader kr = new CKeyReader(command);
		for (int i = 0; i < c.getIdStringLength(); i++)
			kr.next();
		return kr.getRemain();
	}
	
	
	static class CKey {
		
		String key;
		
		boolean ignoreCase;

		public CKey(String key, boolean ignoreCase) {
			this.key = key;
			this.ignoreCase = ignoreCase;
		}

		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((key == null) ? 0 : key.toLowerCase().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof CKey))
				return false;
			final CKey other = (CKey) obj;
			if (key == null) 
				return other.key == null;
			else {
				
				if (ignoreCase || other.ignoreCase)
					return key.equalsIgnoreCase(other.key);
				else
					return key.equals(other.key);
			}
		}
	}
	
	class CKeyReader implements Iterator<CKey> {
		private CmdWordReader wordReader;
		private String remain;
		private CKey next;
		
		CKeyReader(String command) {
			wordReader = new CmdWordReader(command);
			remain = command;
			String word = wordReader.next();
			if (word != null)
				next = new CKey(word, true);
		}
		
		public CKey next() {
			if (next == null)
				throw new NoSuchElementException();
			remain = wordReader.getRemain();
			CKey r = next;
			String word = wordReader.next();
			if (word != null)
				next = new CKey(word, true);
			else
				next = null;
			return r;
		}
		
		String getRemain() {
			return remain;
		}

		public boolean hasNext() {
			return next != null;
		}

		public void remove() {
			throw new UnsupportedOperationException();			
		}
	}
}
