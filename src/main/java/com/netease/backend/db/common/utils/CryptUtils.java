package com.netease.backend.db.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import com.netease.backend.db.common.exceptions.CryptException;

public class CryptUtils {

	public static final String PROP_KEY = "key";
	public static final String KEY_PATH = "../conf/secret.key";
	private static final String Transform = "DESede";

	private static volatile Key defaultKey = null;

	
	public static Key getDefaultKey() {
		return defaultKey;
	}

	
	public static void setDefaultKey(Key defaultKey) {
		CryptUtils.defaultKey = defaultKey;
	}

	
	private static Key getKey() throws Exception {
		Key key = defaultKey;
		if (key == null) {
			ObjectInputStream keyIn = new ObjectInputStream(
					new FileInputStream(System.getProperty(PROP_KEY, KEY_PATH)));
			key = (Key) keyIn.readObject();
			keyIn.close();
			defaultKey = key;
		}
		return key;
	}

	
	public static Key getKey(String keyPath) throws Exception {
		
		
		
		
		

		URL url = null;
		File file = new File(keyPath);
		if (file.exists()) {
			url = file.toURL();
		} else {
			url = new URL(keyPath);
		}
		InputStream in = url.openStream();
		ObjectInputStream keyIn = new ObjectInputStream(in);
		Key key = (Key) keyIn.readObject();
		keyIn.close();
		in.close();
		return key;

	}

	
	public static byte[] encrypt(byte[] in) throws CryptException {
		try {
			Key key = getKey();
			int mode = Cipher.ENCRYPT_MODE;
			Cipher cipher = Cipher.getInstance(Transform);
			cipher.init(mode, key);

			return crypt(in, cipher);
		} catch (Exception e) {
			throw new CryptException(e);
		}
	}

	
	public static byte[] encrypt(byte[] in, Key key) throws CryptException {
		try {
			int mode = Cipher.ENCRYPT_MODE;
			Cipher cipher = Cipher.getInstance(Transform);
			cipher.init(mode, key);

			return crypt(in, cipher);
		} catch (Exception e) {
			throw new CryptException(e);
		}
	}

	
	public static byte[] decrypt(byte[] in, Key key) throws CryptException {
		try {
			int mode = Cipher.DECRYPT_MODE;
			Cipher cipher = Cipher.getInstance(Transform);
			cipher.init(mode, key);

			return crypt(in, cipher);
		} catch (Exception e) {
			throw new CryptException(e);
		}

	}

	
	public static byte[] decrypt(byte[] in) throws CryptException {
		try {
			Key key = getKey();
			int mode = Cipher.DECRYPT_MODE;
			Cipher cipher = Cipher.getInstance(Transform);
			cipher.init(mode, key);

			return crypt(in, cipher);
		} catch (Exception e) {
			throw new CryptException(e);
		}

	}

	
	private static byte[] crypt(byte[] in, Cipher cipher) throws IOException,
			GeneralSecurityException {
		int blockSize = cipher.getBlockSize();
		int outputSize = cipher.getOutputSize(blockSize);
		byte[] inBytes = new byte[blockSize];
		byte[] outBytes = new byte[outputSize];
		int outputLength = (in.length / outputSize + 1) * outputSize;
		byte[] out = new byte[outputLength];

		int offset = 0;
		int outOffset = 0;
		while ((in.length - offset) >= blockSize) {
			for (int i = 0; i < blockSize; i++)
				inBytes[i] = in[offset + i];
			int outLength = cipher.update(inBytes, 0, blockSize, outBytes);
			for (int i = 0; i < outLength; i++)
				out[outOffset + i] = outBytes[i];
			offset += blockSize;
			outOffset += outLength;
		}

		if ((in.length - offset) == 0)
			outBytes = cipher.doFinal();
		else {
			for (int i = 0; i < in.length - offset; i++)
				inBytes[i] = in[offset + i];
			outBytes = cipher.doFinal(inBytes, 0, in.length - offset);
		}

		for (int i = 0; i < outBytes.length; i++)
			out[outOffset + i] = outBytes[i];

		outOffset += outBytes.length;

		byte[] output = new byte[outOffset];
		for (int i = 0; i < outOffset; i++)
			output[i] = out[i];

		return output;

	}

	
	
	public static byte[] cryptMySQL(String password, String seed)
			throws CryptException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");

			byte[] passwordHashStage1 = md.digest(password.getBytes());
			md.reset();

			byte[] passwordHashStage2 = md.digest(passwordHashStage1);
			md.reset();

			md.update(seed.getBytes());
			md.update(passwordHashStage2);

			byte[] toBeXord = md.digest();

			int numToXor = toBeXord.length;

			for (int i = 0; i < numToXor; i++) {
				toBeXord[i] = (byte) (toBeXord[i] ^ passwordHashStage1[i]);
			}

			return toBeXord;
		} catch (Exception e) {
			throw new CryptException(e);
		}
	}
}
