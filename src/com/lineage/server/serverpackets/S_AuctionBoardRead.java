package com.lineage.server.serverpackets;

import java.util.Calendar;

import com.lineage.server.datatables.lock.AuctionBoardReading;
import com.lineage.server.templates.L1AuctionBoardTmp;

/**
 * 盟屋拍賣公告欄內容
 * 
 * @author dexc
 */
public class S_AuctionBoardRead extends ServerBasePacket {

	private byte[] _byte = null;

	public S_AuctionBoardRead(final int objectId, final String house_number) {
		buildPacket(objectId, house_number);
	}

	private void buildPacket(final int objectId, final String house_number) {
		final int number = Integer.valueOf(house_number);
		final L1AuctionBoardTmp board = AuctionBoardReading.get().getAuctionBoardTable(number);
		writeC(S_OPCODE_SHOWHTML);
		writeD(objectId);
		writeS("agsel");
		writeS(house_number); // アジトの番号
		writeH(0x0009); // 以下の文字列の個数
		writeS(board.getHouseName()); // アジトの名前
		writeS(board.getLocation() + "$1195"); // アジトの位置
		writeS(String.valueOf(board.getHouseArea())); // アジトの広さ
		writeS(board.getOldOwner()); // 以前の所有者
		writeS(board.getBidder()); // 現在の入札者
		writeS(String.valueOf(board.getPrice())); // 現在の入札価格
		final Calendar cal = board.getDeadline();
		final int month = cal.get(Calendar.MONTH) + 1;
		final int day = cal.get(Calendar.DATE);
		final int hour = cal.get(Calendar.HOUR_OF_DAY);
		writeS(String.valueOf(month)); // 締切月
		writeS(String.valueOf(day)); // 締切日
		writeS(String.valueOf(hour)); // 締切時
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
