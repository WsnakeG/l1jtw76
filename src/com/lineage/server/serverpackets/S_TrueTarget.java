package com.lineage.server.serverpackets;

/**
 * 魔法效果:精準目標
 * 
 * @author DaiEn
 */
public class S_TrueTarget extends ServerBasePacket {

	private byte[] _byte = null;

	public S_TrueTarget(int targetObjId, int gfxid, int type) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(194);
		writeD(targetObjId);
		writeD(gfxid);
		writeD(type);
		writeD(300);
	}

	public S_TrueTarget(int targetId, boolean active) {
		buildPacket(targetId, active);
	}
	private void buildPacket(int targetId, boolean active) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0xc2);
		writeD(targetId);
		writeC(0x4f);
		writeC(0x33);
		writeC(0x00);
		writeC(0x00);
		writeD(active ? 1 : 0);
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
