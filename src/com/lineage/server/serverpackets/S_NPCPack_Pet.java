package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;

/**
 * 物件封包 - 寵物
 * 
 * @author dexc
 */
public class S_NPCPack_Pet extends ServerBasePacket {

	private static final int STATUS_POISON = 1;
	/*
	 * private static final int STATUS_INVISIBLE = 2; private static final int
	 * STATUS_PC = 4; private static final int STATUS_FREEZE = 8; private static
	 * final int STATUS_BRAVE = 16; private static final int STATUS_ELFBRAVE =
	 * 32; private static final int STATUS_FASTMOVABLE = 64; private static
	 * final int STATUS_GHOST = 128;
	 */

	private byte[] _byte = null;

	/**
	 * 物件封包 - 寵物
	 * 
	 * @param pet
	 * @param pc
	 */
	public S_NPCPack_Pet(final L1PetInstance pet, final L1PcInstance pc) {
		buildPacket(pet, pc);
	}

	private void buildPacket(final L1PetInstance pet, final L1PcInstance pc) {
		/*
		 * final int addbyte = 0; final int addbyte1 = 1; final int addbyte2 =
		 * 13; final int setting = 4;
		 */

		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為 S_OPCODE_OBJECTPACK
		writeH(pet.getX());
		writeH(pet.getY());
		writeD(pet.getId());
		writeH(pet.getGfxId()); // SpriteID in List.spr
		writeC(pet.getStatus()); // Modes in List.spr
		writeC(pet.getHeading());
		writeC(pet.getChaLightSize()); // (Bright) - 0~15
		writeC(pet.getMoveSpeed()); // スピード - 0:normal, 1:fast, 2:slow
		writeD((int) pet.getExp());
		writeH(pet.getTempLawful());
		writeS(pet.getName());
		writeS(pet.getTitle());
		int status = 0;
		if (pet.getPoison() != null) { // 毒状態
			if (pet.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}
		writeC(status); // 狀態
		writeD(0x00000000); // 主人血盟OBJID
		writeS(null); // 血盟名稱

		final StringBuilder stringBuilder = new StringBuilder();
		if (pet.getMaster() != null) {
			stringBuilder.append(pet.getMaster().getViewName());
		} else {
			stringBuilder.append("");
		}
		// 主人名稱
		writeS(stringBuilder.toString());

		writeC(0x00); // 物件分類

		// HP顯示
		if ((pet.getMaster() != null) && (pet.getMaster().getId() == pc.getId())) {
			writeC((100 * pet.getCurrentHp()) / pet.getMaxHp());

		} else {
			writeC(0xFF);
		}

		writeC(0x00);
		writeC(0x00); // LV
		// this.writeC(pet.getLevel()); // LV
		writeC(0x00);
		writeC(0xFF);
		writeC(0xFF);
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
