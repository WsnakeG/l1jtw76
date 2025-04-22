package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 物件封包 - 地面物品
 * 
 * @author dexc
 */
public class S_NPCPack_Item extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件封包 - 地面物品
	 * 
	 * @param item
	 */
	public S_NPCPack_Item(final L1ItemInstance item) {
		buildPacket(item);
	}

	private void buildPacket(final L1ItemInstance item) {
		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為 S_OPCODE_OBJECTPACK
		writeH(item.getX());
		writeH(item.getY());
		writeD(item.getId());
		writeH(item.getItem().getGroundGfxId());
		writeC(0x00);
		writeC(0x00);
		writeC(item.isNowLighting() ? item.getItem().getLightRange() : 0x00);// 亮度
		writeC(0x00);
		writeD((int) Math.min(item.getCount(), 2000000000));// 數量
		writeH(0x0000);
		String name = "";
		if (item.getCount() > 1) {
			name = item.getItem().getNameId() + " (" + item.getCount() + ")";

		} else {
			switch (item.getItemId()) {
			case 20383: // 軍馬頭盔
			case 41235: // %i•體力魔方
			case 41236: // %i•體力魔方(受祝福)
				name = item.getItem().getNameId() + " [" + item.getChargeCount() + "]";
				break;

			case 40006: // 創造怪物魔杖
			case 140006: // 創造怪物魔杖
			case 40007: // 閃電魔杖
			case 40008: // 變形魔杖
			case 140008: // 變形魔杖
			case 40009: // 驅逐魔杖
				if (item.isIdentified()) {
					name = item.getItem().getNameId() + " (" + item.getChargeCount() + ")";
				}
				break;

			default:// 其他道具
				// 照明類
				if ((item.getItem().getLightRange() != 0) && item.isNowLighting()) {
					name = item.getItem().getNameId() + " ($10)";

				} else {
					name = item.getItem().getNameId();
				}
				break;
			}
		}
		writeS(name);
		writeS(null);
		writeC(0x00); // 狀態
		writeD(0x00000000);
		writeS(null);
		writeS(null);

		writeC(0x00); // 物件分類

		writeC(0xff); // HP
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0xff);
		writeC(0xff);
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
