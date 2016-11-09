package com.netease.backend.db.common.cmd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.netease.backend.db.common.definition.Msg;
import com.netease.backend.db.common.management.DDBURL;
import com.netease.backend.db.common.utils.CryptUtils;
import com.netease.backend.db.common.utils.Pair;


public class DDBCommander implements Cloneable {

	protected static void setCryptKeyPath(
		String keyPath)
	{
		try {
			CryptUtils.setDefaultKey(CryptUtils.getKey(keyPath));
		} catch (final Exception ex) {
			throw new RuntimeException("En/Decrypting KEY error", ex);
		}
	}

	
	
	
	
	
	
	
	
	

	
	public static CmdConnection createConnection(
		DDBURL url)
		throws DDBCmdException, IOException
	{
		return connect(url, DEFAULT_SO_TIMEOUT);
	}

	public static void closeConnection(
		CmdConnection conn)
	{
		try {
			conn.close();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}

	protected static void confirm(
		CmdConnection conn)
		throws DDBCmdException, IOException
	{
		conn.confirm();
	}

	protected static void checkResponseCode(
		CmdConnection conn)
		throws DDBCmdException, IOException
	{
		final Pair<Integer, String> c = conn.getRetCode();
		if (c != null)
			throw new DDBCmdException(c.getFirst(), c.getSecond());
	}

	protected static void request(
		CmdConnection conn, int code)
		throws DDBCmdException, IOException
	{
		final Pair<Integer, String> c = conn.request(code);
		if (c != null)
			throw new DDBCmdException(c.getFirst(), c.getSecond());
	}

	
	public static void endConn(
		CmdConnection conn)
	{
		if (!conn.isClosed()) {
			
			try {
				conn.writeEnd();
			} catch (final IOException ex) {
				 ex.printStackTrace();
			} finally {
				
				try {
					conn.close();
				} catch (final IOException ignored) {
				}
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	private static final int	DEFAULT_CONN_RETRIES		= 3 ;
	
	private static final int	DEFAULT_CONN_RETRY_DELAY	= 1000 ;
	
	private static final int	DEFAULT_CONN_TIMEOUT		= 5000 ;
	
	private static final int	DEFAULT_SO_TIMEOUT			= 30000 ;

	
	public static CmdConnection connect(
		DDBURL url, int soTimeout)
		throws DDBCmdException, IOException
	{
		
		final Socket sock = connectAction(url.getHost(), url.getPort(),
				DEFAULT_CONN_RETRIES);
		
		final CmdRequest req = new CmdRequest(sock);
		req.setTimeout((soTimeout > 0) ? soTimeout : DEFAULT_CONN_TIMEOUT);
		
		final CmdConnection conn = req.getConnection();
		
		final Map<String, Object> ret = onSockConnected(conn);
		final String localHost = (String) ret.get("localHost");
		final String ddbName = (String) ret.get("ddbName");
		url.setName(ddbName);
		url.getParams().setProperty("localHost", localHost);
		return conn;
	}

	
	private static Map<String, Object> onSockConnected(
		CmdConnection conn)
		throws DDBCmdException, IOException
	{
		
		conn.writeChars(Msg.MSG_REQ_NEW);
		checkResponseCode(conn);

		request(conn, CmdCode.REQ_CONN);
		checkResponseCode(conn);
		
		final String localHost = conn.readChars();
		final String ddbName = conn.readChars();
		confirm(conn);
		final Map<String, Object> ret = new HashMap<String, Object>(2);
		ret.put("localHost", localHost);
		ret.put("ddbName", ddbName);
		return Collections.unmodifiableMap(ret);
	}

	
	private static Socket connectAction(
		String host, int port, int retries)
		throws IOException
	{
		TRYCONN: for (;;) {
			final InetSocketAddress addr = new InetSocketAddress(host, port);
			try {
				final Socket sock = new Socket(); 
				sock.connect(addr, DEFAULT_CONN_TIMEOUT);
				return sock;
			} catch (final IOException ex) {
				if (retries <= 0)
					throw ex;
				
				synchronized (addr) {
					try {
						addr.wait(DEFAULT_CONN_RETRY_DELAY);
					} catch (final InterruptedException e) {
						continue TRYCONN; 
					}
				}
				
				final String ip;
				if ((addr.getAddress() == null)
						&& ((ip = resolveHost(host)) != null)) {
					host = ip;
				}
			} finally {
				retries--;
			}
		}
	}

	private static final Logger	LOGGER;
	static {
		LOGGER = Logger.getLogger(DDBCommander.class);
	}

	
	private static final String	DNSFILE_WINXP	= "C:/WINDOWS/system32/drivers/etc/hosts";
	
	private static final String	DNSFILE_LINUX	= "/etc/hosts";

	
	private static String resolveHost(
		String name)
	{
		FileInputStream in = null;
		BufferedReader reader = null;
		String dnsFile;
		final String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("windows")) {
			dnsFile = DNSFILE_WINXP;
		} else if (osName.contains("linux")) {
			dnsFile = DNSFILE_LINUX;
		} else {
			dnsFile = DNSFILE_LINUX;
		}

		try {
			
			try {
				in = new FileInputStream(dnsFile);

			} catch (final FileNotFoundException we) {
				LOGGER.warn("hosts file '" + dnsFile + "' cannot be found.");
				return null;
			}

			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (line != null) {
				line = line.trim();
				if (name.equals(line.substring(line.lastIndexOf(' ') + 1)))
					return line.substring(0, line.indexOf(' '));
				line = reader.readLine();
			}
			return null;
		} catch (final Exception e) {
			LOGGER.error("Read hosts file '" + dnsFile + "' failed: "
					+ e.getMessage());
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

}
