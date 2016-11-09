package com.netease.backend.db.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class DdbVersionHelper {

	
	public enum DDBJAR {
		db, dba, master
	}

	
	public static String getJarVersion(DDBJAR ddbJar) {
		String verString = getJarVersionDetail(ddbJar);
		int index = verString.indexOf("-");
		if (index >= 0)
			verString = verString.substring(0, index);
		return verString;
	}

	
	public static String getJarVersionDetail(DDBJAR ddbJar) {
		InputStream is;
		BufferedReader r;
		String verString = "";
		String buildVerFileName = "/" + ddbJar.name() + "-build-ver.txt";
		try {
			is = DdbVersionHelper.class.getResourceAsStream(buildVerFileName);
			if (is == null)
				throw new IOException(buildVerFileName + " not found");
			r = new BufferedReader(new InputStreamReader(is));
			verString = r.readLine();
			r.close();
		} catch (IOException e) {
			return "";
		}
		int index = verString.indexOf("DDB");
		if (index >= 0)
			verString = verString.substring(4);
		return verString;
	}

}
