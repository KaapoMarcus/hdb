package com.netease.backend.db.common.utils;

import static com.netease.backend.db.common.utils.ByteUtils.b2iBig64;
import static com.netease.backend.db.common.utils.ByteUtils.i2bBig;
import static com.netease.backend.db.common.utils.ByteUtils.i2bBig4;
import static com.netease.backend.db.common.utils.ByteUtils.toHexString;

import java.nio.ByteBuffer;


public final class SHA {

	
	private static final String	algorithm		= "SHA-1";
	
	private static final int	digestLength	= 20;
	
	private static final int	blockSize		= 64;

	
	
	
	
	private final int[]			W;

	
	private final int[]			state;

	
	private byte[]				tempArray;

	
	private byte[]				oneByte;

	
	
	
	final byte[]				buffer;
	
	private int					bufOfs;

	
	
	
	
	
	long						bytesProcessed;

	
	public final int getDigestLength() {
		return digestLength;
	}

	
	public final void update(
		ByteBuffer input)
	{
		if (input.hasRemaining() == false) {
			return;
		}
		if (input.hasArray()) {
			byte[] b = input.array();
			int ofs = input.arrayOffset();
			int pos = input.position();
			int lim = input.limit();
			this.update(b, ofs + pos, lim - pos);
			input.position(lim);
		} else {
			int len = input.remaining();
			
			int n = Math.min(4096, len);
			if ((tempArray == null) || (n > tempArray.length)) {
				tempArray = new byte[n];
			}
			while (len > 0) {
				int chunk = Math.min(len, tempArray.length);
				input.get(tempArray, 0, chunk);
				this.update(tempArray, 0, chunk);
				len -= chunk;
			}
		}
	}

	
	public final int digest(
		byte[] out, int ofs, int len)
	{
		if (len < digestLength) {
			throw new IllegalArgumentException("Length must be at least "
					+ digestLength + " for " + algorithm + "digests");
		}
		if ((ofs < 0) || (len < 0) || (ofs > out.length - len)) {
			throw new ArrayIndexOutOfBoundsException();
		}
		if (bytesProcessed < 0) {
			this.reset();
		}
		this.implDigest(out, ofs);
		bytesProcessed = -1;
		return digestLength;
	}

	
	public final int digest(
		byte[] out)
	{
		return this.digest(out, 0, out.length);
	}

	
	
	
	
	
	

	
	public final String digest() {
		if (bytesProcessed < 0) {
			this.reset();
		}
		final String s = this.implDigest();
		bytesProcessed = -1;
		return s;
	}

	
	
	
	
	
	
	
	
	
	
	public final void update(
		byte b)
	{
		if (oneByte == null) {
			oneByte = new byte[1];
		}
		oneByte[0] = b;
		this.update(oneByte, 0, 1);
	}

	public final void update(
		byte[] b)
	{
		this.update(b, 0, b.length);
	}

	
	public final void update(
		byte[] b, int ofs, int len)
	{
		if (len == 0) {
			return;
		}
		if ((ofs < 0) || (len < 0) || (ofs > b.length - len)) {
			throw new ArrayIndexOutOfBoundsException();
		}
		if (bytesProcessed < 0) {
			this.reset();
		}
		bytesProcessed += len;
		
		if (bufOfs != 0) {
			int n = Math.min(len, blockSize - bufOfs);
			System.arraycopy(b, ofs, buffer, bufOfs, n);
			bufOfs += n;
			ofs += n;
			len -= n;
			if (bufOfs >= blockSize) {
				
				this.implCompress(buffer, 0);
				bufOfs = 0;
			}
		}
		
		while (len >= blockSize) {
			this.implCompress(b, ofs);
			len -= blockSize;
			ofs += blockSize;
		}
		
		if (len > 0) {
			System.arraycopy(b, ofs, buffer, 0, len);
			bufOfs = len;
		}
	}

	
	public final void reset() {
		if (bytesProcessed == 0) {
			
			return;
		}
		this.implReset();
		bufOfs = 0;
		bytesProcessed = 0;
	}

	
	static final byte[]	padding;

