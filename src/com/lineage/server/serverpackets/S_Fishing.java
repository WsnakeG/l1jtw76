package com.lineage.server.serverpackets;

/**
 * 物件動作種類(短時間)<BR>
 * 釣魚
 * 
 * @author dexc
 */
public class S_Fishing extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件動作種類(短時間)<BR>
	 * 釣魚
	 * 
	 * @param objectId
	 * @param motionNum
	 * @param x
	 * @param y
	 */
	public S_Fishing(final int objectId, final int motionNum, final int x, final int y) {
		buildPacket(objectId, motionNum, x, y);
	}

	private void buildPacket(final int objectId, final int motionNum, final int x, final int y) {
		writeC(S_OPCODE_DOACTIONGFX);
		writeD(objectId);
		writeC(motionNum);
		writeH(x);
		writeH(y);
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
