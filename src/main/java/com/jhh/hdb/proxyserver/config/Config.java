package com.jhh.hdb.proxyserver.config;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 存储所有查询服务器用到的用户配置参数
 * 
 *
 */
public class Config {
	public static final String CONFIG_KEY = "QsConfigPath";
	public static final String DFAULT_CONF_FILE_PATH = "conf/QSServerConf.xml";

	// 服务器监听ip
	private String ip;

	// 服务器监听端口
	private int port;

	// BLOB参数的最大长度
	private int blobLength;

	private Charset charset;

	private SQLSupport sqlSupport;

	private int maxPstmtFetchSize;

	private boolean useStreamFetch;

	private int streamFetchSize = 0;

	private boolean timestampSkipNano;

	// 保存ddb名称到连接url的列表
	private Map<String, String> ddbMap;

	public Charset getCharset() {
		return charset;
	}

	public Map<String, String> getDdbMap() {
		return ddbMap;
	}

	public void setDdbMap(Map<String, String> ddbMap) {
		this.ddbMap = ddbMap;
	}

	public SQLSupport getSqlSupport() {
		return sqlSupport;
	}

	public void setSqlSupport(SQLSupport sqlSupport) {
		this.sqlSupport = sqlSupport;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBlobLength() {
		return blobLength;
	}

	public void setBlobLength(int blobLength) {
		this.blobLength = blobLength;
	}

	public int getMaxPstmtFetchSize() {
		return maxPstmtFetchSize;
	}

	public void setMaxPstmtFetchSize(int fetchSize) {
		this.maxPstmtFetchSize = fetchSize;
	}

	public boolean isUseStreamFetch() {
		return useStreamFetch;
	}

	public void setUseStreamFetch(boolean useStreamFetch) {
		this.useStreamFetch = useStreamFetch;
	}

	public int getStreamFetchSize() {
		return streamFetchSize;
	}

	public void setStreamFetchSize(int streamFetchSize) {
		this.streamFetchSize = streamFetchSize;
	}

	public boolean isTimestampSkipNano() {
		return timestampSkipNano;
	}

	public void setTimestampSkipNano(boolean timestampSkipNano) {
		this.timestampSkipNano = timestampSkipNano;
	}

	public Config() {
		this.sqlSupport = new SQLSupport();
		//采用jvm所设定的字符集
		String charsetName = System.getProperty("file.encoding");
		this.charset = Charset.forName(charsetName);
	}

	/**
	 * 从指定位置读取配置文件并更新到对象中
	 * @param filePath
	 * @throws Exception
	 */
	public void loadFromFile(String filePath) throws Exception {
		// 读取配置文件准备
		Document doc = null;
		try {
			final DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();

			final File file = new File(filePath);
			doc = db.parse(file);
			doc.normalize();

			updateServerConf(doc);
			updateSqlSupport(doc);
			if(useStreamFetch && streamFetchSize == 0){
				throw new Exception("开启流模式下每批发送记录数不能为0");
			}
		} catch (final Exception e) {
			throw e;
		}
	}

	/**
	 * 根据配置文件更新服务器配置
	 * @param doc
	 * @throws Exception
	 */
	private void updateServerConf(Document doc) throws Exception {
		Element serverNode = (Element) doc.getDocumentElement()
				.getElementsByTagName("server").item(0);

		NodeList ddbList = serverNode.getElementsByTagName("ddb");
		if (ddbList.getLength() > 0) {
			Map<String, String> ddbMap = new HashMap<String, String>(ddbList
					.getLength());
			for (int i = 0; i < ddbList.getLength(); i++) {
				Element ddbInfo = (Element) ddbList.item(i);
				Node nameNode = ddbInfo.getElementsByTagName("name").item(0);
				Node urlNode = ddbInfo.getElementsByTagName("url").item(0);
				String name = getNodeValue(nameNode);
				String url = getNodeValue(urlNode);
				ddbMap.put(name, url);
			}
			this.setDdbMap(ddbMap);
		} else {
			throw new Exception("no ddb is specified.");
		}

		NodeList list = serverNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element node = (Element) list.item(i);
			String nodeName = node.getNodeName();
			if ("ip".equals(nodeName)) {
				String ip = getNodeValue(node);
				if ("".equals(ip))
					throw new Exception("server binding ip is not specified.");
				this.setIp(ip);
			} else if ("port".equals(nodeName)) {
				try {
					this.setPort(Short.parseShort(getNodeValue(node)));
				} catch (NumberFormatException e) {
					throw new Exception("server binding port error: "
							+ e.getMessage());
				}
			} else if ("blob_length".equals(nodeName)) {
				try {
					this.setBlobLength(Integer.parseInt(getNodeValue(node)));
				} catch (NumberFormatException e) {
					throw new Exception("server blob_length error: "
							+ e.getMessage());
				}
			} else if ("max_pstmt_fetch_size".equals(nodeName)) {
				try {
					this.setMaxPstmtFetchSize(Integer
							.parseInt(getNodeValue(node)));
				} catch (NumberFormatException e) {
					throw new Exception("server max_pstmt_fetch_size error: "
							+ e.getMessage());
				}
			} else if ("stream_fetch_size".equals(nodeName)) {
				try {
					this.setStreamFetchSize(Integer
							.parseInt(getNodeValue(node)));
				} catch (NumberFormatException e) {
					throw new Exception("server stream_fetch_size error: "
							+ e.getMessage());
				}
			} else if ("use_stream_fetch".equals(nodeName)) {
				this.setUseStreamFetch(getNodeValue(node).equalsIgnoreCase(
						"true"));
			} else if ("timestamp_skip_nano".equals(nodeName)) {
				this.setTimestampSkipNano(getNodeValue(node).equalsIgnoreCase(
						"true"));
			}
		}
	}

	/**
	 * 根据配置文件更新特殊sql语句处理配置
	 * @param doc
	 * @throws Exception
	 */
	private void updateSqlSupport(Document doc) throws Exception {
		Element supportNode = (Element) doc.getDocumentElement()
				.getElementsByTagName("support").item(0);

		NodeList sqlList = supportNode.getElementsByTagName("sql");
		for (int i = 0; i < sqlList.getLength(); i++) {
			if (sqlList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element node = (Element) sqlList.item(i);
			String sqlStr = getNodeValue(node);
			if (sqlStr.length() < 1)
				throw new Exception("sql string should not be empty.");
			String typeStr = node.getAttribute("type");
			String behaviorStr = node.getAttribute("behavior");
			this.getSqlSupport().addCmd(
					SQLSupport.getBehaviorFromStr(behaviorStr),
					SQLSupport.getKeyWordTypeFromStr(typeStr), sqlStr);
		}
	}

	/**
	 * 读取节点值
	 * @param node
	 * @return
	 */
	private static String getNodeValue(Node node) {
		if (node.getFirstChild() == null)
			return "";
		final String value = node.getFirstChild().getNodeValue();
		if (value != null)
			return value.trim();
		else
			return "";
	}
}
