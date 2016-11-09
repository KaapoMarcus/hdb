package com.jhh.hdb.proxyserver.handlers.mysql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.response.HandshakeCmd;
import com.jhh.hdb.proxyserver.define.CharsetMappingTool;
import com.jhh.hdb.proxyserver.define.ServerCapabilities;
import com.jhh.hdb.proxyserver.define.ServerStatus;
import com.jhh.hdb.proxyserver.server.GlobalContext;
import com.jhh.hdb.proxyserver.session.SessionContext;
import com.jhh.hdb.proxyserver.session.SessionStatusType;

/**
 * �û����Ӹս���ʱ��������ʼ��һЩsession��Ϣ����handshake���͸�ͻ���
 * 
 *
 */
public class HandshakeHandler implements MySQLCmdHandler {
	public static final String SERVER_VERSION = "5.1.47";

	private final static char[] c = { '1', '2', '3', '4', '5', '6', '7', '8',
			'9', '0', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a',
			's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b',
			'n', 'm', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A',
			'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B',
			'N', 'M' };

	private HandshakeCmd handshakeCmd;

	private Random random = new Random();

	public MySQLCmd handleMessage(SessionContext sessionContext, MySQLCmd cmd) {
		HandshakeCmd tmp = (HandshakeCmd) handshakeCmd.clone();
		tmp.setThreadId(Thread.currentThread().hashCode());
		String passwordSeed = getRandomString(8);
		String restPasswordSeed = getRandomString(12);
		tmp.setScrambleBuff(passwordSeed);
		tmp.setRestScrambleBuff(restPasswordSeed);

		sessionContext.setSessionStatus(SessionStatusType.connected);
		sessionContext.setPasswordSeed(passwordSeed);
		sessionContext.setRestPasswordSeed(restPasswordSeed);
		sessionContext.updateServerStatus(ServerStatus.SERVER_STATUS_AUTOCOMMIT);

		return tmp;
	}

	public HandshakeHandler() {
		handshakeCmd = new HandshakeCmd();
		//���ְ���qs�������Ա��Ϊ0
		handshakeCmd.setNumber((byte) 0);
		handshakeCmd.setProtocolVersion((byte) 0x0a);
		InputStream is;
		BufferedReader r;
		String versionString;
		try {
			is = HandshakeHandler.class
					.getResourceAsStream("/db-build-ver.txt");
			if (is == null)
				throw new IOException("db-build-ver.txt not found");
			r = new BufferedReader(new InputStreamReader(is));
			versionString = r.readLine();
			r.close();
		} catch (final IOException e) {
			versionString = "version unknown";
		}

		handshakeCmd.setServerVersion(SERVER_VERSION + "-DDBQS-"
				+ versionString);
		handshakeCmd.setServerCapabilities((short) ServerCapabilities
				.getDefaultCapabilities(GlobalContext.getInstance()
						.getConfig()));
		handshakeCmd.setServerLanguage(CharsetMappingTool
				.getCharsetIndex(GlobalContext.getInstance().getConfig()
						.getCharset().name()));
		handshakeCmd.setServerStatus(ServerStatus.SERVER_STATUS_AUTOCOMMIT);
		handshakeCmd.setRestScrambleBuff("");
	}

	public String getRandomString(int size) {
		StringBuffer sb = new StringBuffer(size);
		synchronized (random) {
			for (int i = 0; i < size; i++) {
				sb.append(c[Math.abs(random.nextInt()) % c.length]);
			}
		}
		return sb.toString();
	}
}
