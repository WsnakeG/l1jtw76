package com.lineage.server.utils;

import java.io.IOException;

/**
 * Thrown when a protocol message being parsed is invalid in some way, e.g. it
 * contains a malformed varint or a negative byte length.
 * 
 * @author kenton@google.com Kenton Varda
 */
public class InvalidProtocolBufferException extends IOException {

	private static final long serialVersionUID = -1616151763072450476L;

	public InvalidProtocolBufferException(final String description) {
		super(description);
	}



	static InvalidProtocolBufferException truncatedMessage() {
		return new InvalidProtocolBufferException(
				"While parsing a protocol message, the input ended unexpectedly "
						+ "in the middle of a field.  This could mean either than the "
						+ "input has been truncated or that an embedded message "
						+ "misreported its own length.");
	}

	static InvalidProtocolBufferException negativeSize() {
		return new InvalidProtocolBufferException(
				"CodedInputStream encountered an embedded string or message "
						+ "which claimed to have negative size.");
	}

	static InvalidProtocolBufferException malformedVarint() {
		return new InvalidProtocolBufferException(
				"CodedInputStream encountered a malformed varint.");
	}

	/**
	 * 協議消息中包含的無效標記(0)
	 * 
	 * @return
	 */
	static InvalidProtocolBufferException invalidTag() {
		return new InvalidProtocolBufferException(
				"Protocol message contained an invalid tag (zero).");
	}

	static InvalidProtocolBufferException invalidEndTag() {
		return new InvalidProtocolBufferException(
				"Protocol message end-group tag did not match expected tag.");
	}

	static InvalidProtocolBufferException invalidWireType() {
		return new InvalidProtocolBufferException(
				"Protocol message tag had invalid wire type.");
	}

	static InvalidProtocolBufferException recursionLimitExceeded() {
		return new InvalidProtocolBufferException(
				"Protocol message had too many levels of nesting.  May be malicious.  "
						+ "Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
	}

	static InvalidProtocolBufferException sizeLimitExceeded() {
		return new InvalidProtocolBufferException(
				"Protocol message was too large.  May be malicious.  "
						+ "Use CodedInputStream.setSizeLimit() to increase the size limit.");
	}

	static InvalidProtocolBufferException parseFailure() {
		return new InvalidProtocolBufferException(
				"Failed to parse the message.");
	}
	
	static InvalidProtocolBufferException badWriteType(final String string) {
		return new InvalidProtocolBufferException(
				"非法屬性:" + string);
	}

}
