package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件封包
 * 
 * @author dexc
 */
public class S_NPCPack extends ServerBasePacket {

	private static final int STATUS_POISON = 1;
	// private static final int STATUS_INVISIBLE = 2;
	private static final int STATUS_PC = 4;
	/*
	 * private static final int STATUS_FREEZE = 8; private static final int
	 * STATUS_BRAVE = 16; private static final int STATUS_ELFBRAVE = 32; private
	 * static final int STATUS_FASTMOVABLE = 64; private static final int
	 * STATUS_GHOST = 128;
	 */

	private byte[] _byte = null;

	/**
	 * 物件封包
	 * 
	 * @param npc
	 */
	public S_NPCPack(final L1NpcInstance npc, final L1PcInstance pc) {
		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為
										// S_OPCODE_OBJECTPACK
		writeH(npc.getX());
		writeH(npc.getY());
		writeD(npc.getId());

		if (npc.getTempCharGfx() == 0) {
			writeH(npc.getGfxId());

		} else {
			writeH(npc.getTempCharGfx());
		}

		if (npc.getNpcTemplate().is_doppel() && (npc.getGfxId() != 31)) { // スライムの姿をしていなければドッペル
			writeC(0x04); // 拿劍

		} else {
			writeC(npc.getStatus());
		}

		writeC(npc.getHeading());
		writeC(npc.getChaLightSize());
		writeC(npc.getMoveSpeed());
		writeD((int) npc.getExp());
		writeH(npc.getTempLawful());
		String levelname = npc.getNameId();
		if ((pc.getLevel() - npc.getLevel() >= 5 && pc.getLevel()
				- npc.getLevel() <= 10)
				|| (npc.getLevel() - pc.getLevel() >= 5 && npc.getLevel()
						- pc.getLevel() <= 10)) {
			levelname = "\\f2" + npc.getNameId();
		} else if (npc.getLevel() - pc.getLevel() >= 30) {
			levelname = "\\f3" + npc.getNameId();
		} else if (npc.getLevel() - pc.getLevel() >= 20) {
			levelname = "\\fC" + npc.getNameId();
		} else if (npc.getLevel() - pc.getLevel() >= 11) {
			levelname = "\\f:" + npc.getNameId();
		}
		writeS(levelname);

		writeS(npc.getTitle());

		/**
		 * ｼｼﾆﾃ - 0:mob,item(atk pointer), 1:poisoned(), 2:invisable(), 4:pc,
		 * 8:cursed(), 16:brave(), 32:??, 64:??(??), 128:invisable but name
		 */
		int status = 0;
		if (npc.getPoison() != null) { // 毒状態
			if (npc.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}
		if (npc.getNpcTemplate().is_doppel()) {
			// PC属性だとエヴァの祝福を渡せないためWIZクエストのドッペルは例外
			if (npc.getNpcTemplate().get_npcId() != 81069) {
				status |= STATUS_PC;
			}
		}
		if (npc.getNpcTemplate().get_npcId() == 90024) {
			status |= STATUS_POISON;
		}
		writeC(status); // 狀態

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

	/**
	 * 物件封包
	 * 
	 * @param npc
	 */
	public S_NPCPack(final L1NpcInstance npc) {
		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為
										// S_OPCODE_OBJECTPACK
		writeH(npc.getX());
		writeH(npc.getY());
		writeD(npc.getId());

		if (npc.getTempCharGfx() == 0) {
			writeH(npc.getGfxId());

		} else {
			writeH(npc.getTempCharGfx());
		}

		if (npc.getNpcTemplate().is_doppel() && (npc.getGfxId() != 31)) { // スライムの姿をしていなければドッペル
			writeC(0x04); // 拿劍

		} else {
			writeC(npc.getStatus());
		}

		writeC(npc.getHeading());
		writeC(npc.getChaLightSize());
		writeC(npc.getMoveSpeed());
		writeD((int) npc.getExp());
		writeH(npc.getTempLawful());
		writeS(npc.getNameId());

		writeS(npc.getTitle());

		/**
		 * ｼｼﾆﾃ - 0:mob,item(atk pointer), 1:poisoned(), 2:invisable(), 4:pc,
		 * 8:cursed(), 16:brave(), 32:??, 64:??(??), 128:invisable but name
		 */
		int status = 0;
		if (npc.getPoison() != null) { // 毒状態
			if (npc.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}
		if (npc.getNpcTemplate().is_doppel()) {
			// PC属性だとエヴァの祝福を渡せないためWIZクエストのドッペルは例外
			if (npc.getNpcTemplate().get_npcId() != 81069) {
				status |= STATUS_PC;
			}
		}
		if (npc.getNpcTemplate().get_npcId() == 90024) {
			status |= STATUS_POISON;
		}
		writeC(status); // 狀態

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
