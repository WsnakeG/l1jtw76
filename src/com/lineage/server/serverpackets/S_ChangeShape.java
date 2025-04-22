package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件外型改變
 * 
 * @author dexc
 */
public class S_ChangeShape extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件外型改變
	 * 
	 * @param obj
	 * @param polyId
	 */
	public S_ChangeShape(final L1Character obj, final int polyId) {
		buildPacket(obj, polyId, false);
	}

	/**
	 * 物件外型改變
	 * 
	 * @param obj
	 * @param polyId
	 * @param weaponTakeoff
	 */
	public S_ChangeShape(final L1Character obj, final int polyId, final boolean weaponTakeoff) {
		buildPacket(obj, polyId, weaponTakeoff);
	}

	private void buildPacket(final L1Character obj, final int polyId, final boolean weaponTakeoff) {
		writeC(S_OPCODE_POLY);
		writeD(obj.getId());
		writeH(polyId);
		// 何故29なのか不明
		writeH(weaponTakeoff ? 0 : 29);
	}

	/**
	 * NPC改變外型(寵物 迷魅使用)
	 * 
	 * @param pc 執行命令PC
	 * @param npc 執行命令NPC
	 * @param polyId 代號
	 */
	public S_ChangeShape(final L1PcInstance pc, final L1NpcInstance npc, final int polyId) {
		writeC(S_OPCODE_POLY);
		writeD(npc.getId());
		writeD(pc.getId());
		writeH(polyId);
		writeS(pc.getViewName());
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
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
