package com.lineage.server.serverpackets;

/**
 * 魔法效果:暗盲咒術
 * 
 * @author dexc
 */
public class S_CurseBlind extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 魔法效果:暗盲咒術
	 * 
	 * @param type 0:OFF 1:自己 2:週邊物件可見
	 */
	public S_CurseBlind(final int type) {
		buildPacket(type);
	}

	private void buildPacket(final int type) {
		writeC(S_OPCODE_CURSEBLIND);
		writeH(type);
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
