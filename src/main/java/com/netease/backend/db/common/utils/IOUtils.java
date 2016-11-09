package com.netease.backend.db.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.netease.backend.db.common.definition.Msg;


public class IOUtils {
	
	
	public static String readString(ObjectInputStream in) throws IOException
	{
		String msg = "";
		
		int length = in.readInt();
		if(length > 0)
		{
			char[] chars = new char[length];
			for(int i=0;i<length;i++)
				chars[i] = in.readChar();
			msg = new String(chars);
		}
		return msg;
	}
	
	
	
	public static byte[] readBytes(ObjectInputStream in) throws IOException
	{
		byte[] msg = null;
		int length = in.readInt();
		if(length>0)
		{
			msg = new byte[length];
			for(int i=0;i<length;i++)
				msg[i]=in.readByte();
		}
		return msg;
	}
	
	
	public static String readString(ObjectInputStream in, int length) throws IOException
	{
		String msg = "";
		
		if(length > 0)
		{
			char[] chars = new char[length];
			for(int i=0;i<length;i++)
				chars[i] = in.readChar();
			msg = new String(chars);
		}
		return msg;
	}
	
	
	
	public static String readCmd(ObjectInputStream in) throws IOException
	{
		return readString(in, Msg.MSG_LENGTH);
	}
	
	
	
	public static void writeString(ObjectOutputStream out, String msg) throws IOException
	{
		if(msg == null || msg.length() == 0)
			out.writeInt(0);
		else
		{
			out.writeInt(msg.length());
			out.writeChars(msg);
		}
	}
	
	
	public static void writeBytes(ObjectOutputStream out, byte[] msg) throws IOException
	{
		if(msg == null || msg.length == 0)
			out.writeInt(0);
		else 
		{
			out.writeInt(msg.length);
			for(int i=0;i<msg.length;i++)
				out.writeByte(msg[i]);
		}
	}
	
	
	
	public static void writeCompressedObject(ObjectOutputStream out, Object obj) throws IOException
	{
		ByteArrayOutputStream bOutput = null;
		GZIPOutputStream gzipOutput = null;
		ObjectOutputStream objectOutput = null;
		try
		{
			bOutput = new ByteArrayOutputStream();
			gzipOutput = new GZIPOutputStream(bOutput);
			objectOutput = new ObjectOutputStream(gzipOutput);
			objectOutput.writeObject(obj);
		}finally
		{
			if(objectOutput != null) objectOutput.close();
			if(gzipOutput != null) gzipOutput.close();
			if(bOutput != null) bOutput.close();
		}
		out.writeInt(bOutput.size());
		bOutput.writeTo(out);
	}

	
	public static Object readCompressedObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream bInput = null;
		GZIPInputStream gzipInput = null;
		ObjectInputStream objectInput = null;
		
		try
		{
			int size = in.readInt();
			byte[] buffer = new byte[size];
			in.readFully(buffer);
			bInput = new ByteArrayInputStream(buffer);
			gzipInput = new GZIPInputStream(bInput);
			objectInput = new ObjectInputStream(gzipInput);
			return objectInput.readObject();
		}finally
		{
			if(objectInput != null) objectInput.close();
			if(gzipInput != null) gzipInput.close();
			if(bInput != null) bInput.close();
		}
	}
	
	
	public static void writeObjByteArray(ObjectOutputStream out, Object obj) throws IOException
	{
		ByteArrayOutputStream bOutput = null;
		ObjectOutputStream objectOutput = null;
		try
		{
			bOutput = new ByteArrayOutputStream();
			objectOutput = new ObjectOutputStream(bOutput);
			objectOutput.writeObject(obj);
		}finally
		{
			if(objectOutput != null) objectOutput.close();
			if(bOutput != null) bOutput.close();
		}
		out.writeInt(bOutput.size());
		bOutput.writeTo(out);
	}

	
	public static Object readObjByteArray(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream bInput = null;
		ObjectInputStream objectInput = null;
		
		try
		{
			int size = in.readInt();
			byte[] buffer = new byte[size];
			in.readFully(buffer);
			bInput = new ByteArrayInputStream(buffer);
			objectInput = new ObjectInputStream(bInput);
			return objectInput.readObject();
		}finally
		{
			if(objectInput != null) objectInput.close();
			if(bInput != null) bInput.close();
		}
	}
}
