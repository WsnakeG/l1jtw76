package com.lineage.server.clientpackets;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;

/**
 * 客戶端封包解析
 * 
 * @author DaiEn
 */
public abstract class ClientBasePacket {

	private static final Log _log = LogFactory.getLog(ClientBasePacket.class);

	protected static final String CLIENT_LANGUAGE_CODE = Config.CLIENT_LANGUAGE_CODE;

	private byte _decrypt[] = null;

	private int _off = 0;

	/**
	 * 執行客戶端封包處理接口
	 */
	public abstract void start(byte[] decrypt, ClientExecutor client) throws Exception;

	/*
	 * public ClientBasePacket() { }
	 */

	/*
	 * protected ClientBasePacket(final byte abyte0[]) { //_log.finest("type=" + getType() + ", len=" + abyte0.length); this._decrypt = abyte0; this._off = 1; }
	 */

	/**
	 * 載入BYTE陣列
	 * 
	 * @param abyte0
	 */
	protected void read(final byte abyte0[]) {
		try {
			_decrypt = abyte0;
			_off = 1;// 忽略第一個封包(OP編組)

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 由byte[]中取回一個int
	 * 
	 * @return
	 */
	protected int readD() {
		try {
			// 10: 01010
			// 20: 10100
			// X=: 11110(30)
			// 00010
			// 10000
			if (_decrypt == null) {
				return 0x00;
			}
			if (_decrypt.length < (_off + 4)) {
				return 0x00;
			}

			int i = _decrypt[_off++] & 0xff;
			i |= (_decrypt[_off++] << 8) & 0xff00;
			i |= (_decrypt[_off++] << 16) & 0xff0000;
			i |= (_decrypt[_off++] << 24) & 0xff000000;

			return i;
		} catch (final Exception e) {
			_log.error("OpCode:" + (_decrypt[0] & 0xff) + "/" + _decrypt.length, e);
		}
		return 0x00;
	}

	/**
	 * 由byte[]中取回一個byte
	 * 
	 * @return
	 */
	protected int readC() {
		try {
			if (_decrypt == null) {
				return 0x00;
			}
			if (_decrypt.length < (_off + 1)) {
				return 0x00;
			}
			final int i = _decrypt[_off++] & 0xff;

			return i;
		} catch (final Exception e) {
			// _log.error("OpCode:" + (_decrypt[0] & 0xff) + "/" +
			// _decrypt.length, e);
		}
		return 0x00;
	}

	/**
	 * 由byte[]中取回一個short
	 * 
	 * @return
	 */
	protected int readH() {
		try {
			if (_decrypt == null) {
				return 0x00;
			}
			if (_decrypt.length < (_off + 2)) {
				return 0x00;
			}
			int i = _decrypt[_off++] & 0xff;
			i |= (_decrypt[_off++] << 8) & 0xff00;

			return i;

		} catch (final Exception e) {
			// _log.error("OpCode:" + (_decrypt[0] & 0xff) + "/" +
			// _decrypt.length, e);
		}
		return 0x00;
	}

	/**
	 * 由byte[]中取回一個short
	 * 
	 * @return
	 */
	protected int readCH() {
		try {
			if (_decrypt == null) {
				return 0x00;
			}
			if (_decrypt.length < (_off + 3)) {
				return 0x00;
			}
			int i = _decrypt[_off++] & 0xff;
			i |= (_decrypt[_off++] << 8) & 0xff00;
			i |= (_decrypt[_off++] << 16) & 0xff0000;

			return i;

		} catch (final Exception e) {
			// _log.error("OpCode:" + (_decrypt[0] & 0xff) + "/" +
			// _decrypt.length, e);
		}
		return 0x00;
	}

	/**
	 * 由byte[]中取回一個double
	 * 
	 * @return
	 */
	protected double readF() {
		try {
			if (_decrypt == null) {
				return 0x00;
			}
			if (_decrypt.length < (_off + 8)) {
				return 0D;
			}
			long l = _decrypt[_off++] & 0xff;
			l |= (_decrypt[_off++] << 8) & 0xff00;
			l |= (_decrypt[_off++] << 16) & 0xff0000;
			l |= (_decrypt[_off++] << 24) & 0xff000000;
			l |= ((long) _decrypt[_off++] << 32) & 0xff00000000L;
			l |= ((long) _decrypt[_off++] << 40) & 0xff0000000000L;
			l |= ((long) _decrypt[_off++] << 48) & 0xff000000000000L;
			l |= ((long) _decrypt[_off++] << 56) & 0xff00000000000000L;

			return Double.longBitsToDouble(l);

		} catch (final Exception e) {
			// _log.error("OpCode:" + (_decrypt[0] & 0xff) + "/" +
			// _decrypt.length, e);
		}
		return 0D;
	}

	/**
	 * 由byte[]中取回一個String
	 * 
	 * @return
	 */
	protected String readS() {
		String s = null;
		try {
			if (_decrypt == null) {
				return s;
			}
			s = new String(_decrypt, _off, _decrypt.length - _off, CLIENT_LANGUAGE_CODE);
			s = s.substring(0, s.indexOf('\0'));
			_off += s.getBytes(CLIENT_LANGUAGE_CODE).length + 1;

		} catch (final Exception e) {
			// _log.error("OpCode:" + (_decrypt[0] & 0xff) + "/" +
			// _decrypt.length, e);
		}
		return s;
	}

	/**
	 * 由byte[]中取回一組byte[]
	 * 
	 * @return
	 */
	protected byte[] readByte() {
		final byte[] result = new byte[_decrypt.length - _off];
		try {
			System.arraycopy(_decrypt, _off, result, 0, _decrypt.length - _off);
			_off = _decrypt.length;

		} catch (final Exception e) {
			// _log.error("OpCode:" + (_decrypt[0] & 0xff) + "/" +
			// _decrypt.length, e);
		}
		return result;
	}

	/**
	 * 由byte[]中取回一組指定長度的byte數組
	 * 
	 * @param length
	 *            指定長度
	 * @return
	 */
	protected byte[] readByte(final int length) {
		if (length <= 0) {
			return null;
		}

		final byte[] result = new byte[length];
		try {
			System.arraycopy(this._decrypt, this._off, result, 0, length);
			final int offset = length + this._off;
			this._off = offset;
		} catch (final Exception e) {
			// _log.error("OpCode:" + (this._decrypt[0] & 0xff) + "/" + this._decrypt.length, e);
		}
		return result;
	}

	/**
	 * 結束byte[]取回
	 */
	public void over() {
		try {
			_decrypt = null;
			_off = 0;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public String getType() {
		return this.getClass().getSimpleName();
	}

	protected byte[] readCraftB() {
		final int i = _decrypt[_off] & 0xFF;
		if ((i < 10) || ((i % 8) != 2)) {
			return null;
		}
		_off += 1;
		final int size = getIntByteLength(false);
		final byte[] result = new byte[size];

		try {
			System.arraycopy(_decrypt, _off, result, 0, size);
			_off += size;

		} catch (final Exception e) {
		}
		return result;
	}

	public int readCraft() {
		return getIntByteLength(true);
	}

	protected int getIntByteLength(final boolean check) {
		if (check) {
			final int i = _decrypt[_off] & 0xFF;
			if ((i < 8) || ((i % 8) != 0)) {
				return 0;
			}
			_off += 1;
		}
		final ByteArrayOutputStream bao = new ByteArrayOutputStream();
		int temp = 0;
		do {
			temp = readC();
			bao.write(temp);
		} while (temp >= 128);

		return (int) getByteLength(bao.toByteArray());
	}

	protected long getByteLength(final byte[] numberArray) {
		if (numberArray.length < 2) {
			return numberArray[0] & 0xFF;
		}
		long decrypt = 0L;
		for (int i = numberArray.length - 1; i > 0; i--) {
			if (decrypt == 0L) {
				decrypt = numberArray[i] << 7;
			} else {
				decrypt <<= 7;
			}
			if ((i - 1) >= 0) {
				decrypt += ((numberArray[(i - 1)] & 0xFF) ^ 0x80);
			}
		}
		return decrypt;
	}

	protected int jdMethod_long() {
		return _off;
	}

	public long readLong() {
		int tempOff = this._off;
		ArrayList<Integer> decryptArray = new ArrayList<Integer>();
		for (int j = tempOff; j < this._decrypt.length; j++) {
			int k = this._decrypt[j] & 0xFF;
			if (k <= 127) {
				decryptArray.add(k);
				this._off += 1;
				break;
			}
			decryptArray.add(k);
			this._off += 1;
		}
		return decrypt(toArray(decryptArray));
	}

	private int[] toArray(ArrayList<Integer> arr) {
		int[] a = new int[arr.size()];
		for (int i = 0; i < a.length; i++) {
			// a[i] = ((Integer) arr.get(i)).intValue();
			a[i] = arr.get(i);
		}
		return a;
	}

	public int decrypt(int[] arr) {
		int decrypt = 0;
		for (int i = arr.length - 1; i > 0; i--) {
			if (decrypt == 0) {
				decrypt = 128 * arr[i];
			} else {
				decrypt *= 128;
			}
			if (i - 1 >= 0) {
				decrypt += arr[(i - 1)] - 128;
			}
		}
		if (arr.length == 1) {
			decrypt = arr[0];
		}
		return decrypt;
	}

	static int _baseNumber = 8;

	/** 讀取字符串 */
	public String readSAt(int index) {
		String s = null;
		if (this._decrypt.length > this._off) {
			long i = this._decrypt[this._off] & 0xFF;
			if (i == _baseNumber * index + 2) {
				try {
					this._off += 1;
					int length = (int) readLong();
					s = new String(this._decrypt, this._off, length, CLIENT_LANGUAGE_CODE);
					this._off += s.getBytes(CLIENT_LANGUAGE_CODE).length;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return s;
	}

	public BigInteger readBigIntAt(int index) {
		BigInteger b = null;
		if (this._decrypt.length > this._off) {
			long i = this._decrypt[this._off] & 0xFF;
			if (i == _baseNumber * index) {
				this._off += 1;
				i = readLong();
			} else {
				return b;
			}
			b = new BigInteger(String.valueOf(i));
		}
		return b;
	}

	/** 讀取byte[] */
	public byte[] readByteAt(int index) {
		byte[] result = null;
		if (this._decrypt.length > this._off) {
			long i = this._decrypt[this._off] & 0xFF;
			if (i == _baseNumber * index + 2) {
				this._off += 1;
				int length = (int) readLong();
				result = new byte[length];
				try {
					System.arraycopy(this._decrypt, this._off, result, 0, length);
					this._off += length;
				} catch (ArrayIndexOutOfBoundsException e) {
					result = null;
				} catch (Exception e) {
					// _log.log(Level.SEVERE, "OpCode=" + (this._decrypt[0] & 0xFF), e);
				}
			}
		}
		return result;
	}

	public int readInteger() {

		ByteArrayOutputStream bao = null;
		try {
			final int i = this._decrypt[this._off] & 0xff;
			if ((i < 8) || ((i % 8) != 0)) {
				return 0;
			}
			this._off++;
			bao = new ByteArrayOutputStream();
			int temp = 0;
			do {
				temp = this.readC();
				bao.write(temp);
			} while (temp >= 128);
		} catch (final Exception e) {
			return 0x00;
		}
		return (int) readC(bao.toByteArray());
	}

	public long readC(byte[] number) {
		if (number.length < 2) {
			return (long) (number[0] & 0xff);
		}
		long decrypt = 0L;
		for (int i = number.length - 1; i > 0; i--) {
			if (decrypt == 0) {
				decrypt = number[i] << 7;
			} else {
				decrypt <<= 7;
			}
			if (i - 1 >= 0) {
				decrypt += number[i - 1] & 0xff ^ 0x80;
			}
		}
		return decrypt;
	}

	/**
	 * 取回SHA加密的byte[]數組
	 * 
	 * @return
	 */
	public byte[] readSHA() {

		final int i = this._decrypt[this._off] & 0xFF;

		if ((i < 10) || ((i % 8) != 2)) {
			return null;
		}
		this._off += 1;
		final int size = this.readC();

		final byte[] result = new byte[size];
		try {
			System.arraycopy(this._decrypt, this._off, result, 0, size);
			this._off += size;
		} catch (final Exception e) {
		}
		return result;
	}
	
	// ----------7.0新增-----------------
		public byte[] readSHA(int size) {
			byte[] result = new byte[size];
			for (int i = 0; i < size; i++) {
				result[i] = (byte) readC();
			}
			return result;
		}

	public String readPB_SHA(final byte[] config) {
		String hs = "";
		try {
			String stmp;
			for (final byte element : config) {
				stmp = Integer.toHexString(element & 0xff);
				if (stmp.length() == 1) {
					hs = hs + "0" + stmp;
				} else {
					hs = hs + stmp;
				}
			}
		} catch (final Exception e) {
		}
		return hs.toUpperCase();
	}

	protected void a(final int _off) {
		this._off = _off;
	}

	protected boolean jdField_do() {
		return _off >= (_decrypt.length - 1);
	}

	// 讀取指定的長度
	public byte[] read(final int length) {
		final byte[] array = new byte[length];
		for (int i = _off; i < _off + length; i++) {
			array[i - _off] = _decrypt[i];
		}
		_off += length;
		return array;
	}
}
