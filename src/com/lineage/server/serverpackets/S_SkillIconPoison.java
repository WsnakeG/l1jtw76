package com.lineage.server.serverpackets;

/**
 * 毒麻痺負面效果圖示
 * 
 * @author KZK
 */
public class S_SkillIconPoison extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**
	 * 
	 * 
	 * 毒麻痺負面效果圖示
	 * 
	 * @param type 1 中毒狀態。 (一般毒)
	 * @param type 2 身體要被麻痺了 (麻痺毒)
	 * @param type 6 突然感覺到混亂。 (卡毒)
	 * @param time 效果時間
	 */
	public S_SkillIconPoison(final int type, final int time) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(S_PacketBox.POISON_ICON);
		writeC(type);
		if (type == 2) {// 麻痺毒額外判斷
			writeH(0x0000);// 這邊如果是麻痺毒的話這邊就是完全麻痺的時間
			writeC(time);// 前置時間
			writeC(0x00);
		} else {// 其他毒
			writeH(time);
			writeC(0x00);
			writeC(0x00);
		}
	}

	// ======================================================================
	// [Server] opcode = 5 負面效果圖示 結束 解毒
	// 0000: 05 a1 00 00 00 00 b2 94 ........
	// ======================================================================
	// ======================================================================
	// [Server] opcode = 5 身體要被麻痺了 (麻痺毒)
	// 0000: 05 a1 02 2d 00 14 00 99 ...-....
	// ======================================================================
	// ======================================================================
	// [Server] opcode = 5 中毒狀態。 (一般毒)
	// 0000: 05 a1 01 2c 01 01 00 92 ...-....
	// ======================================================================
	// ======================================================================
	// [Server] opcode = 5 突然感覺到混亂。 (卡毒)
	// 0000: 05 a1 06 10 0e 01 c1 40 ...-....
	// ======================================================================

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return "[S] " + this.getClass().getSimpleName() + " [S->C 發送技能圖示封包(毒麻痺負面效果圖示)]";
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
