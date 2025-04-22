package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 更新物品使用狀態(背包)-可用次數
 * 
 * @author DaiEn
 */
public class S_ItemAmount extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 更新物品使用狀態(背包)-可用次數
	 * 
	 * @param item
	 */
	public S_ItemAmount(final L1ItemInstance item, final int test) {
		if (item == null) {
			return;
		}

		buildPacket(item);
	}

	private void buildPacket(final L1ItemInstance item) {
		writeC(S_OPCODE_ITEMSTATUS);  // XXX S_OPCODE_ITEMAMOUNT 修改為 S_OPCODE_ITEMSTATUS
		writeD(item.getId());
		writeS(item.getViewName());

		// 定義數量顯示
		/*
		 * int count = 0; if (item.getItem().getMaxChargeCount() > 0) { count =
		 * item.getChargeCount(); } else { count = (int)
		 * Math.min(item.getCount(), 2000000000); }
		 */

		// 定義數量顯示
		final int count = (int) Math.min(item.getCount(), 2000000000);
		// 數量
		writeD(count);

		// 可用數量
		// this.writeD(Math.min(item.getChargeCount(), 2000000000));
		// this.writeC(0x00);
		if (!item.isIdentified()) {
			// 未鑑定 不發送詳細資料
			writeC(0x00);

		} else {
			/*final byte[] status = item.getStatusBytes();
			writeC(status.length);
			for (final byte b : status) {
				writeC(b);
			}*/
			final byte status[] = item.getStatusBytes();
			writeC(status.length);
			byte abyte0[];
			final int j = (abyte0 = status).length;
			for (int i = 0; i < j; i++) {
				final byte b = abyte0[i];
				writeC(b);
			}
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
