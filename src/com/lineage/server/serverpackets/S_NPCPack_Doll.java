package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件封包 - 魔法娃娃
 * 
 * @author dexc
 */
public class S_NPCPack_Doll extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件封包 - 魔法娃娃
	 * 
	 * @param pet
	 * @param pc
	 */
	public S_NPCPack_Doll(final L1DollInstance pet, final L1PcInstance pc) {
		/*
		 * int addbyte = 0; int addbyte1 = 1; int addbyte2 = 13; int setting =
		 * 4;
		 */
		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為 S_OPCODE_OBJECTPACK
		writeH(pet.getX());
		writeH(pet.getY());
		writeD(pet.getId());
		writeH(pet.getGfxId()); // SpriteID in List.spr
		writeC(pet.getStatus()); // Modes in List.spr
		writeC(pet.getHeading());
		writeC(0x00); // (Bright) - 0~15
		writeC(pet.getMoveSpeed()); // ･ｹ･ﾔ｡ｼ･ﾉ - 0:normal,1:fast,2:slow
		writeD(0x00000000); // exp
		writeH(0x0000); // Lawful

		writeS(pet.getNameId());
		writeS(pet.getTitle());

		writeC(0x00); // 狀態
		writeD(0x00000000); // ??

		writeS(null); // ??

		if (pet.getMaster() != null) {
			if (pet.getMaster() instanceof L1PcInstance) {
				writeS(pet.getMaster().getViewName());
			} else if (pet.getMaster() instanceof L1NpcInstance) {
				final L1NpcInstance npc = (L1NpcInstance) pet.getMaster();
				writeS(npc.getNameId());
			}

		} else {
			writeS("");
		}

		writeC(0x00); // 物件分類

		writeC(0xff); // HP
		writeC(0x00);
		writeC(0x00);
		// this.writeC(pet.getLevel()); // LV
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
