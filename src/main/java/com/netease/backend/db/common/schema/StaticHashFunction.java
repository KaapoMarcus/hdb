package com.netease.backend.db.common.schema;

import java.util.HashMap;


public enum StaticHashFunction {
	
	
	DBHash {
		public String getStaticHashFunctionName() {
			return "com.netease.backend.db.common.schema.DBHash";
		}
		
		public String toString() {
			return "DBHash";
		}
	},
	
	DBStringHash {
		public String getStaticHashFunctionName() {
			return "com.netease.backend.db.common.schema.DBStringHash";
		}
		
		public String toString() {
			return "DBStringHash";
		}
	};
	
	
	private static final HashMap<String, String> staticHashFunctionMap = new HashMap<String, String>();
	static {
		staticHashFunctionMap.put(DBHash.name(), DBHash.getStaticHashFunctionName());
		staticHashFunctionMap.put(DBStringHash.name(), DBStringHash.getStaticHashFunctionName());
	}
	
	
	public static String getStaticHashFunctionName(String name) {
		return staticHashFunctionMap.get(name);
	}
	
	
	abstract public String getStaticHashFunctionName();
}
