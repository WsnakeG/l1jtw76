package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 隊伍頻道(聊天)
 * 
 * @author dexc
 */
public class S_ChatParty2 extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 隊伍頻道(聊天)
	 * 
	 * @param pc
	 * @param chat
	 */
	public S_ChatParty2(final L1PcInstance pc, final String chat) {
		buildPacket(pc, chat);
	}

	private void buildPacket(final L1PcInstance pc, final String chat) {
		writeC(S_OPCODE_NORMALCHAT);
		writeC(0x0e);
		writeD(pc.isInvisble() ? 0 : pc.getId());
		writeS("(" + pc.getName() + ") " + chat);
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