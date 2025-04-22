package com.lineage.server.templates;

import java.sql.Timestamp;

public final class L1Mail { // repaired by terry0412

	public L1Mail() {
	}

	private int _id;

	public int getId() {
		return _id;
	}

	public void setId(final int i) {
		_id = i;
	}

	private int _type;

	public int getType() {
		return _type;
	}

	public void setType(final int i) {
		_type = i;
	}

	private String _senderName;

	public String getSenderName() {
		return _senderName;
	}

	public void setSenderName(final String s) {
		_senderName = s;
	}

	private String _receiverName;

	public String getReceiverName() {
		return _receiverName;
	}

	public void setReceiverName(final String s) {
		_receiverName = s;
	}

	/**
	 * 發信日期 (修正 by terry0412)
	 */
	private Timestamp _date;

	public Timestamp getDate() {
		return _date;
	}

	public void setDate(final Timestamp date) {
		_date = date;
	}

	private int _readStatus;

	public int getReadStatus() {
		return _readStatus;
	}

	public void setReadStatus(final int i) {
		_readStatus = i;
	}

	private byte[] _subject;

	public byte[] getSubject() {
		return _subject;
	}

	public void setSubject(final byte[] arg) {
		_subject = arg;
	}

	private byte[] _content;

	public byte[] getContent() {
		return _content;
	}

	public void setContent(final byte[] arg) {
		_content = arg;
	}

	/**
	 * 信件發送類型 (0:發信 1:收信) by terry0412
	 */
	private int _send_type;

	public int getSendType() {
		return _send_type;
	}

	public void setSendType(final int i) {
		_send_type = i;
	}
}
