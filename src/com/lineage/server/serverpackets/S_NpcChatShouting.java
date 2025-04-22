package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;

/**
 * NPC 大喊頻道
 * 
 * @author dexc
 */
public class S_NpcChatShouting extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * NPC 大喊頻道
	 * 
	 * @param npc
	 * @param chat
	 */
	public S_NpcChatShouting(final L1NpcInstance npc, final String chat) {
		buildPacket(npc, chat);
	}

	private void buildPacket(final L1NpcInstance npc, final String chat) {
		writeC(S_OPCODE_NPCSHOUT); // Key is 16 , can use
		// desc-?.tbl
		writeC(0x02); // Color
		writeD(npc.getId());
		writeS("<" + npc.getNameId() + "> " + chat);
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
