package com.lineage.server.serverpackets;

/**
 * 魔法效果:水底呼吸
 * 
 * @author dexc
 */
public class S_SkillIconBlessOfEva extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**
	 * 魔法效果:水底呼吸
	 * 
	 * @param objectId
	 * @param time
	 */
	public S_SkillIconBlessOfEva(final int objectId, final int time) {
		writeC(S_OPCODE_BLESSOFEVA);
		writeD(objectId);
		writeH(time);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
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
