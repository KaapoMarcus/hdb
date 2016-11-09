package com.netease.backend.db.common.cmd;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.netease.backend.db.common.exceptions.CryptException;
import com.netease.backend.db.common.utils.CryptUtils;
import com.netease.backend.db.common.utils.Pair;


public class CmdConnection {

	private final ObjectInputStream commandIn;
	private final ObjectOutputStream commandOut;
	private final Socket sock;

	private boolean closed = false;

	public ObjectInputStream getCommandIn() {
		return commandIn;
	}

	public ObjectOutputStream getCommandOut() {
		return commandOut;
	}

	public Socket getSock() {
		return sock;
	}

	public CmdConnection(ObjectInputStream cin, ObjectOutputStream cout,
			Socket sock) {
		if ((cin == null) || (cout == null))
			throw new NullPointerException();
		this.commandIn = cin;
		this.commandOut = cout;
		this.sock = sock;
	}

	
	public CmdConnection(Socket sock) throws IOException {
		
		
		
		final ObjectOutputStream cout = new ObjectOutputStream(
				new BufferedOutputStream(sock.getOutputStream()));
		cout.flush();

		final ObjectInputStream cin = new ObjectInputStream(sock
				.getInputStream());

		if ((cin == null) || (cout == null))
			throw new NullPointerException();
		this.commandIn = cin;
		this.commandOut = cout;
		this.sock = sock;

	}

	public boolean isClosed() {
		return closed;
	}

	
	public void close() throws IOException {
		try {
			commandIn.close();
			commandOut.close();
		} finally {
			try {
				sock.close();
				closed = true;
			} catch (IOException e) {
				
			}
		}
	}

	
	public String readString(boolean crypted) throws IOException {
		if (!crypted)
			return this.readChars();
		else
			return new String(this.readBytes(true), "UTF-8");
	}

	
	public String readChars() throws IOException {
		try {
			final ObjectInputStream in = this.getCommandIn();

			final StringBuilder ss = new StringBuilder();

			int i = -1;
			for (;;) {
				final char c = in.readChar();
				if (c == 0x0) {
					break;
				}
				ss.append(c);
				i++;
			}

			return ss.toString();
		} catch (IOException e) {
			close();
			throw e;
		}

	}

	
	public boolean readBoolean() throws IOException {
		return (this.readByte() != 0) ? true : false;
	}

	
	public char readChar() throws IOException {
		try {
			return getCommandIn().readChar();
		} catch (IOException e) {
			close();
			throw e;
		}

	}

	
	public int readCommand() throws IOException {
		return this.readInt();
	}

	
	public int readInt() throws IOException {
		try {
			final ObjectInputStream in = this.getCommandIn();

			return in.readInt();
		} catch (IOException e) {
			close();
			throw e;
		}

	}

