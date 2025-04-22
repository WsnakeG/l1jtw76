package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;

/**
 * NPC 廣播頻道
 * 
 * @author dexc
 */
public class S_NpcChatGlobal extends ServerBasePacket {

	private byte[] _byte = null;

	public S_NpcChatGlobal(final L1NpcInstance npc, final String chat) {
		buildPacket(npc, chat);
	}

	private void buildPacket(final L1NpcInstance npc, final String chat) {
		writeC(S_OPCODE_NPCSHOUT);
		writeC(0x03); // XXX 白色になる
		writeD(npc.getId());
		writeS("[" + npc.getNameId() + "] " + chat);
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
