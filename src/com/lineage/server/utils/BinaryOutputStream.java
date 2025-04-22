package com.lineage.server.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

import com.lineage.config.Config;

public class BinaryOutputStream extends OutputStream {

	private static final String CLIENT_LANGUAGE_CODE = Config.CLIENT_LANGUAGE_CODE;

	private final ByteArrayOutputStream _bao = new ByteArrayOutputStream();
	
	private static final BigInteger temp = new BigInteger("80", 16);
	private static final BigInteger temp1 = new BigInteger("0", 16);

	public BinaryOutputStream() {
	}

	@Override
	public void write(final int b) throws IOException {
		_bao.write(b);
	}

	public void writeD(final int value) {
		_bao.write(value & 0xff);
		_bao.write((value >> 8) & 0xff);
		_bao.write((value >> 16) & 0xff);
		_bao.write((value >> 24) & 0xff);
	}

	public void writeH(final int value) {
		_bao.write(value & 0xff);
		_bao.write((value >> 8) & 0xff);
	}

	public void writeC(final int value) {
		_bao.write(value & 0xff);
	}

	public void writeP(final int value) {
		_bao.write(value);
	}

	public void writeL(final long value) {
		_bao.write((int) (value & 0xff));
	}

	public void writeF(final double org) {
		final long value = Double.doubleToRawLongBits(org);
		_bao.write((int) (value & 0xff));
		_bao.write((int) ((value >> 8) & 0xff));
		_bao.write((int) ((value >> 16) & 0xff));
		_bao.write((int) ((value >> 24) & 0xff));
		_bao.write((int) ((value >> 32) & 0xff));
		_bao.write((int) ((value >> 40) & 0xff));
		_bao.write((int) ((value >> 48) & 0xff));
		_bao.write((int) ((value >> 56) & 0xff));
	}

	public void writeS(final String text) {
		try {
			if (text != null) {
				_bao.write(text.getBytes(CLIENT_LANGUAGE_CODE));
			}
		} catch (final Exception e) {
		}

		_bao.write(0x00);
	}

	public void writeByte(final byte[] text) {
		try {
			if (text != null) {
				_bao.write(text);
			}
		} catch (final Exception e) {
		}
	}

	public int getLength() {
		return _bao.size() + 2;
	}

	public byte[] getBytes() {
		return _bao.toByteArray();
	}
	
	public void writeC(int count, long value) {
		writeC(count, 0, value);
	}
	
	public void writeC(int count, int add, long value) {

		if (count != 0) {
			int index;
			for (index = 8 * count + add; index >> 7 != 0; index >>= 7) {
				writeC(index & 0x7F | 0x80);
			}
			writeC(index);
		}
		if (value < 0L) {
			BigInteger k = new BigInteger(Long.toHexString(value), 16);
			while (!k.divide(temp).equals(temp1)) {
				this._bao.write(k.remainder(temp).add(temp).intValue());
				k = k.divide(temp);
			}
			this._bao.write(k.intValue());

		} else {
			for (; value / 128L != 0L; value /= 128L) {
				writeC((int) (value % 128L + 128L));
			}
			// for (; value >> 7 != 0L; value >>= 7) {
			// writeC((int) (value & 0x7F | 0x80));
			// }
			writeC((int) value);
		}
	}
}
