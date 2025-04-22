package com.lineage.server.serverpackets;

/**
 * 角色名稱變紫色
 * 
 * @author DaiEn
 */
public class S_PinkName extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色名稱變紫色
	 * 
	 * @param objecId
	 * @param time
	 */
	public S_PinkName(final int objecId, final int time) {
		// 0000: 19 40 60 b8 00 0f d2 03 .@`.....
		writeC(S_OPCODE_PINKNAME);
		writeD(objecId);
		writeC(time);
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
