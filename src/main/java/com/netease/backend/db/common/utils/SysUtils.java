package com.netease.backend.db.common.utils;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;


public class SysUtils {
	
	private static File pidFile;
	
	
	public static void createPidFile(String path) throws Exception
	{
		if(path==null || path.trim().equals(""))
			throw new Exception("pid�ļ�·��Ϊ�գ�");
		
		path = path.trim();
		
		
		pidFile = new File(path);
		if(pidFile.exists())
			pidFile.delete();
		
		if(!pidFile.createNewFile())
			throw new Exception("����"+path+"ʧ�ܣ�");
	}
	
	
	public static void delPidFile() throws Exception
	{		
		
		
		if(pidFile != null && pidFile.exists())
			pidFile.delete();
		
	}

	
	
	public static  boolean fileExist(String path)
	{
		File file =  new File(path);
		return file.exists() && !file.isDirectory();
	}
	
	
	public static boolean dirExist(String path)
	{
		File dir =  new File(path);
		return dir.exists() && dir.isDirectory();
	}
	
	
	
	public static boolean cmdExist(String path)
	{
		BufferedReader br = null;
		String cmd = "which "+path;
		try {
			String cmdArray[] = {"/bin/bash", "-c", cmd};
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec(cmdArray);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while( br.readLine()!=null )
				;
			int ret = process.waitFor();
			br.close();
			return ret == 0;
		}catch(Exception e)
		{
			return false;
		}
	}
	
	
	
	public static void foldCompress(String foldPath, String targetFile, Logger logger) throws Exception
	{
		Process process;
		StreamGobbler outputGlobber = null;
		StreamGobbler errGlobber = null;
		
		
		int pos = foldPath.lastIndexOf(File.separator);
		String currentPath = foldPath.substring(0,pos);
		String foldName = foldPath.substring(pos+1, foldPath.length());
		
		
		try
		{
			String cmd = "tar --directory="+currentPath+
					" --exclude=*.tar --exclude=*.gz --exclude=*.log -cvzf "+targetFile+" "+foldName;
			String cmdArray[] = {"/bin/bash", "-c", cmd};
			if(logger != null)
     	logger.info("Input> "+cmd);
			process = Runtime.getRuntime().exec(cmdArray);
			outputGlobber = new StreamGobbler(process.getInputStream(),"Output",logger);
			errGlobber = new StreamGobbler(process.getErrorStream(),"Error",logger);
			outputGlobber.start();
			errGlobber.start();
			int ret = process.waitFor();
			outputGlobber.join();
			errGlobber.join();
			if(ret !=0)
				throw new Exception("ѹ������ʱ�������˳���errno="+ret);
		}catch(IOException ioe)
		{
			throw new Exception("ѹ������ʧ�ܡ�"+ioe.getMessage());
		}catch(InterruptedException ie)
		{
			throw new Exception("ѹ�����ݲ������жϡ�"+ie.getMessage());
		}
	}
	
	
	
	
	public static void executeCopyScript(String scriptPath, String dataPath, Logger logger) throws Exception
	{
		Process process;
		StreamGobbler outputGlobber = null;
		StreamGobbler errGlobber = null;
		try
		{
			if(!cmdExist(scriptPath))
				throw new Exception("���ݿ����ű�"+scriptPath+"�����ڡ�");
			String cmd = scriptPath+" "+dataPath;
			String cmdArray[] = {"/bin/bash", "-c", cmd};
			if(logger != null)
				logger.info("Input> "+cmd);
			process = Runtime.getRuntime().exec(cmdArray);
			outputGlobber = new StreamGobbler(process.getInputStream(),"Output",logger);
			errGlobber = new StreamGobbler(process.getErrorStream(),"Error",logger);
			outputGlobber.start();
			errGlobber.start();
			int ret = process.waitFor();
			outputGlobber.join();
			errGlobber.join();
			if(ret !=0)
				throw new Exception("ִ�нű�����ʧ�ܣ�errno="+ret);	
		}catch(IOException ioe)
		{
			throw new Exception("ִ�нű�����ʧ�ܣ�"+ioe.getMessage());
		}catch(InterruptedException ie)
		{
			throw new Exception("ִ�нű������жϣ�"+ie.getMessage());
		}
	}
	
	
	
	public static void deleteDir(File file) throws Exception
	{
		if(!file.exists())
			return;
		
		if(!file.isDirectory())
			return;
		
		File[] files = file.listFiles();
		
		for(int i=0;i<files.length;i++)
		{
			if(files[i].isDirectory())
				deleteDir(files[i]);
			else	if(!files[i].delete())
					throw new Exception("�޷�ɾ���ļ�"+files[i].getAbsolutePath());
		}
		if(!file.delete())
			throw new Exception("�޷�ɾ��Ŀ¼"+file.getAbsolutePath());
	}
	
	
	
	public static void delExpiredFolder(String path, int expiredDays) throws Exception
	{		
		File file = new File(path);
		if(!file.exists())
			throw new Exception("Ŀ¼"+path+"�����ڡ�");
		if(!file.isDirectory())
			throw new Exception(path+"����Ŀ¼��");
		
		long now = System.currentTimeMillis();
		long expiredTime = expiredDays * 24 * 60 * 60 * 1000;
		
		
		File[] files = file.listFiles();
		for(int i=0;i<files.length;i++)
		{
			long lastModifyTime = files[i].lastModified();
			if(now - lastModifyTime > expiredTime)
			{
				if(files[i].isDirectory())
					deleteDir(files[i]);
				else if(!files[i].delete())
					throw new Exception("�޷�ɾ���ļ�"+files[i].getAbsolutePath());
			}
		}
	}
	
	public static void main(String[] args)
	{
		File file = new File("D:/temp");
		try
		{
			deleteDir(file);
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
