package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 更新正義值
 * 
 * @author dexc
 */
public class S_Lawful extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 更新正義值
	 * 
	 * @param pc
	 */
	public S_Lawful(final L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(final L1PcInstance pc) {
		// 0000: 75 5d 71 cd 00 00 00 45 u]q....E
		writeC(S_OPCODE_LAWFUL);
		writeD(pc.getId());
		writeH(pc.getLawful());
	}

	/**
	 * 更新正義值
	 * 
	 * @param objid
	 * @param lawful
	 */
	public S_Lawful(final int objid) {
		writeC(S_OPCODE_LAWFUL);
		writeD(objid);
		writeH(-32768);
		writeH(-32768);
		writeH(-32768);
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