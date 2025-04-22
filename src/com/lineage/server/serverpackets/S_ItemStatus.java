package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 更新物品使用狀態(背包)-數量/狀態
 * 
 * @author dexc
 */
public class S_ItemStatus extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 更新物品使用狀態(背包)-數量/狀態
	 */
	public S_ItemStatus(final L1ItemInstance item) {
		if (item == null) {
			return;
		}
		buildPacket(item);
	}

	private void buildPacket(final L1ItemInstance item) {
		writeC(S_OPCODE_ITEMSTATUS); // XXX S_OPCODE_ITEMAMOUNT 修改為 S_OPCODE_ITEMSTATUS
		writeD(item.getId());
		writeS(item.getViewName());

		// 定義數量顯示
		final int count = (int) Math.min(item.getCount(), 2000000000);
		// 數量
		writeD(count);

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

	/**
	 * 更新物品使用狀態(背包)-數量(交易專用)
	 */
	public S_ItemStatus(final L1ItemInstance item, final long count) {
		writeC(S_OPCODE_ITEMSTATUS); // XXX S_OPCODE_ITEMAMOUNT 修改為 S_OPCODE_ITEMSTATUS
		writeD(item.getId());
		writeS(item.getNumberedViewName(count));

		// 定義數量顯示
		final int out_count = (int) Math.min(count, 2000000000);
		// 數量
		writeD(out_count);

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
