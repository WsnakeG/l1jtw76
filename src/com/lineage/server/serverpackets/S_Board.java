package com.lineage.server.serverpackets;

import java.util.ArrayList;

import com.lineage.server.datatables.lock.BoardReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1Board;

/**
 * 佈告欄列表
 * 
 * @author dexc
 */
public class S_Board extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 佈告欄列表
	 * 
	 * @param npc
	 */
	public S_Board(final L1NpcInstance npc) {
		buildPacket(npc, 0);
	}

	/**
	 * 佈告欄列表
	 * 
	 * @param npc
	 * @param number
	 */
	public S_Board(final L1NpcInstance npc, final int number) {
		buildPacket(npc, number);
	}

	private void buildPacket(final L1NpcInstance npc, final int number) {
		int count = 0;

		final ArrayList<L1Board> showList = new ArrayList<L1Board>();

		int maxid = BoardReading.get().getMaxId();
		while ((count < 8) && (maxid > 0)) {
			final L1Board boardInfo = BoardReading.get().getBoardTable(maxid--);
			if (boardInfo != null) {
				if ((boardInfo.get_id() <= number) || (number == 0)) {
					showList.add(count, boardInfo);
					count++;
				}
			}
		}

		writeC(S_OPCODE_BOARD);
		writeC(0x00); // ?
		writeD(npc.getId());
		writeC(0xff); // ?
		writeC(0xff); // ?
		writeC(0xff); // ?
		writeC(0x7f); // ?
		writeH(showList.size());
		writeH(0x012c);// 300
		for (int i = 0; i < showList.size(); i++) {
			final L1Board boardInfo = showList.get(i);
			if (boardInfo != null) {
				writeD(boardInfo.get_id());
				writeS(boardInfo.get_name());
				writeS(boardInfo.get_date());
				writeS(boardInfo.get_title());
			}
		}
	}

	/**
	 * 佈告欄列表 - 測試
	 * 
	 * @param objectid
	 */
	public S_Board(final int objectid) {
		writeC(S_OPCODE_BOARD);
		writeC(0x00); // ?
		writeD(objectid);
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0x7F); // ?
		writeH(2);
		writeH(300);

		writeD(2);
		writeS("佈告欄列表");
		writeS("2010-2-2");
		writeS("佈告欄列表TITLE");

		writeD(1);
		writeS("佈告欄列表2");
		writeS("2010-2-3");
		writeS("佈告欄列表TITLE2");
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
