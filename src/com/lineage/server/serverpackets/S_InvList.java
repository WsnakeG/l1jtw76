package com.lineage.server.serverpackets;

import java.util.List;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 物品名单(背包)
 * 
 * @author dexc
 */
public class S_InvList extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物品名单(背包)
	 */
	public S_InvList(final List<L1ItemInstance> items) {
		writeC(S_OPCODE_INVLIST);
		writeC(items.size()); // 道具数量

		for (final L1ItemInstance item : items) {
			writeD(item.getId());
			writeH(item.getItem().getItemDescId());

			int type = item.getItem().getUseType();
			if (type < 0) {
				type = 0;
			}
			if ((type == 96) || (type >= 98)) {
				writeC(26);
			} else if (type == 97) {
				writeC(27);
			} else {
				writeC(type);// 使用类型
			}
			if (item.getChargeCount() <= 0) {
				writeC(0x00);// 可用次数

			} else {
				writeC(item.getChargeCount());// 可用次数
			}

			writeH(item.get_gfxid());// 图示
			writeC(item.getBless());// 祝福状态

			writeExp(item.getCount());// 数量
			int statusX = 0;
			if (item.isIdentified()) {
				statusX |= 1;
			}
			if (!item.getItem().isTradable()) {
				statusX |= 2;
			}
			if (item.getItem().isCantDelete()) {
				statusX |= 4;
			}
			if ((item.getItem().get_safeenchant() < 0) || (item.getItem().getUseType() == -3)
					|| (item.getItem().getUseType() == -2)) {
				statusX |= 8;
			}
			if (item.getBless() >= 128) {
				statusX = 32;
				if (item.isIdentified()) {
					statusX |= 1;
					statusX |= 2;
					statusX |= 4;
					statusX |= 8;
				} else {
					statusX |= 2;
					statusX |= 4;
					statusX |= 8;
				}
			}
			writeC(statusX);
			writeS(item.getViewName());// 名称
			if (!item.isIdentified()) {
				// 未见定状态 不需发送详细资料
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
			writeC(0x17);
			writeC(0);
			//writeH(0);
			//writeH(0);
			this.writeC(0x00);
			this.writeC(0x00);
			this.writeC(0x00);
			this.writeC(0x00);
			if (item.getItem().getType() == 10) { // 如果是法書，傳出法術編號
				writeC(0);
			} else {
				writeC(item.getEnchantLevel()); // 物品武捲等級
			}
			writeD(item.getId()); // 3.80 物品世界流水編號
			//writeD(0);
			//writeD(0);
			//writeD(item.getBless() >= 128 ? 3 : item.getItem().isTradable() ? 7 : 2); // 7:可刪除,																		// 封印狀態
			//writeC(0);
			this.writeC(0x00);
			this.writeC(0x00);
			this.writeC(0x00);
			this.writeC(0x00);
			this.writeC(0x00);
			this.writeC(0x00);
			this.writeC(0x00);
			this.writeC(0x00);		
			// 380tw add
			// 可交易
			if (item.getItem().isTradable()) {
				this.writeC(0x07);
			// 不可交易 可存倉	
			} else if (!item.getItem().isTradable()) {
				this.writeC(0x03);
			} else {// 不可交易 不可存倉..
				this.writeC(0x02);
			}		
			this.writeC(0x00);
			this.writeC(0x00);
			this.writeC(0x00);
			this.writeC(0x00);

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
