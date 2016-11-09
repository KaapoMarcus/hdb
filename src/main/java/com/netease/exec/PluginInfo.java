package com.netease.exec;

import java.util.Collection;

import com.netease.exec.option.Option;
import com.netease.util.Trie;
import com.netease.util.DList.DLink;


class PluginInfo {
	
	Plugin plugin;
	
	Collection<Command> commands;
	
	Command genericCommand;
	
	Collection<Option> options;
	
	Trie<Executor.CKey, Command> commandTrie;
	
	DLink<PluginInfo> link;
	
	PluginInfo(Plugin plugin, Collection<Command> commands, Collection<Option> options) {
		this.plugin = plugin;
		this.commands = commands;
		this.options = options;
		for (Command c : commands) {
			if (c.getIdString().length() == 0) {
				if (genericCommand != null)
					throw new IllegalArgumentException("An plugin can not register multiple generic commands");
				genericCommand = c;
			}
		}
		
		commandTrie = new Trie<Executor.CKey, Command>();
		addCommands(commands);
		addCommands(options);
	}

	private void addCommands(Collection<? extends Command> commands) {
		for (Command c : commands) {
			if (c.getIdStringLength() == 0)
				continue;
			String[] a = c.getIdString().split(" ");
			Executor.CKey[] keys = new Executor.CKey[a.length];
			for (int i = 0; i < a.length; i++)
				keys[i] = new Executor.CKey(a[i], !c.isCaseSensitive());
			if (commandTrie.put(keys, c) != null)
				throw new IllegalArgumentException("Duplicate command: " + c.getIdString());
		}
	}
}
