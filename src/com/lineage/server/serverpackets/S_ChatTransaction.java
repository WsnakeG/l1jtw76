package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 交易頻道
 * 
 * @author dexc
 */
public class S_ChatTransaction extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 交易頻道
	 * 
	 * @param pc
	 * @param chat
	 */
	public S_ChatTransaction(final L1PcInstance pc, final String chat) {
		buildPacket(pc, chat);
	}

	private void buildPacket(final L1PcInstance pc, final String chat) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x0c);
		writeS("[" + pc.getName() + "] " + chat);
	}

	/**
	 * NPC對話輸出
	 * 
	 * @param de
	 * @param chat
	 */
	public S_ChatTransaction(final L1NpcInstance npc, final String chat) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x0c);
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