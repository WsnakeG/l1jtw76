package com.lineage.server.serverpackets;

/**
 * UI地圖座標傳送
 * 
 * @author kzk
 */
public class S_PacketBoxLoc extends ServerBasePacket {

	private byte[] _byte = null;

	/** UI地圖座標傳送 */
	public static final int SEND_LOC = 111;

	/**
	 * UI地圖座標傳送
	 * 
	 * @param name
	 * @param map
	 * @param x
	 * @param y
	 * @param zone
	 */
	public S_PacketBoxLoc(final String name, final int map, final int x, final int y, final int zone) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(SEND_LOC);
		writeS(name);
		writeH(map);
		writeH(x);
		writeH(y);
		writeD(zone);
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
