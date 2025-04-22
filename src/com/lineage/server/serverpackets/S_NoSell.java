package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;

public class S_NoSell extends ServerBasePacket {
	private byte[] _byte = null;

	public S_NoSell(final L1NpcInstance npc) {
		buildPacket(npc);
	}

	private void buildPacket(final L1NpcInstance npc) {
		writeC(S_OPCODE_SHOWHTML);
		writeD(npc.getId());
		writeS("nosell");
		writeC(0x01);
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
