package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;

/**
 * 物件新增主人
 * 
 * @author dexc
 */
public class S_NewMaster extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件新增主人
	 * 
	 * @param name 主人名稱
	 * @param npc
	 */
	public S_NewMaster(final String name, final L1NpcInstance npc) {
		buildPacket(name, npc);
	}

	private void buildPacket(final String name, final L1NpcInstance npc) {
		writeC(S_OPCODE_NEWMASTER);
		writeD(npc.getId());
		writeS(name);
	}

	public S_NewMaster(final L1NpcInstance npc) {
		writeC(S_OPCODE_NEWMASTER);
		writeD(npc.getId());
		writeS("");
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
