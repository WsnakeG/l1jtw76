package com.lineage.server.serverpackets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigBad;
import com.lineage.server.utils.RandomArrayList;

/**
 * @author dexc
 */
public abstract class ServerBasePacket extends OpcodesServer {

	private static final Log _log = LogFactory.getLog(ServerBasePacket.class);

	protected static final String CLIENT_LANGUAGE_CODE = Config.CLIENT_LANGUAGE_CODE;
	protected ByteArrayOutputStream _bao3 = new ByteArrayOutputStream();
	protected ByteArrayOutputStream _bao5 = new ByteArrayOutputStream();
	protected ByteArrayOutputStream _bao = new ByteArrayOutputStream();

	static int _baseNumber = 8;

	private static final BigInteger jdField_for = new BigInteger("80", 16);
	private static final BigInteger jdField_if = new BigInteger("0", 16);

	/**
	 * boolean
	 * 
	 * @param b
	 * @return
	 */
	protected Object writeBoolean(final boolean b) {
		_bao.write(b ? 0x01 : 0x00);

		return null;
	}

	protected void writeD(final int value) {
		_bao.write(value & 0xff);
		_bao.write((value >> 8) & 0xff);
		_bao.write((value >> 16) & 0xff);
		_bao.write((value >> 24) & 0xff);
	}

	protected void writeH(final int value) {
		_bao.write(value & 0xff);
		_bao.write((value >> 8) & 0xff);
	}

	protected void writeC(final int value) {
		_bao.write(value & 0xff);
	}

	protected void writeP(final int value) {
		_bao.write(value);
	}

	protected void writeL(final long value) {
		_bao.write((int) (value & 0xff));
	}

