package com.jhh.hdb.proxyserver.qssql;

import java.io.IOException;

import com.netease.exec.Command;
import com.netease.exec.OutputDevice;
import com.netease.exec.Result;

/**
 * 
 */
class QSIsqlOutput implements OutputDevice {
	
	private Object resultData = null;

    public QSIsqlOutput() {
        super();
    }
    
    public void puts(Result result) throws IOException {
    	resultData = result.getData();
    }

    public void puts(Command commandObject, String command, Exception e)
            throws IOException {
    	throw new IOException("Not supported function!");
    }
    
    public void puts(Command commandObject, String command, String msg) throws IOException {
    	throw new IOException("Not supported function!");
	}

    public void clearResult() {
    	this.resultData = null;
    }
    
    public Object getResultData() {
    	return this.resultData;
    }
}
