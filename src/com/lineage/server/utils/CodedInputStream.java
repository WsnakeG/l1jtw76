package com.lineage.server.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Reads and decodes protocol message fields.
 * 
 * This class contains two kinds of methods: methods that read specific protocol
 * message constructs and field types (e.g. {@link #readTag()} and
 * {@link #readInt32()}) and methods that read low-level values (e.g.
 * {@link #readRawVarint32()} and {@link #readRawBytes}). If you are reading
 * encoded protocol messages, you should use the former methods, but if you are
 * reading some other format of your own design, use the latter.
 * 
 * @author kenton@google.com Kenton Varda
 */
public final class CodedInputStream {

	/**
	 * Create a new CodedInputStream wrapping the given byte array.
	 */
	public static CodedInputStream newInstance(final byte[] buf) {
		return newInstance(buf, 0, buf.length);
	}

	/**
	 * Create a new CodedInputStream wrapping the given byte array slice.
	 */
	public static CodedInputStream newInstance(final byte[] buf, final int off, final int len) {
		final CodedInputStream result = new CodedInputStream(buf, off, len);
		try {
			// Some uses of CodedInputStream can be more efficient if they know
			// exactly how many bytes are available. By pushing the end point of
			// the
			// buffer as a limit, we allow them to get this information via
			// getBytesUntilLimit(). Pushing a limit that we know is at the end
			// of
			// the stream can never hurt, since we can never past that point
			// anyway.
			result.pushLimit(len);
		} catch (final InvalidProtocolBufferException ex) {
			// The only reason pushLimit() might throw an exception here is if
			// len
			// is negative. Normally pushLimit()'s parameter comes directly off
			// the
			// wire, so it's important to catch exceptions in case of corrupt or
			// malicious data. However, in this case, we expect that len is not
			// a
			// user-supplied value, so we can assume that it being negative
			// indicates
			// a programming error. Therefore, throwing an unchecked exception
			// is
			// appropriate.
			throw new IllegalArgumentException(ex);
		}
		return result;
	}
	

	// -----------------------------------------------------------------
	/**
	 * Parse a single field from {@code input} and merge it into this set.
	 * 
	 * @param tag
	 *            The field's tag number, which was already parsed.
	 * @return {@code false} if the tag is an end group tag.
	 */
	public boolean mergeFieldFrom(final int tag, final CodedInputStream input)
			throws IOException {
		final int number = getTagFieldNumber(tag);
		switch (getTagWireType(tag)) {
		case WIRETYPE_VARINT:
			System.out.println("未知屬性 Key:" + number + " Value:" + input.readInt64());
			return true;
		case WIRETYPE_FIXED64:
			System.out.println("未知屬性 Key:" + number + " Value:" + input.readFixed64());
			return true;
		case WIRETYPE_LENGTH_DELIMITED:
			System.out.println("未知屬性 Key:" + number + " Value:" + bytesToHex(input.readByteArray()).toUpperCase().replace(":", " "));
			return true;
		case WIRETYPE_START_GROUP:// bad writetype
			throw InvalidProtocolBufferException.badWriteType("START_GROUP");
		case WIRETYPE_END_GROUP:// bad writetype
			throw InvalidProtocolBufferException.badWriteType("END_GROUP");
		case WIRETYPE_FIXED32:
			System.out.println("未知屬性 Key:" + number + " Value:" + input.readFixed32());
			return true;
		default:
			throw InvalidProtocolBufferException.invalidWireType();
		}
	}

	/**
	 * Attempt to read a field tag, returning zero if we have reached EOF.
	 * Protocol message parsers use this to read tags, since a protocol message
	 * may legally end wherever a tag occurs, and zero is not a valid tag
	 * number.
	 */
	public int readTag() throws IOException {
		if (this.isAtEnd()) {
			this.lastTag = 0;
			return 0;
		}

		this.lastTag = this.readRawVarint32();

		// 異常Number 0
		if (getTagFieldNumber(this.lastTag) == 0) {
			// If we actually read zero (or any tag number corresponding to
			// field
			// number zero), that's not a valid tag.
			throw InvalidProtocolBufferException.invalidTag();
		}

		return this.lastTag;
	}

	/**
	 * Verifies that the last call to readTag() returned the given tag value.
	 * This is used to verify that a nested group ended with the correct end
	 * tag.
	 * 
	 * @throws InvalidProtocolBufferException
	 *             {@code value} does not match the last tag.
	 */
	public void checkLastTagWas(final int value)
			throws InvalidProtocolBufferException {
		if (this.lastTag != value) {
			throw InvalidProtocolBufferException.invalidEndTag();
		}
	}

	public int getLastTag() {
		return this.lastTag;
	}

	/**
	 * Reads and discards a single field, given its tag value.
	 * 
	 * @return {@code false} if the tag is an endgroup tag, in which case
	 *         nothing is skipped. Otherwise, returns {@code true}.
	 */
	public boolean skipField(final int tag) throws IOException {
		switch (getTagWireType(tag)) {
		case WIRETYPE_VARINT:
			this.skipRawVarint();
			return true;
		case WIRETYPE_FIXED64:
			this.skipRawBytes(8);
			return true;
		case WIRETYPE_LENGTH_DELIMITED:
			this.skipRawBytes(this.readRawVarint32());
			return true;
		case WIRETYPE_START_GROUP:
			this.skipMessage();
			this.checkLastTagWas(makeTag(getTagFieldNumber(tag),
					WIRETYPE_END_GROUP));
			return true;
		case WIRETYPE_END_GROUP:
			return false;
		case WIRETYPE_FIXED32:
			this.skipRawBytes(4);
			return true;
		default:
			throw InvalidProtocolBufferException.invalidWireType();
		}
	}

	/**
	 * Reads and discards an entire message. This will read either until EOF or
	 * until an endgroup tag, whichever comes first.
	 */
	public void skipMessage() throws IOException {
		while (true) {
			final int tag = this.readTag();
			if (tag == 0 || !this.skipField(tag)) {
				return;
			}
		}
	}

	// -----------------------------------------------------------------

	/** Read a {@code double} field value from the stream. */
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(this.readRawLittleEndian64());
	}

	/** Read a {@code float} field value from the stream. */
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(this.readRawLittleEndian32());
	}

	/** Read a {@code uint64} field value from the stream. */
	public long readUInt64() throws IOException {
		return this.readRawVarint64();
	}

	/** Read an {@code int64} field value from the stream. */
	public long readInt64() throws IOException {
		return this.readRawVarint64();
	}

	/** Read an {@code int32} field value from the stream. */
	public int readInt32() throws IOException {
		return this.readRawVarint32();
	}

	/** Read a {@code fixed64} field value from the stream. */
	public long readFixed64() throws IOException {
		return this.readRawLittleEndian64();
	}

	/** Read a {@code fixed32} field value from the stream. */
	public int readFixed32() throws IOException {
		return this.readRawLittleEndian32();
	}

	/** Read a {@code bool} field value from the stream. */
	public boolean readBool() throws IOException {
		return this.readRawVarint64() != 0;
	}

	/**
	 * Read a {@code string} field value from the stream. If the stream contains
	 * malformed charsetName, replace the offending bytes with the charsetName
	 * replacement character.
	 */
	public String readString(final String charsetName) throws IOException {
		final int size = this.readRawVarint32();
		if (size <= this.bufferSize - this.bufferPos && size > 0) {
			// Fast path: We already have the bytes in a contiguous buffer, so
			// just copy directly from it.
			final String result = new String(this.buffer, this.bufferPos, size,
					charsetName);
			this.bufferPos += size;
			return result;
		} else if (size == 0) {
			return "";
		} else {
			// Slow path: Build a byte array first then copy it.
			return new String(this.readRawBytesSlowPath(size), charsetName);
		}
	}

	/** Read a {@code bytes} field value from the stream. */
	public byte[] readByteArray() throws IOException {
		final int size = this.readRawVarint32();
		if (size <= this.bufferSize - this.bufferPos && size > 0) {
			// Fast path: We already have the bytes in a contiguous buffer, so
			// just copy directly from it.
			final byte[] result = Arrays.copyOfRange(this.buffer,
					this.bufferPos, this.bufferPos + size);
			this.bufferPos += size;
			return result;
		} else {
			// Slow path: Build a byte array first then copy it.
			return this.readRawBytesSlowPath(size);
		}
	}

	/** Read a {@code uint32} field value from the stream. */
	public int readUInt32() throws IOException {
		return this.readRawVarint32();
	}

	/**
	 * Read an enum field value from the stream. Caller is responsible for
	 * converting the numeric value to an actual enum.
	 */
	public int readEnum() throws IOException {
		return this.readRawVarint32();
	}

	/** Read an {@code sfixed32} field value from the stream. */
	public int readSFixed32() throws IOException {
		return this.readRawLittleEndian32();
	}

	/** Read an {@code sfixed64} field value from the stream. */
	public long readSFixed64() throws IOException {
		return this.readRawLittleEndian64();
	}

	/** Read an {@code sint32} field value from the stream. */
	public int readSInt32() throws IOException {
		return decodeZigZag32(this.readRawVarint32());
	}

	/** Read an {@code sint64} field value from the stream. */
	public long readSInt64() throws IOException {
		return decodeZigZag64(this.readRawVarint64());
	}

	// =================================================================

	/**
	 * Read a raw Varint from the stream. If larger than 32 bits, discard the
	 * upper bits.
	 */
	public int readRawVarint32() throws IOException {
		// See implementation notes for readRawVarint64
		fastpath: {
			int pos = this.bufferPos;

			if (this.bufferSize == pos) {
				break fastpath;
			}

			final byte[] buffer = this.buffer;
			int x;
			if ((x = buffer[pos++]) >= 0) {
				this.bufferPos = pos;
				return x;
			} else if (this.bufferSize - pos < 9) {
				break fastpath;
			} else if ((x ^= buffer[pos++] << 7) < 0L) {
				x ^= ~0L << 7;
			} else if ((x ^= buffer[pos++] << 14) >= 0L) {
				x ^= ~0L << 7 ^ ~0L << 14;
			} else if ((x ^= buffer[pos++] << 21) < 0L) {
				x ^= ~0L << 7 ^ ~0L << 14 ^ ~0L << 21;
			} else {
				final int y = buffer[pos++];
				x ^= y << 28;
				x ^= ~0L << 7 ^ ~0L << 14 ^ ~0L << 21 ^ ~0L << 28;
				if (y < 0 && buffer[pos++] < 0 && buffer[pos++] < 0
						&& buffer[pos++] < 0 && buffer[pos++] < 0
						&& buffer[pos++] < 0) {
					break fastpath; // Will throw malformedVarint()
				}
			}
			this.bufferPos = pos;
			return x;
		}
		return (int) this.readRawVarint64SlowPath();
	}

	private void skipRawVarint() throws IOException {
		if (this.bufferSize - this.bufferPos >= 10) {
			final byte[] buffer = this.buffer;
			int pos = this.bufferPos;
			for (int i = 0; i < 10; i++) {
				if (buffer[pos++] >= 0) {
					this.bufferPos = pos;
					return;
				}
			}
		}
		this.skipRawVarintSlowPath();
	}

	private void skipRawVarintSlowPath() throws IOException {
		for (int i = 0; i < 10; i++) {
			if (this.readRawByte() >= 0) {
				return;
			}
		}
		throw InvalidProtocolBufferException.malformedVarint();
	}

	/** Read a raw Varint from the stream. */
	public long readRawVarint64() throws IOException {
		// Implementation notes:
		//
		// Optimized for one-byte values, expected to be common.
		// The particular code below was selected from various candidates
		// empirically, by winning VarintBenchmark.
		//
		// Sign extension of (signed) Java bytes is usually a nuisance, but
		// we exploit it here to more easily obtain the sign of bytes read.
		// Instead of cleaning up the sign extension bits by masking eagerly,
		// we delay until we find the final (positive) byte, when we clear all
		// accumulated bits with one xor. We depend on javac to constant fold.
		fastpath: {
			int pos = this.bufferPos;

			if (this.bufferSize == pos) {
				break fastpath;
			}

			final byte[] buffer = this.buffer;
			long x;
			int y;
			if ((y = buffer[pos++]) >= 0) {
				this.bufferPos = pos;
				return y;
			} else if (this.bufferSize - pos < 9) {
				break fastpath;
			} else if ((x = y ^ buffer[pos++] << 7) < 0L) {
				x ^= ~0L << 7;
			} else if ((x ^= buffer[pos++] << 14) >= 0L) {
				x ^= ~0L << 7 ^ ~0L << 14;
			} else if ((x ^= buffer[pos++] << 21) < 0L) {
				x ^= ~0L << 7 ^ ~0L << 14 ^ ~0L << 21;
			} else if ((x ^= (long) buffer[pos++] << 28) >= 0L) {
				x ^= ~0L << 7 ^ ~0L << 14 ^ ~0L << 21 ^ ~0L << 28;
			} else if ((x ^= (long) buffer[pos++] << 35) < 0L) {
				x ^= ~0L << 7 ^ ~0L << 14 ^ ~0L << 21 ^ ~0L << 28 ^ ~0L << 35;
			} else if ((x ^= (long) buffer[pos++] << 42) >= 0L) {
				x ^= ~0L << 7 ^ ~0L << 14 ^ ~0L << 21 ^ ~0L << 28 ^ ~0L << 35
						^ ~0L << 42;
			} else if ((x ^= (long) buffer[pos++] << 49) < 0L) {
				x ^= ~0L << 7 ^ ~0L << 14 ^ ~0L << 21 ^ ~0L << 28 ^ ~0L << 35
						^ ~0L << 42 ^ ~0L << 49;
			} else {
				x ^= (long) buffer[pos++] << 56;
				x ^= ~0L << 7 ^ ~0L << 14 ^ ~0L << 21 ^ ~0L << 28 ^ ~0L << 35
						^ ~0L << 42 ^ ~0L << 49 ^ ~0L << 56;
				if (x < 0L) {
					if (buffer[pos++] < 0L) {
						break fastpath; // Will throw malformedVarint()
					}
				}
			}
			this.bufferPos = pos;
			return x;
		}
		return this.readRawVarint64SlowPath();
	}

	/** Variant of readRawVarint64 for when uncomfortably close to the limit. */
	/* Visible for testing */
	long readRawVarint64SlowPath() throws IOException {
		long result = 0;
		for (int shift = 0; shift < 64; shift += 7) {
			final byte b = this.readRawByte();
			result |= (long) (b & 0x7F) << shift;
			if ((b & 0x80) == 0) {
				return result;
			}
		}
		throw InvalidProtocolBufferException.malformedVarint();
	}

	/** Read a 32-bit little-endian integer from the stream. */
	public int readRawLittleEndian32() throws IOException {
		int pos = this.bufferPos;

		// hand-inlined ensureAvailable(4);
		if (this.bufferSize - pos < 4) {
			this.refillBuffer(4);
			pos = this.bufferPos;
		}

		final byte[] buffer = this.buffer;
		this.bufferPos = pos + 4;
		return buffer[pos] & 0xff | (buffer[pos + 1] & 0xff) << 8
				| (buffer[pos + 2] & 0xff) << 16
				| (buffer[pos + 3] & 0xff) << 24;
	}

	/** Read a 64-bit little-endian integer from the stream. */
	public long readRawLittleEndian64() throws IOException {
		int pos = this.bufferPos;

		// hand-inlined ensureAvailable(8);
		if (this.bufferSize - pos < 8) {
			this.refillBuffer(8);
			pos = this.bufferPos;
		}

		final byte[] buffer = this.buffer;
		this.bufferPos = pos + 8;
		return buffer[pos] & 0xffL | (buffer[pos + 1] & 0xffL) << 8
				| (buffer[pos + 2] & 0xffL) << 16
				| (buffer[pos + 3] & 0xffL) << 24
				| (buffer[pos + 4] & 0xffL) << 32
				| (buffer[pos + 5] & 0xffL) << 40
				| (buffer[pos + 6] & 0xffL) << 48
				| (buffer[pos + 7] & 0xffL) << 56;
	}

	/**
	 * Decode a ZigZag-encoded 32-bit value. ZigZag encodes signed integers into
	 * values that can be efficiently encoded with varint. (Otherwise, negative
	 * values must be sign-extended to 64 bits to be varint encoded, thus always
	 * taking 10 bytes on the wire.)
	 * 
	 * @param n
	 *            An unsigned 32-bit integer, stored in a signed int because
	 *            Java has no explicit unsigned support.
	 * @return A signed 32-bit integer.
	 */
	public static int decodeZigZag32(final int n) {
		return n >>> 1 ^ -(n & 1);
	}

	/**
	 * Decode a ZigZag-encoded 64-bit value. ZigZag encodes signed integers into
	 * values that can be efficiently encoded with varint. (Otherwise, negative
	 * values must be sign-extended to 64 bits to be varint encoded, thus always
	 * taking 10 bytes on the wire.)
	 * 
	 * @param n
	 *            An unsigned 64-bit integer, stored in a signed int because
	 *            Java has no explicit unsigned support.
	 * @return A signed 64-bit integer.
	 */
	public static long decodeZigZag64(final long n) {
		return n >>> 1 ^ -(n & 1);
	}

	// -----------------------------------------------------------------

	private byte[] buffer;
	private int bufferSize;
	private int bufferSizeAfterLimit;
	private int bufferPos;
	private int lastTag;
	/**
	 * The total number of bytes read before the current buffer. The total bytes
	 * read up to the current position can be computed as
	 * {@code totalBytesRetired + bufferPos}. This value may be negative if
	 * reading started in the middle of the current buffer (e.g. if the
	 * constructor that takes a byte array and an offset was used).
	 */
	private int totalBytesRetired;

	/** The absolute position of the end of the current message. */
	private int currentLimit = Integer.MAX_VALUE;

	private static final int BUFFER_SIZE = 4096;

	static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	static final int WIRETYPE_VARINT = 0;
	static final int WIRETYPE_FIXED64 = 1;
	static final int WIRETYPE_LENGTH_DELIMITED = 2;
	static final int WIRETYPE_START_GROUP = 3;
	static final int WIRETYPE_END_GROUP = 4;
	static final int WIRETYPE_FIXED32 = 5;

	static final int TAG_TYPE_BITS = 3;
	static final int TAG_TYPE_MASK = (1 << TAG_TYPE_BITS) - 1;

	/** Given a tag value, determines the wire type (the lower 3 bits). */
	public static int getTagWireType(final int tag) {
		return tag & TAG_TYPE_MASK;
	}

	/** Given a tag value, determines the field number (the upper 29 bits). */
	public static int getTagFieldNumber(final int tag) {
		return tag >>> TAG_TYPE_BITS;
	}

	/** Makes a tag value given a field number and wire type. */
	static int makeTag(final int fieldNumber, final int wireType) {
		return fieldNumber << TAG_TYPE_BITS | wireType;
	}

	private CodedInputStream(final byte[] buffer, final int off, final int len) {
		this.buffer = buffer;
		this.bufferSize = off + len;
		this.bufferPos = off;
		this.totalBytesRetired = -off;
	}
	

	public void reset(final byte[] buf) {
		this.reset(buf, 0, buf.length);
	}
	
	public void reset(final byte[] buffer, final int off, final int len) {
		this.buffer = buffer;
		this.bufferSize = off + len;
		this.bufferPos = off;
		this.totalBytesRetired = -off;
		
		this.bufferSizeAfterLimit = 0;
		
		this.currentLimit = Integer.MAX_VALUE;
		
		this.lastTag = 0;
		
		try {
			// Some uses of CodedInputStream can be more efficient if they know
			// exactly how many bytes are available. By pushing the end point of
			// the
			// buffer as a limit, we allow them to get this information via
			// getBytesUntilLimit(). Pushing a limit that we know is at the end
			// of
			// the stream can never hurt, since we can never past that point
			// anyway.
			this.pushLimit(len);
		} catch (final InvalidProtocolBufferException ex) {
			// The only reason pushLimit() might throw an exception here is if
			// len
			// is negative. Normally pushLimit()'s parameter comes directly off
			// the
			// wire, so it's important to catch exceptions in case of corrupt or
			// malicious data. However, in this case, we expect that len is not
			// a
			// user-supplied value, so we can assume that it being negative
			// indicates
			// a programming error. Therefore, throwing an unchecked exception
			// is
			// appropriate.
			throw new IllegalArgumentException(ex);
		}
	}


	/**
	 * Resets the current size counter to zero (see {@link #setSizeLimit(int)}).
	 */
	public void resetSizeCounter() {
		this.totalBytesRetired = -this.bufferPos;
	}

	/**
	 * Sets {@code currentLimit} to (current position) + {@code byteLimit}. This
	 * is called when descending into a length-delimited embedded message.
	 * 
	 * <p>
	 * Note that {@code pushLimit()} does NOT affect how many bytes the
	 * {@code CodedInputStream} reads from an underlying {@code InputStream}
	 * when refreshing its buffer. If you need to prevent reading past a certain
	 * point in the underlying {@code InputStream} (e.g. because you expect it
	 * to contain more data after the end of the message which you need to
	 * handle differently) then you must place a wrapper around your
	 * {@code InputStream} which limits the amount of data that can be read from
	 * it.
	 * 
	 * @return the old limit.
	 */
	public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
		if (byteLimit < 0) {
			throw InvalidProtocolBufferException.negativeSize();
		}
		byteLimit += this.totalBytesRetired + this.bufferPos;
		final int oldLimit = this.currentLimit;
		if (byteLimit > oldLimit) {
			throw InvalidProtocolBufferException.truncatedMessage();
		}
		this.currentLimit = byteLimit;

		this.recomputeBufferSizeAfterLimit();

		return oldLimit;
	}

	private void recomputeBufferSizeAfterLimit() {
		this.bufferSize += this.bufferSizeAfterLimit;
		final int bufferEnd = this.totalBytesRetired + this.bufferSize;
		if (bufferEnd > this.currentLimit) {
			// Limit is in current buffer.
			this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
			this.bufferSize -= this.bufferSizeAfterLimit;
		} else {
			this.bufferSizeAfterLimit = 0;
		}
	}

	/**
	 * Discards the current limit, returning to the previous limit.
	 * 
	 * @param oldLimit
	 *            The old limit, as returned by {@code pushLimit}.
	 */
	public void popLimit(final int oldLimit) {
		this.currentLimit = oldLimit;
		this.recomputeBufferSizeAfterLimit();
	}

	/**
	 * Returns the number of bytes to be read before the current limit. If no
	 * limit is set, returns -1.
	 */
	public int getBytesUntilLimit() {
		if (this.currentLimit == Integer.MAX_VALUE) {
			return -1;
		}

		final int currentAbsolutePosition = this.totalBytesRetired + this.bufferPos;
		return this.currentLimit - currentAbsolutePosition;
	}

	/**
	 * Returns true if the stream has reached the end of the input. This is the
	 * case if either the end of the underlying input source has been reached or
	 * if the stream has reached a limit created using {@link #pushLimit(int)}.
	 */
	public boolean isAtEnd() throws IOException {
		return this.bufferPos == this.bufferSize && !this.tryRefillBuffer(1);
	}

	/**
	 * The total bytes read up to the current position. Calling
	 * {@link #resetSizeCounter()} resets this value to zero.
	 */
	public int getTotalBytesRead() {
		return this.totalBytesRetired + this.bufferPos;
	}

	/**
	 * Ensures that at least {@code n} bytes are available in the buffer,
	 * reading more bytes from the input if necessary to make it so. Caller must
	 * ensure that the requested space is less than BUFFER_SIZE.
	 * 
	 * @throws InvalidProtocolBufferException
	 *             The end of the stream or the current limit was reached.
	 */
	private void ensureAvailable(final int n) throws IOException {
		if (this.bufferSize - this.bufferPos < n) {
			this.refillBuffer(n);
		}
	}

	/**
	 * Reads more bytes from the input, making at least {@code n} bytes
	 * available in the buffer. Caller must ensure that the requested space is
	 * not yet available, and that the requested space is less than BUFFER_SIZE.
	 * 
	 * @throws InvalidProtocolBufferException
	 *             The end of the stream or the current limit was reached.
	 */
	private void refillBuffer(final int n) throws IOException {
		if (!this.tryRefillBuffer(n)) {
			throw InvalidProtocolBufferException.truncatedMessage();
		}
	}

	/**
	 * Tries to read more bytes from the input, making at least {@code n} bytes
	 * available in the buffer. Caller must ensure that the requested space is
	 * not yet available, and that the requested space is less than BUFFER_SIZE.
	 * 
	 * @return {@code true} if the bytes could be made available; {@code false}
	 *         if the end of the stream or the current limit was reached.
	 */
	private boolean tryRefillBuffer(final int n) throws IOException {
		if (this.bufferPos + n <= this.bufferSize) {
			throw new IllegalStateException("refillBuffer() called when " + n
					+ " bytes were already available in buffer");
		}

		if (this.totalBytesRetired + this.bufferPos + n > this.currentLimit) {
			// Oops, we hit a limit.
			return false;
		}

		return false;
	}

	/**
	 * Read one byte from the input.
	 * 
	 * @throws InvalidProtocolBufferException
	 *             The end of the stream or the current limit was reached.
	 */
	public byte readRawByte() throws IOException {
		if (this.bufferPos == this.bufferSize) {
			this.refillBuffer(1);
		}
		return this.buffer[this.bufferPos++];
	}

	/**
	 * Read a fixed size of bytes from the input.
	 * 
	 * @throws InvalidProtocolBufferException
	 *             The end of the stream or the current limit was reached.
	 */
	public byte[] readRawBytes(final int size) throws IOException {
		final int pos = this.bufferPos;
		if (size <= this.bufferSize - pos && size > 0) {
			this.bufferPos = pos + size;
			return Arrays.copyOfRange(this.buffer, pos, pos + size);
		} else {
			return this.readRawBytesSlowPath(size);
		}
	}

	/**
	 * Exactly like readRawBytes, but caller must have already checked the fast
	 * path: (size <= (bufferSize - pos) && size > 0)
	 */
	private byte[] readRawBytesSlowPath(final int size) throws IOException {
		if (size <= 0) {
			if (size == 0) {
				return EMPTY_BYTE_ARRAY;
			} else {
				throw InvalidProtocolBufferException.negativeSize();
			}
		}

		if (this.totalBytesRetired + this.bufferPos + size > this.currentLimit) {
			// Read to the end of the stream anyway.
			this.skipRawBytes(this.currentLimit - this.totalBytesRetired
					- this.bufferPos);
			// Then fail.
			throw InvalidProtocolBufferException.truncatedMessage();
		}

		if (size < BUFFER_SIZE) {
			// Reading more bytes than are in the buffer, but not an excessive
			// number
			// of bytes. We can safely allocate the resulting array ahead of
			// time.

			// First copy what we have.
			final byte[] bytes = new byte[size];
			final int pos = this.bufferSize - this.bufferPos;
			System.arraycopy(this.buffer, this.bufferPos, bytes, 0, pos);
			this.bufferPos = this.bufferSize;

			// We want to refill the buffer and then copy from the buffer into
			// our
			// byte array rather than reading directly into our byte array
			// because
			// the input may be unbuffered.
			this.ensureAvailable(size - pos);
			System.arraycopy(this.buffer, 0, bytes, pos, size - pos);
			this.bufferPos = size - pos;

			return bytes;
		} else {// XXX 這邊基本上不會用到
			// The size is very large. For security reasons, we can't allocate
			// the
			// entire byte array yet. The size comes directly from the input, so
			// a
			// maliciously-crafted message could provide a bogus very large size
			// in
			// order to trick the app into allocating a lot of memory. We avoid
			// this
			// by allocating and reading only a small chunk at a time, so that
			// the
			// malicious message must actually *be* extremely large to cause
			// problems. Meanwhile, we limit the allowed size of a message
			// elsewhere.

			// Remember the buffer markers since we'll have to copy the bytes
			// out of
			// it later.
			final int originalBufferPos = this.bufferPos;
			final int originalBufferSize = this.bufferSize;

			// Mark the current buffer consumed.
			this.totalBytesRetired += this.bufferSize;
			this.bufferPos = 0;
			this.bufferSize = 0;

			// Read all the rest of the bytes we need.
			int sizeLeft = size - (originalBufferSize - originalBufferPos);
			final List<byte[]> chunks = new ArrayList<byte[]>();

			while (sizeLeft > 0) {
				final byte[] chunk = new byte[Math.min(sizeLeft, BUFFER_SIZE)];
				final int pos = 0;
				while (pos < chunk.length) {
					throw InvalidProtocolBufferException.truncatedMessage();
					/*
					 * final int n = this.input == null ? -1 : this.input.read(
					 * chunk, pos, chunk.length - pos); if (n == -1) { throw
					 * InvalidProtocolBufferException.truncatedMessage(); }
					 * this.totalBytesRetired += n; pos += n;
					 */
				}
				sizeLeft -= chunk.length;
				chunks.add(chunk);
			}

			// OK, got everything. Now concatenate it all into one buffer.
			final byte[] bytes = new byte[size];

			// Start by copying the leftover bytes from this.buffer.
			int pos = originalBufferSize - originalBufferPos;
			System.arraycopy(this.buffer, originalBufferPos, bytes, 0, pos);

			// And now all the chunks.
			for (final byte[] chunk : chunks) {
				System.arraycopy(chunk, 0, bytes, pos, chunk.length);
				pos += chunk.length;
			}

			// Done.
			return bytes;
		}
	}

	/**
	 * Reads and discards {@code size} bytes.
	 * 
	 * @throws InvalidProtocolBufferException
	 *             The end of the stream or the current limit was reached.
	 */
	public void skipRawBytes(final int size) throws IOException {
		if (size <= this.bufferSize - this.bufferPos && size >= 0) {
			// We have all the bytes we need already.
			this.bufferPos += size;
		} else {
			this.skipRawBytesSlowPath(size);
		}
	}

	/**
	 * Exactly like skipRawBytes, but caller must have already checked the fast
	 * path: (size <= (bufferSize - pos) && size >= 0)
	 */
	private void skipRawBytesSlowPath(final int size) throws IOException {
		if (size < 0) {
			throw InvalidProtocolBufferException.negativeSize();
		}

		if (this.totalBytesRetired + this.bufferPos + size > this.currentLimit) {
			// System.out.println("totalBytesRetired:" +
			// this.totalBytesRetired);
			// System.out.println("bufferPos:" + this.bufferPos);
			// System.out.println("size:" + size);
			// System.out.println("currentLimit:" + this.currentLimit);
			// Read to the end of the stream anyway.
			this.skipRawBytes(this.currentLimit - this.totalBytesRetired
					- this.bufferPos);
			// Then fail.
			throw InvalidProtocolBufferException.truncatedMessage();
		}

		// Skipping more bytes than are in the buffer. First skip what we have.
		int pos = this.bufferSize - this.bufferPos;
		this.bufferPos = this.bufferSize;

		// Keep refilling the buffer until we get to the point we wanted to skip
		// to.
		// This has the side effect of ensuring the limits are updated
		// correctly.
		this.refillBuffer(1);
		while (size - pos > this.bufferSize) {
			pos += this.bufferSize;
			this.bufferPos = this.bufferSize;
			this.refillBuffer(1);
		}

		this.bufferPos = size - pos;
	}
	
	/**
	 * 返回一個指定的byte陣列轉換為Hex字串
	 * 
	 * @param byteArray 指定的byte陣列
	 * @return 轉換後的Hex字串 格式: XX:XX:XX:XX:XX..
	 */
	private static String bytesToHex(final byte[] byteArray) {
		final StringBuffer sb = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < byteArray.length; n++) {
			stmp = Integer.toHexString(byteArray[n] & 0XFF);
			if (stmp.length() == 1) {
				sb.append("0").append(stmp);
			} else {
				sb.append(stmp);
			}
			if (n < byteArray.length - 1) {
				sb.append(":");
			}
		}
		return sb.toString();
	}

	
}
