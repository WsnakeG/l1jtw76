package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_PacketBoxCooking extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * <font color=#00800>料理選單</font>
	 */
	public static final int COOK_WINDOW = 52;

	/** writeByte(type) writeShort(time): 料理アイコンが表示される */
	public static final int ICON_COOKING = 53;

	public S_PacketBoxCooking(final int value) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(COOK_WINDOW);
		writeC(0xdb); // ?
		writeC(0x31);
		writeC(0xdf);
		writeC(0x02);
		writeC(0x01);
		writeC(value); // level
	}

	public S_PacketBoxCooking(final L1PcInstance pc, final int type, final int time) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(ICON_COOKING);
		// 0000: 79 35 15 19 12 0c 0e 0c d0 07 31 24 84 03 85 16
		int food = (pc.get_food() * 10) - 250;
		if (food < 0) {
			food = 0;
		}
		switch (type) {
		case 0x07:
			writeC(pc.getStr());// str
			writeC(pc.getInt());// int
			writeC(pc.getWis());// wis
			writeC(pc.getDex());// dex
			writeC(pc.getCon());// con
			writeC(pc.getCha());// cha
			writeH(food);
			writeC(type);// 類型
			writeC(0x24);
			writeH(time);// 時間
			writeC(0x0);// 負重
			break;

		case 54:
			writeC(0x00);// str
			writeC(0x00);// int
			writeC(0x00);// wis
			writeC(0x00);// dex
			writeC(0x00);// con
			writeC(0x00);// cha
			writeH(0x00);// 飽食
			writeC(type);// 類型
			writeC(0x2a);
			writeH(time);// 時間
			writeC(0x0);// 負重
			break;

		default:
			writeC(pc.getStr());// str
			writeC(pc.getInt());// int
			writeC(pc.getWis());// wis
			writeC(pc.getDex());// dex
			writeC(pc.getCon());// con
			writeC(pc.getCha());// cha
			writeH(food);
			writeC(type);// 類型
			writeC(0x26);
			writeH(time);// 時間
			writeC(0x0);// 負重
			break;
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
