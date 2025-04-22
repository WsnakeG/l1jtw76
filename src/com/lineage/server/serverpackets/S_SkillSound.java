package com.lineage.server.serverpackets;

/**
 * 產生動畫(物件)
 * 
 * @author dexc
 */
public class S_SkillSound extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 產生動畫(物件)
	 * 
	 * @param objid
	 * @param gfxid
	 */
	public S_SkillSound(final int objid, final int gfxid) {
		buildPacket(objid, gfxid);
	}

	private void buildPacket(final int objid, final int gfxid) {
		// 0000: 56 2c 80 a1 01 82 08 87 V,......
		writeC(S_OPCODE_SKILLSOUNDGFX);
		writeD(objid);
		writeH(gfxid);
	}

	/**
	 * 產生動畫(物件)
	 * 
	 * @param objid
	 * @param gfxid
	 * @param time
	 */
	public S_SkillSound(final int objid, final int gfxid, final int time) {
		// 0000: 56 2c 80 a1 01 82 08 87 V,......
		writeC(S_OPCODE_SKILLSOUNDGFX);
		writeD(objid);
		writeH(gfxid);
		writeH(time);
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