	protected void writeF(final double org) {
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

	/**
	 * 寫入一個布林至暫存器中
	 * 
	 * @param b
	 */
	protected void writeB(final boolean b) {
		// true = 1, false = 0
		this._bao.write(b ? 1 : 0);
	}
	
	protected void writeExp(final long value) {
		_bao.write((int) (value & 0xff));
		_bao.write((int) ((value >> 8) & 0xff));
		_bao.write((int) ((value >> 16) & 0xff));
		_bao.write((int) ((value >> 24) & 0xff));

		/*
		 * this._bao.write((int) (value & 0xff)); this._bao.write((int) (value
		 * >> 8 & 0xff)); this._bao.write((int) (value >> 16 & 0xff));
		 * this._bao.write((int) (value >> 24 & 0xff)); this._bao.write((int)
		 * (value >> 32 & 0xff)); this._bao.write((int) (value >> 40 & 0xff));
		 * this._bao.write((int) (value >> 48 & 0xff)); this._bao.write((int)
		 * (value >> 56 & 0xff));
		 */
	}

	protected void writeS(final String text) {
		try {
			if (text != null) {
				String chtext = text;
				for (final Iterator<String> iter = ConfigBad.BAD_TEXT_LIST.iterator(); iter.hasNext();) {
					final String bad = iter.next();
					final int index = chtext.indexOf(bad);
					if (index != -1) {
						chtext = text.substring(0, index);
						chtext += text.substring(index + bad.length());
					}
				}
				_bao.write(chtext.getBytes(CLIENT_LANGUAGE_CODE));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		_bao.write(0x00);
	}

	protected void writeByte(final byte[] text) {
		try {
			if (text != null) {
				_bao.write(text);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		// this._bao.write(0x00);
	}

	// private static Random _random = new Random();

	/**
	 * 不足8組 補滿8組BYTE
	 * 
	 * @return
	 */
	protected byte[] getBytes() {

		final int padding = _bao.size() % 8;
		if (padding != 0) {
			for (int i = padding; i < 8; i++) {
				writeC(0x00);
			}
		}

		return _bao.toByteArray();
	}

	/*
	 * protected void jdField_do(byte[] text) { try { if (text != null)
	 * this._bao.write(text); } catch (Exception e) { } }
	 */
	protected void writecraft(final byte[] array) {
		try {
			if ((array != null) && ((long) array.length > 0)) {
				jdField_for(array.length);
				_bao.write(array);
			} else {
				_bao.write(0);
			}
		} catch (final Exception e) {
		}
	}

	protected void jdField_for(long value) {
		if (value < 0L) {
			BigInteger k = new BigInteger(Long.toHexString(value), 16);
			while (!k.divide(jdField_for).equals(jdField_if)) {
				_bao.write(k.remainder(jdField_for).add(jdField_for).intValue());
				k = k.divide(jdField_for);
			}
			_bao.write(k.intValue());
		} else {
			while ((value >> 7) != 0L) {
				if (value < 0L) {
					_bao.write((byte) (int) (0xFF | (value & 0x7F)));
				} else {
					_bao.write((byte) (int) ((value & 0x7F) | 0x80));
				}
				value >>= 7;
			}
			_bao.write((byte) (int) value);
		}
	}

	protected void jdField_if(final String text) {
		try {
			if (text != null) {
				final byte[] testArray = text.getBytes(CLIENT_LANGUAGE_CODE);
				writecraft(testArray);

			} else {
				_bao.write(0);
			}
		} catch (final Exception e) {
		}
	}

	protected void a(final int key, final long value) {
		_bao.write(key);
		jdField_for(value);
	}

	protected void as(final int key, final String text) {
		_bao.write(key);
		jdField_if(text);
	}

	protected void a(final int key, final byte[] array) {
		_bao.write(key);
		writecraft(array);
	}

	protected void writeCC(final int count, long value) {
		if (count != 0) {
			writeC(8 * count);
		}
		while ((value >> 7) != 0L) {
			writeC((int) ((value & 0x7F) | 0x80));
			value >>= 7;
		}
		writeC((int) value);
	}
	
	

	protected void writeCS(final int count, final String text) {
		try {
			writeC((count * 8) + 2);
			if (text != null) {
				writeCC(0, text.getBytes(CLIENT_LANGUAGE_CODE).length);
				_bao.write(text.getBytes(CLIENT_LANGUAGE_CODE));
			} else {
				writeC(0);
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	protected void writeByte(int count, byte array[]) {
		try {
			// writeC(count * 8 + 2);
			if (count != 0) {
				int index;
				for (index = _baseNumber * count + 2; index >> 7 != 0; index >>= 7) {
					writeC(index & 0x7F | 0x80);
				}
				writeC(index);
			}
			if (array != null) {
				writeCC(0, array.length);
				byte[] abyte0 = array;
				int j = array.length;
				for (int i = 0; i < j; i++) {
					int b = abyte0[i];
					this._bao.write(b);
				}
			} else {
				writeC(0);
			}
		} catch (Exception e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	// XXX protobuf
	private static final Charset CLIENT_CHARSET = Charset.forName(Config.CLIENT_LANGUAGE_CODE);
	
	private static final int WIRETYPE_VARINT = 0;

	private static final int WIRETYPE_FIXED64 = 1;

	private static final int WIRETYPE_LENGTH_DELIMITED = 2;

	private static final int WIRETYPE_FIXED32 = 5;

	private static final int TAG_TYPE_BITS = 3;
	
	/** 1 x 1 */
	protected void randomByte() {
		this.writeC((byte) RandomArrayList.getInt(256));
	}

	/** 1 x 2 */
	protected void randomShort() {
		this.randomByte();
		this.randomByte();
	}

	/** 1 x 3 */
	protected void randomShort3() {
		this.randomByte();
		this.randomByte();
		this.randomByte();
	}

	/** 2 x 2 */
	protected void randomInt() {
		this.randomShort();
		this.randomShort();
	}

	/** 2 x 3 */
	protected void randomInt6() {
		this.randomShort();
		this.randomInt();
	}

	/** 4 x 4 */
	protected void randomLong() {
		this.randomInt();
		this.randomInt();
	}
	
	protected void writeDouble(final int fieldNumber, final double value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_FIXED64);
		this.writeRawLittleEndian64(Double.doubleToRawLongBits(value));
	}

	protected void writeFloat(final int fieldNumber, final float value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_FIXED32);
		this.writeRawLittleEndian32(Float.floatToRawIntBits(value));
	}

	protected void writeFixed64(final int fieldNumber, final long value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_FIXED64);
		this.writeRawLittleEndian64(value);
	}

	protected void writeFixed32(final int fieldNumber, final int value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_FIXED32);
		this.writeRawLittleEndian32(value);
	}

	protected void writeUInt64(final int fieldNumber, final long value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_VARINT);
		this.writeRawVarint64(value);
	}

	protected void writeUInt32(final int fieldNumber, final int value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_VARINT);
		this.writeRawVarint32(value);
	}

	protected void writeInt64(final int fieldNumber, final long value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_VARINT);
		this.writeRawVarint64(value);
	}

	protected void writeInt32(final int fieldNumber, final int value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_VARINT);
		if (value >= 0) {
			this.writeRawVarint32(value);
		} else {
			this.writeRawVarint64(value);
		}
	}

	protected void writeBool(final int fieldNumber, final boolean value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_VARINT);
		_bao.write(value ? 1 : 0);
	}

	protected void writeEnum(final int fieldNumber, final int value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_VARINT);
		if (value >= 0) {
			this.writeRawVarint32(value);
		} else {
			this.writeRawVarint64(value);
		}
	}

