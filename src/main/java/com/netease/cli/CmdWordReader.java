package com.netease.cli;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;


public final class CmdWordReader {
	
	private String command;
	
	private MyStringReader sr;
	
	private StreamTokenizer tokenizer;

	
	public CmdWordReader(String command) {
		this.command = command;
		sr = new MyStringReader(command);
		tokenizer = new StreamTokenizer(sr);
		tokenizer.slashSlashComments(false);
		tokenizer.ordinaryChars('!', '~');
		tokenizer.wordChars('!', '~');
		tokenizer.quoteChar('"');
		tokenizer.quoteChar('\'');
	}

	
	public String next() {
		int prevPos = sr.getCurrentPos();
		try {
			int ttype = tokenizer.nextToken();
			if (ttype == StreamTokenizer.TT_EOF)
				return null;
			else if (ttype == StreamTokenizer.TT_NUMBER)
				return command.substring(prevPos, sr.getCurrentPos()).trim();
			else if (ttype == StreamTokenizer.TT_WORD)
				return tokenizer.sval;
			else if (ttype == '\'' || ttype == '"')
				return tokenizer.sval;
			else
				return "" + (char) ttype;
		} catch (IOException e) {
			return null;
		}
	}

	
	public String getRemain() {
		return command.substring(sr.getCurrentPos());
	}

	
	class MyStringReader extends Reader {
		
		private String str;
		
		private int length;
		
		private int next = 0;

		MyStringReader(String s) {
			str = s;
			length = s.length();
		}

		@Override
		public void close() throws IOException {
			str = null;
		}

		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			if ((off < 0) || (off > cbuf.length) || (len < 0)
					|| ((off + len) > cbuf.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return 0;
			}
			if (next >= length)
				return -1;
			int n = Math.min(length - next, len);
			str.getChars(next, next + n, cbuf, off);
			next += n;
			return n;
		}

		public int getCurrentPos() {
			return next;
		}
	}
}
