package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 傳送鎖定(瞬間移動)
 * 
 * @author dexc
 */
public class S_Teleport extends ServerBasePacket {

	private static final String S_TELEPORT = "[S] S_Teleport";

	private byte[] _byte = null;

	public S_Teleport(final L1PcInstance pc) {
		writeC(S_OPCODE_TELEPORT);
		writeC(0x00);
		writeC(0x40);
		writeD(pc.getId());
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
		return S_TELEPORT;
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
