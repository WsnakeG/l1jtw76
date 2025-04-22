package com.lineage.server.serverpackets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import com.lineage.server.datatables.lock.AuctionBoardReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1AuctionBoardTmp;

/**
 * 盟屋拍賣公告欄列表
 * 
 * @author dexc
 */
public class S_AuctionBoard extends ServerBasePacket {

	private byte[] _byte = null;

	public S_AuctionBoard(final L1NpcInstance board) {
		buildPacket(board);
	}

	private void buildPacket(final L1NpcInstance board) {
		final ArrayList<L1AuctionBoardTmp> houseListX = new ArrayList<L1AuctionBoardTmp>();

		final Collection<L1AuctionBoardTmp> boardList = AuctionBoardReading.get().getAuctionBoardTableList()
				.values();
		for (final L1AuctionBoardTmp boardX : boardList) {
			final int houseId = boardX.getHouseId();
			if ((board.getX() == 33421) && (board.getY() == 32823)) { // 奇岩
				if ((houseId >= 262145) && (houseId <= 262189)) {
					houseListX.add(boardX);
				}
			} else if ((board.getX() == 33585) && (board.getY() == 33235)) { // 海音
				if ((houseId >= 327681) && (houseId <= 327691)) {
					houseListX.add(boardX);
				}
			} else if ((board.getX() == 33959) && (board.getY() == 33253)) { // 亞丁
				if ((houseId >= 458753) && (houseId <= 458819)) {
					houseListX.add(boardX);
				}
			} else if ((board.getX() == 32611) && (board.getY() == 32775)) { // 古魯丁
				if ((houseId >= 524289) && (houseId <= 524294)) {
					houseListX.add(boardX);
				}
			}
		}
		writeC(S_OPCODE_HOUSELIST);
		// this.writeC(0x00); // ?
		writeD(board.getId());
		writeH(houseListX.size()); // レコード数
		for (final L1AuctionBoardTmp boardX : houseListX) {
			writeD(boardX.getHouseId()); // 小屋編號
			writeS(boardX.getHouseName()); // 小屋名稱
			writeH(boardX.getHouseArea()); // 小屋大小
			final Calendar cal = boardX.getDeadline();
			writeC(cal.get(Calendar.MONTH) + 1); // 締切月
			writeC(cal.get(Calendar.DATE)); // 締切日
			writeD((int) boardX.getPrice()); // 現在の入札価格
		}
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
