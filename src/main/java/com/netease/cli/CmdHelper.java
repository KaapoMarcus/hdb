package com.netease.cli;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Vector;


public class CmdHelper {
	
	public static String readCommand(Reader in, String delimiter, char quoter) throws IOException {
		char[] endChars = delimiter.toCharArray();
		int endNChars = endChars.length;
		int endMatched = 0;
		char[] buf = new char[32];
		int size = buf.length;
		boolean inquote = false;
		int n = 0;
		boolean inEscape = false;
		while (true) {
			int c = in.read();
			if (c < 0) {
				
				int i;
				for (i = n - 1; i >= 0; i--) {
					if (!Character.isWhitespace(buf[i]))
						break;
				}
				if (i < 0)	
					return null;
				break;
			}
			if (n >= size - 1) {
				char[] newBuf = new char[size * 2];
				System.arraycopy(buf, 0, newBuf, 0, size);
				size *= 2;
				buf = newBuf;
			}
			buf[n] = (char) c;
			if (c == quoter && !inEscape)	
				inquote = !inquote;
			n++;
			if (inEscape)
				inEscape = false;
			else if (c == '\\')
				inEscape = true;
			if (!inquote && !inEscape) {
				if (c == endChars[endMatched]) {
					endMatched++;
					if (endMatched == endNChars)	
							break;
				} else
					endMatched = 0;
			} else
				endMatched = 0;
		}
		return new String(buf, 0, n - endMatched);
	}

	
	public static String[] splitCommand(String cmd) {
		CmdWordReader wr = new CmdWordReader(cmd);
		Vector<String> v = new Vector<String>();
		String word;
		while ((word = wr.next()) != null)
			v.add(word);
		String[] a = new String[v.size()];
		v.toArray(a);
		return a;
	}

	
	public static boolean isCommand(String cmd, String expect) {
		return isCommand(cmd, expect, null, -1, true);	
	}
	
	
	public static boolean isCommand(String cmd, String expect,
			List<String> args, int expectedArgsNumber, boolean ignoreCase) {
		String[] a1 = splitCommand(cmd);
		String[] a2 = splitCommand(expect);
		if (a1.length < a2.length)
			return false;
		if (ignoreCase) {
			for (int i = 0; i < a2.length; i++) 
				if (!a2[i].equalsIgnoreCase(a1[i]))
					return false;
		} else {
			for (int i = 0; i < a2.length; i++) 
				if (!a2[i].equals(a1[i]))
					return false;
		}
		
		if (expectedArgsNumber >= 0 && a1.length - a2.length != expectedArgsNumber)
			return false;
		if (expectedArgsNumber < 0)
			return true;
		if (args == null)
			return true;
		args.clear();
		for (int i = a2.length; i < a1.length; i++)
			args.add(a1[i]);
		return true;
	}

}
