
package com.netease.backend.db.common.management.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.netease.pool.Factory;
import com.netease.pool.Pool;


public class DbaSocketResFactory implements Factory<DbaSocketRes, DbaSocketCreateArg> {
	private static Logger logger = Logger.getLogger(DbaSocketResFactory.class);
	
	private static final int RETRY_INTERVAL = 500;
	
	public DbaSocketRes createResource(DbaSocketCreateArg arg, Pool<?, ?> pool)
	throws IOException {
		String masterID = arg.getMaster() + "_" + arg.getPort();
		
		logger.debug("[" + masterID + "] create dba socket.");
		Socket socket = null;
		int retry = arg.getRetryTimes();

		while (true) {
			try {
				if (retry <= 0) {
					break;
				}
				retry--;
				
				socket = new Socket();
				socket.connect(new InetSocketAddress(arg.getMaster(), arg.getPort()), arg.getConnectTimeout());
				int soTimeout = arg.getSoTimeOut();
				if (soTimeout > 0)
					socket.setSoTimeout(soTimeout);
				break;
			} catch (IOException ioe) {
				
				try {
					Thread.sleep(RETRY_INTERVAL);
				} catch (InterruptedException e) {
					logger.warn("��������ʱ����ȴ��̱߳�������ֹ��" + e.toString());
				}
			}
		}
		return new DbaSocketRes("", new DbaSocket(socket), pool);
	}
}
