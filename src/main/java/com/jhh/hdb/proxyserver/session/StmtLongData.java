package com.jhh.hdb.proxyserver.session;

import java.util.LinkedList;
import java.util.List;

/**
 * 用于存放命令COM_LONG_DATA发送过来的值
 * 
 *
 */
public class StmtLongData {

	private List<byte[]> dataList = new LinkedList<byte[]>();

	private int totalLength = 0;

	public byte[] convert2Array() {
		byte[] ret = new byte[totalLength];
		int index = 0;
		for (byte[] data : dataList) {
			System.arraycopy(data, 0, ret, index, data.length);
			index += data.length;
		}
		return ret;
	}

	public void append(byte[] data) {
		if (data == null)
			return;
		
		dataList.add(data);
		totalLength += data.length;
	}

	public int getTotalLength() {
		return totalLength;
	}

}
