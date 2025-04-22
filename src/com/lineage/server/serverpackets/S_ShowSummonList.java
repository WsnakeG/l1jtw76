package com.lineage.server.serverpackets;

/**
 * @author dexc
 */
public class S_ShowSummonList extends ServerBasePacket {

	private byte[] _byte = null;

	public S_ShowSummonList(final int objid) {
		writeC(S_OPCODE_SHOWHTML);
		writeD(objid);
		writeS("summonlist");
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
