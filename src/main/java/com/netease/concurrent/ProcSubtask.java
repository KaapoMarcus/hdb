package com.netease.concurrent;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class ProcSubtask implements Subtask<Integer> {
	private String command;
	private Process proc;
	private String output;
	private String error;
	
	public ProcSubtask(String command) {
		this.command = command;
	}
	
	
	public void cancel() {
		if (proc != null)
			proc.destroy();
	}

	
	public String getError() {
		return error;
	}

	
	public String getOutput() {
		return output;
	}

	public Integer execute() throws Exception {
		proc = Runtime.getRuntime().exec(command);
		
		String s = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			if (s == null)
				s = line;
			else
				s += "\n" + line;
		}
		br.close();
		output = s;
		
		s = null;
		br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
		while ((line = br.readLine()) != null) {
			if (s == null)
				s = line;
			else
				s += "\n" + line;
		}
		error = s;
		
		int r = proc.waitFor();
		proc = null;
		return r;
	}
}
