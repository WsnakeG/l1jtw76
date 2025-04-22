package com.lineage.server.serverpackets;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;

import com.lineage.config.ConfigAlt;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件封包 - 其他人物
 * 
 * @author dexc
 */
public class S_OtherCharPacks extends ServerBasePacket {

	private static final int STATUS_POISON = 1;
	private static final int STATUS_INVISIBLE = 2;
	private static final int STATUS_PC = 4;
	// private static final int STATUS_FREEZE = 8;
	private static final int STATUS_BRAVE = 16;
	private static final int STATUS_ELFBRAVE = 32;
	private static final int STATUS_FASTMOVABLE = 64;
	// private static final int STATUS_GHOST = 128;

	private byte[] _byte = null;

	/**
	 * 物件封包 - 其他人物
	 * 
	 * @param pc
	 */
	public S_OtherCharPacks(final L1PcInstance pc) {
		int status = STATUS_PC;

		if (pc.getPoison() != null) { // 毒状態
			if (pc.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}
		if (pc.isInvisble()) {
			status |= STATUS_INVISIBLE;
		}
		if (pc.isBrave()) {
			status |= STATUS_BRAVE;
		}
		if (pc.isElfBrave()) {
			// エルヴンワッフルの場合は、STATUS_BRAVEとSTATUS_ELFBRAVEを立てる。
			// STATUS_ELFBRAVEのみでは効果が無い？
			status |= STATUS_BRAVE;
			status |= STATUS_ELFBRAVE;
		}
		if (pc.isFastMovable()) {
			status |= STATUS_FASTMOVABLE;
		}

		writeC(S_OPCODE_OBJECTPACK); // XXX S_OPCODE_CHARPACK 修正為 S_OPCODE_OBJECTPACK
		writeH(pc.getX());
		writeH(pc.getY());
		writeD(pc.getId());

		if (pc.isDead()) {
			writeH(pc.getTempCharGfxAtDead());

		} else {
			writeH(pc.getTempCharGfx());
		}

		if (pc.isDead()) {
			writeC(pc.getStatus());
		} else {
			writeC(pc.getCurrentWeapon());
		}

		writeC(pc.getHeading());
		// writeC(0); // makes char invis (0x01), cannot move. spells display
		writeC(pc.getChaLightSize());
		writeC(pc.getMoveSpeed());
		writeD(0x00000000); // exp
		writeH(pc.getLawful());

		if (pc.get_redbluejoin() == 11 || pc.get_redbluejoin() == 21) {
			writeS("\\f=復仇者聯盟");
		} else if (pc.get_redbluejoin() == 12 || pc.get_redbluejoin() == 22) {
			writeS("\\f2暗殺者組織");
		} else if (pc.isProtector()) {
			writeS("**守護者**");

		} else {
			// 是否開放給其他人看見[陣營稱號]和[轉生稱號] by terry0412
			if (ConfigAlt.SHOW_SP_TITLE) {
				final StringBuilder stringBuilder = new StringBuilder();

				if (pc.get_c_power() != null
						&& pc.get_c_power().get_c1_type() != 0) {
					final String type = pc.get_c_power().get_power()
							.get_c1_name_type();
					stringBuilder.append(type);
				}
				if (pc.get_other().get_color() != 0) {
					stringBuilder.append(pc.get_other().color());
				}

				stringBuilder.append(pc.getName());
				/*
				 * if (_prestigeSystem != null) {
				 * sbr.append(_prestigeSystem.getTitle()); }
				 */
				if (pc.getMeteAbility() != null) {
					stringBuilder.append(pc.getMeteAbility().getTitle());
				}
				writeS(stringBuilder.toString());

			} else {
				if (pc.get_other().get_color() != 0) {
					writeS(pc.get_other().color() + pc.getName());
				} else {
					writeS(pc.getName());
				}
			}
		}

		if (pc.get_redbluejoin() == 11 || pc.get_redbluejoin() == 21
				|| pc.get_redbluejoin() == 12 || pc.get_redbluejoin() == 22) {
			writeS("");
		} else
			writeS(pc.getTitle());
		    writeC(status); // 狀態
		if (pc.get_redbluejoin() == 11 || pc.get_redbluejoin() == 21
				|| pc.get_redbluejoin() == 12 || pc.get_redbluejoin() == 22) {
			writeD(0);
		} else
			writeD(pc.getEmblemId());
		writeS(pc.getClanname()); // 血盟名稱
		writeS(null); // 主人名稱

		// 0:NPC,道具
		// 1:中毒 ,
		// 2:隱身
		// 4:人物
		// 8:詛咒
		// 16:勇水
		// 32:??
		// 64:??(??)
		// 128:invisible but name
		writeC(pc.getClanRank() << 4);// 血盟階級

		// this.writeC(100 * pc.getCurrentHp() / pc.getMaxHp()); // HP顯示
		writeC(0xff); // HP顯示
		if (pc.hasSkillEffect(STATUS_BRAVE3)) {
			writeC(0x08); // 巧克力蛋糕

		} else {
			writeC(0x00); // タルクック距離(通り)
		}
		this.writeC(0x00); // LV
		if (pc.isPrivateShop()) {
			this.writeByte(pc.getShopChat());
			this.writeC(0);
		} else {
			this.writeC(0);
		}
		writeC(0xff);
		writeC(0xff);
		writeC(0);
		writeC(pc.getPolyStatus());
		writeC(0xFF);
		writeH(0);
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