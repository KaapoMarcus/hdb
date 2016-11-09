package com.netease.cli;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.gnu.readline.Readline;
import org.gnu.readline.ReadlineCompleter;
import org.gnu.readline.ReadlineLibrary;


public final class ConsoleHelper {
	
	private static boolean readlineInitialized = false;
	
	private static boolean readlineEnabled = false;
	
	private static boolean shutdown = false;

	
	public static void initReadline() {
		if (!readlineInitialized) {
			readlineInitialized = true;
			String osName = System.getProperty("os.name").toLowerCase();
			if (osName.contains("windows"))
				return;
			try {
				Readline.load(ReadlineLibrary.GnuReadline);
				Readline.initReadline("ddb");
				try {
					Readline.setWordBreakCharacters(Readline.getWordBreakCharacters() + ".");
				} catch (UnsupportedEncodingException e) {
				}
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						shutdown = true;
						Readline.cleanup();
					}
				});
				readlineEnabled = true;
			} catch (UnsatisfiedLinkError e) {
				System.out.println("ע�⣺��Ļ�����û�а�װlibreadline-java��������������ʷ�ȹ��ܽ�����ʹ�á�"
					+ "����ϵ����ϵͳ����Ա��װlibreadline-java�������䶯̬���ӿ�����Ŀ¼�ӵ�java.library.path�У�"
					+ "�Ի�ø��õ������в������顣");
			}
		}
	}

	
	public static void setReadlineCompleter(ReadlineCompleter rlc) {
		Readline.setCompleter(rlc);
	}

	
	public static String askForOneString(String question, String defaultValue) throws IOException {
		System.out.print(question);
		if (defaultValue != null)
			System.out.print("[" + defaultValue + "]");
		System.out.println(":");
		return safeReadLine(" ", defaultValue);
	}

	
	public static String safeReadLine(String prompt, String defaultValue) throws IOException {
		if(shutdown)
			throw new IOException("console has been shutdown");
		initReadline();
		String value = null;
		while (true) {
			if (prompt == null || prompt.length() == 0)
				prompt = " ";
			value = Readline.readline(prompt);
			if (value == null || value.length() == 0)
				value = defaultValue;
			if (value != null) {
				
				
				if (readlineEnabled) {
					byte[] bytes = new byte[value.length()];
					for (int i = 0; i < bytes.length; i++)
						bytes[i] = (byte) value.charAt(i);
					value = new String(bytes);
				}
				break;
			}
		}
		return value;
	}

	
	public static boolean askForYesNo(String question, boolean defaultValue) throws IOException {
		while (true) {
			String r = askForOneString(question, defaultValue ? "yes" : "no");
			if (r.equalsIgnoreCase("yes") || r.equalsIgnoreCase("y"))
				return true;
			else if (r.equalsIgnoreCase("no") || r.equalsIgnoreCase("n"))
				return false;
			else
				System.out.println("Invalid value, must be yes/no/y/n!");
		}
	}

	
	public static int askForOneInteger(String question, Integer defaultValue) throws IOException {
		while (true) {
			System.out.print(question);
			if (defaultValue != null)
				System.out.print("[" + defaultValue + "]");
			System.out.println(":");
			String s = safeReadLine(" ", "" + defaultValue);
			try {
				int v = Integer.parseInt(s);
				return v;
			} catch (NumberFormatException e) {
				System.out.println("����ȷ�����룬����Ϊ���͡�����������");
			}
		}
	}

	
	public static Object askForChoice(String question, Integer defaultValue, String[] choice,
		Object[] values) throws IOException {
		if (choice.length != values.length)
			throw new IllegalArgumentException("ѡ����ֵ�����С��һ��");
		System.out.print(question);
		if (defaultValue != null)
			System.out.print("[" + defaultValue + "]");
		System.out.println(":");
		for (int i = 0; i < choice.length; i++) {
			System.out.println("  " + (i + 1) + ": " + choice[i]);
		}
		while (true) {
			String s = safeReadLine(" ", "" + defaultValue);
			try {
				int c = Integer.parseInt(s);
				if (c <= 0 || c > choice.length) {
					System.out.println("����ȷ�����룬����Ϊ[1-" + choice.length + "]֮�������");
					continue;
				}
				return values[c - 1];
			} catch (NumberFormatException e) {
				System.out.println("����ȷ�����룬����Ϊ���͡�����������");
			}
		}
	}

	
	public static String readCommandFromConsole(String prompt, String end) throws IOException {
		String cmd = "";
		String thisPrompt = prompt;
		while (true) {
			String line = safeReadLine(thisPrompt, null);
			if (thisPrompt == prompt) {
				thisPrompt = "";
				for (int i = 0; i < prompt.length(); i++)
					thisPrompt += " ";
			}
			line = line.trim();
			if (line.endsWith(end)) {
				cmd = cmd + " " + line.substring(0, line.length() - end.length());
				break;
			} else if (end.equals("\n")) {
				cmd = cmd + line;
				break;
			} else
				cmd = cmd + " " + line;
		}
		return cmd;
	}

	
	public static String readSQL(Reader in, String end, char quoter) throws IOException {
		if (end.length() > 2)
			throw new IllegalArgumentException("���ݳ��������ַ��Ľ������ָ��㷨��δʵ��");
		char[] endChars = end.toCharArray();
		int endNChars = endChars.length;
		int endMatched = 0;
		char[] buf = new char[32];
		int size = buf.length;
		boolean inquote = false;
		int n = 0;
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
			if (c == quoter) {
				
				if (n > 0 && buf[n - 1] == '\\') {
					boolean isRealSlash = true;
					int j = n - 2;
					while (j >= 0 && buf[j] == '\\') {
						isRealSlash = !isRealSlash;
						j--;
					}
					if (!isRealSlash)
						inquote = !inquote;
				} else
					inquote = !inquote;
			}
			n++;
			if (!inquote) {
				if (c == endChars[endMatched]) {
					endMatched++;
					if (endMatched == endNChars) { 
						if (!(n > endNChars && buf[n - endNChars - 1] == '\\'))
							break;
						else {
							endMatched = 0;
							inquote = true;
						}
					}
				} else
					endMatched = 0;
			} else
				endMatched = 0;
		}
		if (n == 0)
			return null;
		else
			return new String(buf, 0, n - endNChars);
	}

	public static boolean isShutdown() {
		return shutdown;
	}

	
}
