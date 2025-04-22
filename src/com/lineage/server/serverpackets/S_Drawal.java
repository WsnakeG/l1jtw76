package com.lineage.server.serverpackets;

/**
 * 城堡寶庫(要求領出資金)
 * 
 * @author dexc
 */
public class S_Drawal extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 城堡寶庫(要求領出資金)
	 * 
	 * @param objectId
	 * @param count
	 */
	public S_Drawal(final int objectId, final long count) {
		writeC(S_OPCODE_DRAWAL);
		writeD(objectId);
		writeD((int) Math.min(count, 2000000000));
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