	public long readLong() throws IOException {
		try {
			final ObjectInputStream in = this.getCommandIn();

			return in.readLong();
		} catch (IOException e) {
			close();
			throw e;
		}

	}

	
	public byte[] readBytes(boolean crypted) throws IOException {
		final byte[] b = this.readBytes();
		if (crypted && (b.length > 0)) {
			try {
				return CryptUtils.decrypt(b);
			} catch (final Exception ex) {
				throw new IOException("Encrypting stream error: "
						+ ex.getMessage());
			}
		}
		return b;
	}

	
	public byte[] readBytes() throws IOException {
		try {
			final ObjectInputStream in = this.getCommandIn();

			final int len = in.readInt();
			if (len < 0)
				throw new IOException(
						"Protocol error: no byte array length header");
			final byte[] b = new byte[len];
			for (int i = 0; i < len; i++) {
				b[i] = in.readByte();
			}
			return b;
		} catch (IOException e) {
			close();
			throw e;
		}

	}

	
	public byte readByte() throws IOException {
		try {
			final ObjectInputStream in = this.getCommandIn();

			return in.readByte();
		} catch (IOException e) {
			close();
			throw e;
		}

	}

	
	@SuppressWarnings("unchecked")
	public <T> T read() throws IOException {
		try {
			final ObjectInputStream in = this.getCommandIn();

			return (T) this.read(in);
		} catch (IOException e) {
			close();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T read(ObjectInputStream in) throws IOException {
		try {
			return (T) (in.readObject());
		} catch (final ClassNotFoundException ex) {
			throw new IOException("Deserialize error: " + ex.getMessage());
		} catch (final ClassCastException ex) {
			throw new IOException("Deserialize error: " + ex.getMessage());
		}
	}

	
	@SuppressWarnings("unchecked")
	public <T> T read(boolean compressed) throws IOException {
		if (compressed) {
			final byte[] b = this.readBytes();
			final ByteArrayInputStream bInput = new ByteArrayInputStream(b);
			GZIPInputStream gzipInput = null;
			ObjectInputStream objectInput = null;
			try {
				gzipInput = new GZIPInputStream(bInput);
				objectInput = new ObjectInputStream(gzipInput);
				return (T) this.read(objectInput);
			} finally {
				if (objectInput != null) {
					objectInput.close();
				}
				if (gzipInput != null) {
					gzipInput.close();
				}
				bInput.close();
			}
		} else
			return (T) this.read();
	}

	
	public void write(Object obj) throws IOException {
		final ObjectOutputStream out = this.getCommandOut();

		try {
			out.writeObject(obj);
		} catch (IOException e) {
			close();
			throw e;
		}

		out.flush();
	}

	
	public void write(Object obj, boolean compressed) throws IOException {
		if (compressed) {
			final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			GZIPOutputStream gzipOutput = null;
			ObjectOutputStream objOut = null;
			try {
				gzipOutput = new GZIPOutputStream(bOut);
				objOut = new ObjectOutputStream(gzipOutput);
				objOut.writeObject(obj);
			} finally {
				if (objOut != null) {
					objOut.close();
				}
				if (gzipOutput != null) {
					gzipOutput.close();
				}
				bOut.close();
			}

			this.writeBytes(bOut.toByteArray());
		} else {
			this.write(obj);
		}
	}

	
	public void writeBoolean(boolean b) throws IOException {
		this.writeByte(b ? (byte) 1 : (byte) 0);
	}

	
	public void writeCommand(int code) throws IOException {
		this.writeInt(code);
	}

	
	public void writeEnd() throws IOException {
		this.writeCommand(CmdCode.REQ_END);
	}

	
	public void writeCommand(int code, String info) throws IOException {
		this.writeCommand(code);
		if ((info != null) && (info.length() != 0)) {
			this.writeChars(info);
		}
	}

	
	public void writeInt(int v) throws IOException {
		final ObjectOutputStream out = this.getCommandOut();
		try {
			out.writeInt(v);
		} catch (IOException e) {
			close();
			throw e;
		}

		out.flush();
	}

	public void writeLong(long v) throws IOException {
		final ObjectOutputStream out = this.getCommandOut();
		try {
			out.writeLong(v);
		} catch (IOException e) {
			close();
			throw e;
		}

		out.flush();
	}

	
	public void writeByte(byte b) throws IOException {
		final ObjectOutputStream out = this.getCommandOut();
		try {
			out.writeByte(b);
		} catch (IOException e) {
			close();
			throw e;
		}

		out.flush();
	}

	
	public void writeBytes(byte[] b) throws IOException {
		final ObjectOutputStream out = this.getCommandOut();
		try {
			if ((b == null) || (b.length == 0)) {
				out.writeInt(0);
			} else {
				out.writeInt(b.length);
				for (final byte element : b) {
					out.writeByte(element);
				}
			}
		} catch (IOException e) {
			close();
			throw e;
		}

		out.flush();
	}

	
	public void writeBytes(byte[] b, boolean crypt) throws IOException {
		if (crypt) {
			try {
				b = CryptUtils.encrypt(b);
			} catch (final Exception e) {
				throw new IOException("Decrypting stream error: "
						+ e.getMessage());
			}
		}
		this.writeBytes(b);
	}

	
	public void writeChars(String s) throws IOException {
		final ObjectOutputStream out = this.getCommandOut();
		try {
			out.writeChars(s);
			out.writeChar(0x0);
		} catch (IOException e) {
			close();
			throw e;
		}

		out.flush();
	}

	
	public void writeString(String s, boolean crypted) throws IOException {
		if (!crypted) {
			this.writeChars(s);
		} else {
			this.writeBytes(s.getBytes("UTF-8"), true);
		}
	}

	public void flush() throws IOException {
		try {
			getCommandOut().flush();
		} catch (IOException e) {
			close();
			throw e;
		}
	}

	
	public Pair<Integer, String> request(int code) throws IOException {
		this.writeCommand(code);
		return this.getRetCode();
	}

	
	public void confirm() throws IOException {
		this.writeCommand(DDBCode.OK);
	}

	
	public void err(int code, String info) throws IOException {
		if (info == null) {
			info = "";
		}
		this.writeCommand(code, info);
	}

	
	public Pair<Integer, String> getRetCode() throws IOException {
		final int c = this.readCommand();
		if (c != DDBCode.OK)
			return new Pair<Integer, String>(c, this.readChars());

		return null;
	}

	
	public boolean getIfConfirmed() throws IOException {
		final int c = this.readCommand();
		return c == DDBCode.OK;
	}

}
