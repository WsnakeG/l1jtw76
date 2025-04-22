package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 同盟頻道
 * 
 * @author terry0412
 */
public class S_ChatClanAlliance extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 同盟頻道(~)
	 * 
	 * @param pc
	 * @param chat
	 */
	public S_ChatClanAlliance(final L1PcInstance pc, final String clanName, final String chat) {
		buildPacket(pc, clanName, chat);
	}

	private void buildPacket(final L1PcInstance pc, final String clanName, final String chat) {
		writeC(S_OPCODE_NORMALCHAT);
		writeC(0x0f);
		writeD(pc.isInvisble() ? 0 : pc.getId());
		writeS("[" + clanName + "][" + pc.getName() + "] " + chat);
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