	static {
		
		
		
		padding = new byte[136];
		padding[0] = (byte) 0x80;
	}

	
	@Override
	public SHA clone() {
		return new SHA(this);
	}

	
	public SHA() {
		
		
		

		buffer = new byte[blockSize];
		state = new int[5];
		W = new int[80];

		this.implReset();
	}

	
	private SHA(
		SHA base)
	{
		
		
		

		this.buffer = base.buffer.clone();
		this.bufOfs = base.bufOfs;
		this.bytesProcessed = base.bytesProcessed;
		this.state = base.state.clone();
		this.W = new int[80];
	}

	
	void implReset() {
		state[0] = 0x67452301;
		state[1] = 0xefcdab89;
		state[2] = 0x98badcfe;
		state[3] = 0x10325476;
		state[4] = 0xc3d2e1f0;
	}

	
	void implDigest(
		byte[] out, int ofs)
	{
		this.implPadBeforeDigest();
		i2bBig(state, 0, out, ofs, 20);
	}

	
	String implDigest() {
		this.implPadBeforeDigest();
		return toHexString(state, 0, state.length);
	}

	
	
	
	

	private final void implPadBeforeDigest() {
		long bitsProcessed = bytesProcessed << 3;

		int index = (int) bytesProcessed & 0x3f;
		int padLen = (index < 56) ? (56 - index) : (120 - index);
		this.update(padding, 0, padLen);

		i2bBig4((int) (bitsProcessed >>> 32), buffer, 56);
		i2bBig4((int) bitsProcessed, buffer, 60);
		this.implCompress(buffer, 0);
	}

	
	private final static int	round1_kt	= 0x5a827999;
	private final static int	round2_kt	= 0x6ed9eba1;
	private final static int	round3_kt	= 0x8f1bbcdc;
	private final static int	round4_kt	= 0xca62c1d6;

	
	void implCompress(
		byte[] buf, int ofs)
	{
		b2iBig64(buf, ofs, W);

		
		
		for (int t = 16; t <= 79; t++) {
			int temp = W[t - 3] ^ W[t - 8] ^ W[t - 14] ^ W[t - 16];
			W[t] = (temp << 1) | (temp >>> 31);
		}

		int a = state[0];
		int b = state[1];
		int c = state[2];
		int d = state[3];
		int e = state[4];

		
		for (int i = 0; i < 20; i++) {
			int temp = ((a << 5) | (a >>> (32 - 5))) + ((b & c) | ((~b) & d))
					+ e + W[i] + round1_kt;
			e = d;
			d = c;
			c = ((b << 30) | (b >>> (32 - 30)));
			b = a;
			a = temp;
		}

		
		for (int i = 20; i < 40; i++) {
			int temp = ((a << 5) | (a >>> (32 - 5))) + (b ^ c ^ d) + e + W[i]
					+ round2_kt;
			e = d;
			d = c;
			c = ((b << 30) | (b >>> (32 - 30)));
			b = a;
			a = temp;
		}

		
		for (int i = 40; i < 60; i++) {
			int temp = ((a << 5) | (a >>> (32 - 5)))
					+ ((b & c) | (b & d) | (c & d)) + e + W[i] + round3_kt;
			e = d;
			d = c;
			c = ((b << 30) | (b >>> (32 - 30)));
			b = a;
			a = temp;
		}

		
		for (int i = 60; i < 80; i++) {
			int temp = ((a << 5) | (a >>> (32 - 5))) + (b ^ c ^ d) + e + W[i]
					+ round4_kt;
			e = d;
			d = c;
			c = ((b << 30) | (b >>> (32 - 30)));
			b = a;
			a = temp;
		}
		state[0] += a;
		state[1] += b;
		state[2] += c;
		state[3] += d;
		state[4] += e;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
