package com.lineage.server.serverpackets;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1Item;

/**
 * 交易增加物品
 * 
 * @author dexc
 */
public class S_TradeAddItem extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**
	 * 交易增加物品
	 * 
	 * @param item
	 * @param count
	 * @param type
	 */
	public S_TradeAddItem(final L1ItemInstance item, final long count, final int type) {
		// 0000: 21 00 3e 01 24 37 36 37 00 03 00 b7 32 b3 9c 2f
		// !.>.$767....2../
		writeC(S_OPCODE_TRADEADDITEM);
		writeC(type); // 0:交易視窗上半部 1:交易視窗下半部
		writeH(item.getItem().getGfxId());

		final String name = item.getNumberedViewName(count);

		writeS(name);
		// 0:祝福
		// 1:通常
		// 2:詛咒
		// 3:未鑑定
		// 128:祝福&封印
		// 129:&封印
		// 130:詛咒&封印
		// 131:未鑑定&封印
		if (!item.isIdentified()) {
			writeC(0x03);
			writeC(0x00);
		} else {
			writeC(item.getBless());
			final byte status[] = item.getStatusBytes();
			writeC(status.length);
			byte abyte0[];
			final int j = (abyte0 = status).length;
			for (int i = 0; i < j; i++) {
				final byte b = abyte0[i];
				writeC(b);
			}
		}
		/*writeC(item.getBless());
		final L1Item template = ItemTable.get().getTemplate(item.getItemId());
		if (template == null) {
			writeC(0);
		} else {
			final byte status[] = item.getStatusBytes();
			writeC(status.length);
			byte abyte0[];
			final int j = (abyte0 = status).length;
			for (int i = 0; i < j; i++) {
				final byte b = abyte0[i];
				writeC(b);
			}
		}*/
	}

	/**
	 * 交易增加物品 - 測試
	 */
	public S_TradeAddItem() {
		writeC(S_OPCODE_TRADEADDITEM);
		writeC(0x01); // 0:交易視窗上半部 1:交易視窗下半部
		writeH(714);// 惡魔頭盔
		writeS("測試物品(55)");
		// 0:祝福
		writeC(0x00);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
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
