package com.jhh.hdb.meta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class StringTemplateUtils {

	private static Map<String, String> kv_map = new HashMap<String, String>(); 
	static {

		String user_dir = System.getProperty("user.dir");
		String filename = user_dir + "\\stringtemplate.txt";

		File file = new File(filename);
		FileReader fr;
		StringBuilder sb = new StringBuilder();
		try {
			fr = new FileReader(file);
			fr.skip(0);
			BufferedReader br = new BufferedReader(fr);

			String line_str = null;
			while ((line_str = br.readLine()) != null) {
				sb.append(line_str + System.lineSeparator());
			}
			br.close();

			String str = sb.toString().trim();
			String[] str_arr = str.split(";;;;+");
			for (int i = 0; i < str_arr.length; i++) {
				String s = str_arr[i];
				if(s.matches("[.\\S\\s]*====+[.\\S\\s]*")){
					String[] kv = str_arr[i].split("====+");
					kv_map.put(kv[0].trim(), kv[1].trim());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String read_stat(String key) {
		return kv_map.get(key);
	}
}