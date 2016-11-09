package com.netease.backend.db.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class CmdExecutor extends Thread {

	
	private String cmd;

	
	boolean result = true;

	
	String msg = "";

	
	String errMsg = "";

	
	public CmdExecutor(String command) {
		if (command != null)
			this.cmd = command.trim();
	}

	
	public static String exec(String cmd) throws Exception {
		String msg = "";
		String errMsg = "";

		if (cmd == null || cmd.equals(""))
			throw new Exception("command is null.");

		BufferedReader brInput = null;
		BufferedReader brErr = null;
		try {
			String cmdArray[] = { "/bin/bash", "-c", cmd };
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec(cmdArray);

			brInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = brInput.readLine()) != null)
				msg += line + "\n";

			brErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			while ((line = brErr.readLine()) != null)
				errMsg += line + "\n";

			if (errMsg.equals(""))
				errMsg = "Unknown reason.";

			int ret = process.waitFor();
			process.destroy();
			if (ret != 0)
				throw new Exception(errMsg);
			return msg;
		} catch (IOException ioe) {
			throw new Exception(errMsg);
		} catch (InterruptedException ie) {
			throw new Exception(errMsg);
		}
	}

	
	public static String execCmd(String cmd) throws Exception {
		Runtime rt = Runtime.getRuntime();
		Process process;

		
		StreamGobbler outputGlobber = null;
		StreamGobbler errGlobber = null;

		String str[] = new String[3];
		str[0] = "/bin/sh";
		str[1] = "-c";
		str[2] = cmd;
		try {
			process = rt.exec(str);
			outputGlobber = new StreamGobbler(process.getInputStream(), "Output", null, true);
			errGlobber = new StreamGobbler(process.getErrorStream(), "Error", null, true);
			outputGlobber.start();
			errGlobber.start();
			process.waitFor();
			if (process.exitValue() != 0) 
				throw new Exception(errGlobber.getMsg());
		} catch (IOException ioe) {
			throw new Exception(ioe.getMessage());
		} catch (InterruptedException ie) {
			throw new Exception("Process is interrupted.");
		}
		return outputGlobber.getMsg();
	}

	
	public static void execNoWait(String cmd) throws Exception {
		if (cmd == null || cmd.equals(""))
			throw new Exception("command is null.");

		try {
			String cmdArray[] = { "/bin/bash", "-c", cmd };
			Runtime rt = Runtime.getRuntime();
			rt.exec(cmdArray);
		} catch (IOException ioe) {
			throw new Exception(ioe.getMessage());
		}
	}

	public void run() {
		result = true;
		try {
			msg = exec(cmd);
		} catch (Exception e) {
			result = false;
			errMsg = e.getMessage();
		}
	}

	public String getCmd() {
		return cmd;
	}

	public String getMsg() {
		return msg;
	}

	public boolean getResult() {
		return result;
	}

	public static void main(String[] args) {
		String cmd = "ssh ddb@test-3.lab.163.org mysqld_safe --defaults-file=/home/ddb/mys.cnf &";
		
		
		try {
			System.out.println("command: " + cmd);
			String msg = CmdExecutor.exec(cmd);
			System.out.println("Output: " + msg);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}
