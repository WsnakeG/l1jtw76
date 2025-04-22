package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 強制登出人物(死亡後重新開始 - 未知)
 * 
 * @author dexc
 */
public class S_ChatOut extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 強制登出人物(死亡後重新開始)
	 * 
	 * @param objid
	 */
	public S_ChatOut(final int objid) {
		buildPacket(objid);
	}

	/**
	 * 強制登出人物(死亡後重新開始)
	 * 
	 * @param pc
	 */
	public S_ChatOut(final L1PcInstance pc) {
		buildPacket(pc.getId());
	}

	private void buildPacket(final int objid) {
		// this.writeC(S_OPCODE_CHAROUT);
		writeD(objid);
		writeD(0x00000000);
		writeD(0x00000000);
		writeD(0x00000000);
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