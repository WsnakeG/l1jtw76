package com.lineage.server.serverpackets;

import com.lineage.server.datatables.lock.BoardReading;
import com.lineage.server.templates.L1Board;

/**
 * 佈告欄內容
 * 
 * @author dexc
 */
public class S_BoardRead extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 佈告欄內容
	 * 
	 * @param number
	 */
	public S_BoardRead(final int number) {
		buildPacket(number);
	}

	private void buildPacket(final int number) {
		final L1Board board = BoardReading.get().getBoardTable(number);
		writeC(S_OPCODE_BOARDREAD);
		writeD(board.get_id());
		writeS(board.get_name());
		writeS(board.get_date());
		writeS(board.get_title());
		writeS(board.get_content());
	}

	/**
	 * 佈告欄內容 - 測試
	 * 
	 * @param number
	 */
	public S_BoardRead() {
		writeC(S_OPCODE_BOARDREAD);
		writeD(10);
		writeS("測試NAME");
		writeS("2010-02-02");
		writeS("測試TITLE");
		writeS("測試內容");
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public byte[] getContentBIG5() { //20240901
		if (_byte == null) {
			_byte = _bao3.toByteArray();
		}
		return _byte;
	}
	
	@Override
	public byte[] getContentGBK() { //20240901
		if (_byte == null) {
			_byte = _bao5.toByteArray();
		}
		return _byte;
	}
}
