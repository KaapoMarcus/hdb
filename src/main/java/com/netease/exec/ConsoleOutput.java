package com.netease.exec;

import java.io.IOException;
import java.io.PrintStream;

import com.netease.cli.StringTable;
import com.netease.cli.TableFormatter;


public class ConsoleOutput implements OutputDevice {
	protected PrintStream output;
	
	public ConsoleOutput(PrintStream output) {
		this.output = output;
	}

	public void puts(Result result) throws IOException {
		Object data = result.getData();
		if (data != null) {
			if (data instanceof StringTable)
				new TableFormatter().print((StringTable)data, output);
			else
				output.println(data);
		}
	}

	public void puts(Command commandObject, String command, Exception e) throws IOException {
		e.printStackTrace();
	}

	public void puts(Command commandObject, String command, String msg) throws IOException {
		output.println(msg);
	}
}
