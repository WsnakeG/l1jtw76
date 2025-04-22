package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;

/**
 * NPC對話視窗(變身清單)
 * 
 * @author dexc
 */
public class S_ShowPolyList extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * NPC對話視窗(變身清單)
	 * 
	 * @param objid
	 */
	public S_ShowPolyList(final int objid) {
		writeC(S_OPCODE_SHOWHTML);
		writeD(objid);
		writeS("monlist");
	}

	/**
	 * NPC對話視窗(變身清單)
	 * 
	 * @param target
	 */
	public S_ShowPolyList(final L1Character target) {
		writeC(S_OPCODE_SHOWHTML);
		writeD(target.getId());
		writeS("monlist");
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