	protected void writeString(final int fieldNumber, final String value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS
				| WIRETYPE_LENGTH_DELIMITED);
		try {
			if (value != null) {
				final byte[] bytes = value.getBytes(CLIENT_CHARSET);
				this.writeRawVarint32(bytes.length);
				_bao.write(bytes);
			} else {
				this.writeRawVarint32(0);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	protected void writeByteArray(final int fieldNumber, final byte[] value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS
				| WIRETYPE_LENGTH_DELIMITED);
		try {
			if (value != null) {
				this.writeRawVarint32(value.length);
				_bao.write(value);
			} else {
				this.writeRawVarint32(0);
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	protected void writeSInt64(final int fieldNumber, final long value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_VARINT);
		this.writeRawVarint64(encodeZigZag64(value));
	}

	protected void writeSInt32(final int fieldNumber, final int value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_VARINT);
		this.writeRawVarint32(encodeZigZag32(value));
	}

	protected void writeSFixed64(final int fieldNumber, final long value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_FIXED64);
		this.writeRawLittleEndian64(value);
	}

	protected void writeSFixed32(final int fieldNumber, final int value) {
		this.writeRawVarint32(fieldNumber << TAG_TYPE_BITS | WIRETYPE_FIXED32);
		this.writeRawLittleEndian32(value);
	}

	private void writeRawLittleEndian64(final long value) {
		this._bao.write((int) value & 0xFF);
		this._bao.write((int) (value >> 8) & 0xFF);
		this._bao.write((int) (value >> 16) & 0xFF);
		this._bao.write((int) (value >> 24) & 0xFF);
		this._bao.write((int) (value >> 32) & 0xFF);
		this._bao.write((int) (value >> 40) & 0xFF);
		this._bao.write((int) (value >> 48) & 0xFF);
		this._bao.write((int) (value >> 56) & 0xFF);
	}

	private void writeRawLittleEndian32(final int value) {
		this._bao.write(value & 0xFF);
		this._bao.write(value >> 8 & 0xFF);
		this._bao.write(value >> 16 & 0xFF);
		this._bao.write(value >> 24 & 0xFF);
	}

	private void writeRawVarint64(long value) {
		while (true) {
			if ((value & ~0x7FL) == 0) {
				this._bao.write((int) value);
				return;
			} else {
				this._bao.write((int) value & 0x7F | 0x80);
				value >>>= 7;
			}
		}
	}

	private void writeRawVarint32(int value) {
		while (true) {
			if ((value & ~0x7F) == 0) {
				this._bao.write(value);
				return;
			} else {
				this._bao.write(value & 0x7F | 0x80);
				value >>>= 7;
			}
		}
	}
	
	protected void writeBit(long value) {
		if (value < 0L) {
			final String stringValue = Integer.toBinaryString((int) value);
			value = Long.valueOf(stringValue, 2).longValue();
		}
		int i = 0;
		while (value >> 7 * (i + 1) > 0L) {
			_bao.write((int) ((value >> 7 * i++) % 128L | 0x80));
		}
		_bao.write((int) ((value >> 7 * i) % 128L));
	}
	
	protected void write7B(final long value) {
		int i = 0;
		final BigInteger b = new BigInteger("18446744073709551615");

		while (BigInteger.valueOf(value).and(b).shiftRight((i + 1) * 7).longValue() > 0) {
			_bao.write(BigInteger.valueOf(value).and(b).shiftRight(7 * i++).remainder(BigInteger.valueOf(0x80)).or(BigInteger.valueOf(0x80)).intValue());
		}
		_bao.write(BigInteger.valueOf(value).and(b).shiftRight(7 * i++).remainder(BigInteger.valueOf(0x80)).intValue());
	}

	static long encodeZigZag64(final long n) {
		return n << 1 ^ n >> 63;
	}

	static int encodeZigZag32(final int n) {
		return n << 1 ^ n >> 31;
	}
	
	public abstract byte[] getContent() throws IOException;

	public String getType() { //20240910
		return this.getClass().getSimpleName();
	}
	
	public abstract byte[] getContentGBK() throws IOException;
	public abstract byte[] getContentBIG5() throws IOException;
	
}
