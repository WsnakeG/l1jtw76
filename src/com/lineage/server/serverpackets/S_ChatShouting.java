package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 大喊頻道
 * 
 * @author dexc
 */
public class S_ChatShouting extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 大喊頻道
	 * 
	 * @param pc
	 * @param chat
	 */
	public S_ChatShouting(final L1PcInstance pc, final String chat) {
		buildPacket(pc, chat);
	}

	private void buildPacket(final L1PcInstance pc, final String chat) {
		writeC(S_OPCODE_NORMALCHAT);
		writeC(0x02);
		writeD(pc.isInvisble() ? 0 : pc.getId());
		writeS("<" + pc.getViewName() + "> " + chat);

		writeH(pc.getX());
		writeH(pc.getY());
	}

	/**
	 * NPC對話輸出
	 * 
	 * @param pc
	 * @param chat
	 */
	public S_ChatShouting(final L1NpcInstance npc, final String chat) {
		writeC(S_OPCODE_NORMALCHAT);
		writeC(0x02);
		writeD(npc.isInvisble() ? 0 : npc.getId());
		writeS("<" + npc.getNameId() + "> " + chat);

		writeH(npc.getX());
		writeH(npc.getY());
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
