
package com.netease.backend.db.common.management.socket;

import java.io.IOException;
import java.net.Socket;

import com.netease.pool.Disposable;


public class DbaSocket implements Disposable {

	
	private Socket socket;
	
	private boolean isHealthy;

	
	public DbaSocket(Socket socket) {
		this.socket = socket;
	}

	
	public boolean isHealthy() {
		return isHealthy;
	}

	
	public void setHealthy(boolean isHealthy) {
		this.isHealthy = isHealthy;
	}

	
	public Socket getSocket() {
		return socket;
	}

	
	public void dispose() {
		if (null != socket) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}
}
