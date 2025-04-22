package com.lineage.server.serverpackets;

/**
 * @author dexc
 */
public class S_PacketBoxItemLv extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * writeByte(level) writeByte(0): <font color=#00800>(672)
	 * 等級%d(51~127)以上才可使用此道具。 </font><br>
	 * writeByte(0) writeByte(level): <font color=#00800>(673)
	 * 等級%d(0~49)以下才能使用此道具。 </font>
	 */
	public static final int MSG_LEVEL_OVER = 0x0c;// 12;

	/**
	 * <b><font color=red>封包分類項目 : </font>
	 * <font color=#008000>封包盒子(物品等級限制)</font></b>
	 * 
	 * @param minLv
	 * @param maxLv
	 */
	public S_PacketBoxItemLv(final int minLv, final int maxLv) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(MSG_LEVEL_OVER);
		writeC(minLv); // 0~49
		writeC(maxLv); // 0~49
	}

	/**
	 * <b><font color=red>封包分類項目 : </font>
	 * <font color=#008000>封包盒子(測試物品等級限制)</font></b>
	 * 
	 * @param minLv
	 */
	public S_PacketBoxItemLv(final int opid) {
		writeC(opid);
		writeC(MSG_LEVEL_OVER);
		writeC(10); // 0~49
		writeC(1249); // msg id
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
