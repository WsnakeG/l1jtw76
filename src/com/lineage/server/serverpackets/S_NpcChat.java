package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * NPC 一般頻道
 * 
 * @author dexc
 */
public class S_NpcChat extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * NPC 一般頻道
	 * 
	 * @param npc NPC
	 * @param chat 對話內容
	 */
	public S_NpcChat(final L1NpcInstance npc, final String chat) {
		writeC(S_OPCODE_NPCSHOUT);
		// desc-?.tbl
		writeC(0x00); // Color
		writeD(npc.getId());
		writeS(npc.getNameId() + ": " + chat);
	}

	public S_NpcChat(final int objid, final String chat) {
		writeC(S_OPCODE_NPCSHOUT);
		// desc-?.tbl
		writeC(0x00); // Color
		writeD(objid);
		writeS(chat);
	}

	/**
	 * NPC 一般頻道
	 * 
	 * @param npc NPC
	 * @param chat 對話內容
	 * @param name 是否顯示名稱 true:是 false:不是
	 */
	public S_NpcChat(final L1NpcInstance npc, final String chat, final boolean name) {
		writeC(S_OPCODE_NPCSHOUT);
		// desc-?.tbl
		writeC(0x00); // Color
		writeD(npc.getId());
		writeS((name ? npc.getNameId() + ": " : "") + chat);
	}

	/**
	 * PC對話輸出(使用NPC頻道)
	 * 
	 * @param pc
	 * @param chat
	 */
	public S_NpcChat(final L1PcInstance pc, final String chat) {
		writeC(S_OPCODE_NPCSHOUT);
		// desc-?.tbl
		writeC(0x00); // Color
		writeD(pc.getId());
		writeS(pc.getName() + ": " + chat);
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
