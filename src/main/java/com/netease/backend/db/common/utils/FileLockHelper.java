package com.netease.backend.db.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;


public class FileLockHelper {

	
	public static FileLock tryLockExclusively(String dirToLock)
			throws IOException, OverlappingFileLockException {
		
		final File lockFile = new File(dirToLock + File.separator + ".lock");
		if (!lockFile.exists()) {
			lockFile.createNewFile();
		}

		
		final FileChannel fc = new RandomAccessFile(lockFile, "rw")
				.getChannel();
		
		final ByteBuffer bytes = ByteBuffer.allocate(12);

		
		final FileLock lock = fc.tryLock();
		if (lock == null) 
			
			return null;

		
		
		
		
		
		
		

		
		bytes.clear();
		
		bytes.putLong(System.currentTimeMillis());
		bytes.flip();

		
		fc.write(bytes);
		
		fc.force(false);

		
		
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					if (lock != null && lock.isValid()) {
						lock.release();
					}
					if (fc != null) {
						fc.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				
				lockFile.delete();
			}
		}));
		return lock;
		
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
