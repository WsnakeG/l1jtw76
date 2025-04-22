package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 密語交談(發送)頻道
 * 
 * @author dexc
 */
public class S_ChatWhisperTo extends ServerBasePacket {

	private byte[] _byte = null;

	public S_ChatWhisperTo(final L1PcInstance pc, final String chat) {
		buildPacket(pc, chat);
	}

	private void buildPacket(final L1PcInstance pc, final String chat) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x09);
		writeS("-> (" + pc.getName() + ") " + chat);
	}

	public S_ChatWhisperTo(final L1DeInstance de, final String chat) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x09);
		writeS("-> (" + de.getNameId() + ") " + chat);
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