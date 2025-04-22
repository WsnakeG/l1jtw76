package com.lineage.server.serverpackets;

/**
 * 黑名單視窗
 * @author admin
 *
 */
public class S_PacketBoxExclude extends ServerBasePacket {


	private byte[] _byte = null;
	
	/**
	 * <font color=#827B00>開啟拒絕名單 :</font><BR>
	 * 0000: 4e 11 空字串 56 a6 7d 34 31 N..V.}41<BR>
	 * <BR>
	 * 將角色名稱加入進拒絕名單 :<BR>
	 * 0000: 4e 12 字串 6a N.Fdsf.j<BR>
	 * <BR>
	 * 將角色名稱移除出拒絕名單 :<BR>
	 * 0000: 4e 13 字串 34 N.Fdsf.4
	 * @param type 執行方式 0x11初始化列表 0x12增加進列表 0x13移除出列表
	 * @param name 物件名稱
	 * @return
	 */
	
	/**
	 * 啟用黑名單
	 * @param type
	 * @param name
	 * @param type2 0X00 斷絕指令視窗 0X01信件黑名單視窗
	 */
	public S_PacketBoxExclude(final int type, final String name, final int type2) {
		this.writeC(S_OPCODE_PACKETBOX);
		this.writeC(type);
		this.writeS(name);
		this.writeC(type2);
	}
	
	/**
	 * 拒絕視窗清單初始化(發送時機不明)
	 * @param type
	 * @param name
	 */
	public S_PacketBoxExclude(final int type) {
		this.writeC(S_OPCODE_PACKETBOX);
		this.writeC(S_PacketBox.ADD_EXCLUDE2);
		this.writeC(type);// 0X00 斷絕指令視窗 0X01信件黑名單視窗
		this.randomInt();
	}
	
	/**
	 * 拒絕視窗清單初始化(發送時機不明)
	 * @param type
	 * @param name
	 */
	public S_PacketBoxExclude(int type, Object[] names) {
		this.writeC(S_OPCODE_PACKETBOX);
		this.writeC(S_PacketBox.ADD_EXCLUDE2);
		this.writeC(type);// 0X00 斷絕指令視窗 0X01信件黑名單視窗
		this.writeC(type);// 0X00 斷絕指令視窗 0X01信件黑名單視窗
		writeC(names.length);
		for (Object name : names) {
			writeS(name.toString());
		}
		writeH(0x00);
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
		return "[S] " + this.getClass().getSimpleName();
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
