package com.lineage.server.serverpackets;

/**
 * 戰爭
 * 
 * @author simlin
 */
public class S_WarNameAndTime extends ServerBasePacket {
	private byte[] _byte = null;

	public S_WarNameAndTime(final boolean isAtt, final int time, final String name) {
		writeC(S_OPCODE_CRAFTSYSTEM); // XXX S_OPCODE_EXTENDED_PROTOBUF 修改為 S_OPCODE_CRAFTSYSTEM
		writeH(76);
		writeCC(1, isAtt ? 2 : 1);
		writeCC(2, time * 2);
		writeCS(3, name);
		writeH(0);
	}

	public S_WarNameAndTime() {
		writeC(S_OPCODE_CRAFTSYSTEM); // XXX S_OPCODE_EXTENDED_PROTOBUF 修改為 S_OPCODE_CRAFTSYSTEM
		writeH(76);
		writeCC(1, 1L);
		writeCC(2, 0L);
		writeH(0);
	}

	@Override
	public byte[] getContent() {
		return _bao.toByteArray();
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
