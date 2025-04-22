package com.lineage.server.serverpackets;

import com.lineage.server.datatables.lock.AuctionBoardReading;
import com.lineage.server.templates.L1AuctionBoardTmp;

/**
 * 選取物品數量 (盟屋拍賣公告)
 * 
 * @author dexc
 */
public class S_ApplyAuction extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 選取物品數量 (盟屋拍賣公告)
	 * 
	 * @param objectId
	 * @param houseNumber
	 */
	public S_ApplyAuction(final int objectId, final String houseNumber) {
		buildPacket(objectId, houseNumber);
	}

	private void buildPacket(final int objectId, final String houseNumber) {
		final int number = Integer.valueOf(houseNumber);
		final L1AuctionBoardTmp board = AuctionBoardReading.get().getAuctionBoardTable(number);
		writeC(S_OPCODE_INPUTAMOUNT);
		writeD(objectId);
		writeD(0x00000000); // ?
		if (board.getBidderId() == 0) { // 無目前競標者
			writeD((int) board.getPrice()); // 數量初始質
			writeD((int) board.getPrice()); // 最低可換數量

		} else { // 具有目前競標者
			writeD((int) board.getPrice() + 1); // 數量初始質
			writeD((int) board.getPrice() + 1); // 最低可換數量
		}

		writeD(0x77359400); // 2000000000 最高可換數量
		writeH(0x0000); // ?
		writeS("agapply");// HTML
		writeS("agapply " + houseNumber);// 命令
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
