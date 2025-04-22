package com.lineage.server.serverpackets;

/**
 * 物件動作種類(短時間)
 * 
 * @author dexc
 */
public class S_DoActionGFX extends ServerBasePacket {

	public static int ACTION_MAGIC = 0x16;

	private byte[] _byte = null;

	/**
	 * 物件動作種類(短時間)
	 * 
	 * @param objectId
	 * @param actionId
	 */
	public S_DoActionGFX(final int objectId, final int actionId) {
		writeC(S_OPCODE_DOACTIONGFX);
		writeD(objectId);
		writeC(actionId);
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
