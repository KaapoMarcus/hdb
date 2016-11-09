package com.netease.exec;

import java.io.IOException;


public interface OutputDevice {
	
	void puts(Result result) throws IOException;
	
	
	void puts(Command commandObject, String command, Exception e) throws IOException;
	
	
	void puts(Command commandObject, String command, String msg) throws IOException;
}
