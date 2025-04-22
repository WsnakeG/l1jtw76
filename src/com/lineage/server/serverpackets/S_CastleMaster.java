package com.lineage.server.serverpackets;

/**
 * 角色皇冠
 * 
 * @author dexc
 */
public class S_CastleMaster extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色皇冠
	 * 
	 * @param type 城堡編號
	 * @param objecId 人物OBJID
	 */
	public S_CastleMaster(final int type, final int objecId) {
		buildPacket(type, objecId);
	}

	private void buildPacket(final int type, final int objecId) {
		writeC(S_OPCODE_CASTLEMASTER);
		writeC(type);
		writeD(objecId);
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
