package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1EffectInstance;

/**
 * 物件封包
 * 
 * @author dexc
 */
public class S_NPCPack_Eff extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件封包
	 * 
	 * @param npc
	 */
	public S_NPCPack_Eff(final L1EffectInstance npc) {
		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為 S_OPCODE_OBJECTPACK
		writeH(npc.getX());
		writeH(npc.getY());
		writeD(npc.getId());

		writeH(npc.getGfxId());

		writeC(npc.getStatus());

		writeC(npc.getHeading());
		writeC(npc.getChaLightSize());
		writeC(npc.getMoveSpeed());
		writeD((int) npc.getExp());
		writeH(npc.getTempLawful());
		writeS(npc.getNameId());

		writeS(npc.getTitle());

		writeC(0x00); // 狀態

		writeD(0x00000000); // 0以外にするとC_27が飛ぶ
		writeS(null);
		writeS(null); // マスター名？

		writeC(0x00); // 物件分類

		writeC(0xff); // HP
		writeC(0x00);
		writeC(0x00);// LV
		// this.writeC(npc.getLevel());// LV
		writeC(0x00);
		writeC(0xff);
		writeC(0xff);
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
