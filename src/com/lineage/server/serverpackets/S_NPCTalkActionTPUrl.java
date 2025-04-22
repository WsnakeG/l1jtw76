package com.lineage.server.serverpackets;

import com.lineage.server.model.L1NpcTalkData;

/**
 * NPC對話視窗
 * 
 * @author dexc
 */
public class S_NPCTalkActionTPUrl extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * NPC對話視窗
	 * 
	 * @param cha
	 * @param prices
	 * @param objid
	 */
	public S_NPCTalkActionTPUrl(final L1NpcTalkData cha, final Object[] prices, final int objid) {
		buildPacket(cha, prices, objid);
	}

	private void buildPacket(final L1NpcTalkData npc, final Object[] prices, final int objid) {
		String htmlid = "";
		htmlid = npc.getTeleportURL();
		writeC(S_OPCODE_SHOWHTML);
		writeD(objid);
		writeS(htmlid);
		writeH(0x01); // 不明
		writeH(prices.length); // 引数の数

		for (final Object price : prices) {
			writeS(String.valueOf(((Integer) price).intValue()));
		}
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
