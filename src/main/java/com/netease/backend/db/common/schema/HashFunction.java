package com.netease.backend.db.common.schema;

import java.io.Serializable;


public class HashFunction implements Serializable, Cloneable {
	private static final long serialVersionUID = 3563792291819790770L;

	
	public static final byte TYPE_LONG = 0;
	
	
	public static final byte TYPE_STRING = 1;
	
    
    public static final byte TYPE_LIST = 2;
    
	
	public static final String DEFAULT_PACKAGE = "com.netease.backend.db.common.myhash";
	
	
	String name;

	
	long timeStamp;

	
	byte argType;

	
	String comment;

	
	String code = null;

	
	byte[] classBytes = null;

	
	public HashFunction(String name, int argType, String code, String comment) {
		this.name = name;
		if (argType > 2)
			throw new IllegalArgumentException("argType incorrect!");
		this.argType = (byte) argType;
		this.code = code;
		this.comment = comment;
	}
	
	
	public HashFunction(String name, long timeStamp, int argType, String code, byte[] classBytes, String comment) {
		this.name = name;
		this.timeStamp = timeStamp;
		if (argType > 2)
			throw new IllegalArgumentException("argType incorrect!");
		this.argType = (byte) argType;
		this.code = code;
		this.classBytes = classBytes;
		this.comment = comment;
	}
	
	
	
	public String getClassName() {
		return DEFAULT_PACKAGE + "." + name + "_" + timeStamp;
	}
	
	
	public String getSimpleName() {
		return name + "_" + timeStamp;
	}

	
	public String getName() {
		return name;
	}
	
	public String getCompleteCode() {
		if (null == code || code.length() == 0)
			return null;

		StringBuilder codeSb = new StringBuilder();
		codeSb.append("package ");
		codeSb.append(DEFAULT_PACKAGE);
		codeSb.append("; \n");
		codeSb.append("import java.util.List; \n");
		codeSb.append("import com.netease.backend.db.common.schema.Hash; \n");
		codeSb.append("public class ");
		codeSb.append(name);
		codeSb.append("_");
		codeSb.append(timeStamp);
		codeSb.append(" extends Hash { \n");
		codeSb.append(code);
		codeSb.append("\n}");
		
		return codeSb.toString();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public byte getArgType() {
		return argType;
	}

	public void setArgType(byte argType) {
		this.argType = argType;
	}
	
	
	public String getArgTypeDesc() {
		if (argType == TYPE_LONG)
			return "long";
		else if (argType == TYPE_STRING)
			return "String";
		else if (argType == TYPE_LIST)
			return "List<Object>";
		return "";
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public byte[] getClassBytes() {
		return classBytes;
	}

	public void setClassBytes(byte[] classBytes) {
		this.classBytes = classBytes;
	}
	
	public HashFunction clone() {
		try {
			return (HashFunction) super.clone();
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
