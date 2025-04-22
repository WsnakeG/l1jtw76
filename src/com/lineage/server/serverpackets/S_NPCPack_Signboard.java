package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1SignboardInstance;

/**
 * 物件封包 - 告示
 * 
 * @author dexc
 */
public class S_NPCPack_Signboard extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件封包 - 告示
	 * 
	 * @param signboard
	 */
	public S_NPCPack_Signboard(final L1SignboardInstance signboard) {
		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為 S_OPCODE_OBJECTPACK
		writeH(signboard.getX()); // X
		writeH(signboard.getY()); // Y
		writeD(signboard.getId()); // OBJID
		writeH(signboard.getGfxId()); // GFXID
		writeC(0x00); // 物件外觀屬性
		writeC(getDirection(signboard.getHeading())); // 方向
		writeC(0x00); // 亮度 0:normal, 1:fast, 2:slow
		writeC(0x00); // 速度
		writeD(0x00000000); // 數量, 經驗值
		writeH(0x0000); // 正義質
		writeS(null); // 名稱
		writeS(signboard.getName()); // 封號
		writeC(0x00); // 狀態
		writeD(0x00000000); // 血盟OBJID
		writeS(null); // 血盟名稱
		writeS(null); // 主人名稱

		// 0:NPC,道具
		// 1:中毒 ,
		// 2:隱身
		// 4:人物
		// 8:詛咒
		// 16:勇水
		// 32:??
		// 64:??(??)
		// 128:invisible but name
		writeC(0x00); // 物件分類

		writeC(0xFF); // HP顯示
		writeC(0x00); // タルクック距離(通り)
		writeC(0x00); // LV
		writeC(0x00);
		writeC(0xFF);
		writeC(0xFF);
	}

	private int getDirection(final int heading) {
		int dir = 0;
		switch (heading) {
		case 2:
			dir = 1;
			break;
		case 3:
			dir = 2;
			break;
		case 4:
			dir = 3;
			break;
		case 6:
			dir = 4;
			break;
		case 7:
			dir = 5;
			break;
		}
		return dir;
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

