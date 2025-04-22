package com.lineage.server.serverpackets;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;

import java.util.Random;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件封包 - 本身
 * 
 * @author dexc
 */
public class S_OwnCharPack extends ServerBasePacket {

	// private static final int STATUS_POISON = 1;
	private static final int STATUS_INVISIBLE = 2;
	private static final int STATUS_PC = 4;
	// private static final int STATUS_FREEZE = 8;
	private static final int STATUS_BRAVE = 16;
	private static final int STATUS_ELFBRAVE = 32;
	private static final int STATUS_FASTMOVABLE = 64;
	private static final int STATUS_GHOST = 128;

	public static final Random _random = new Random();

	private byte[] _byte = null;

	/**
	 * 物件封包 - 本身
	 * 
	 * @param pc
	 */
	public S_OwnCharPack(final L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(final L1PcInstance pc) {
		int status = STATUS_PC;

		if (pc.isInvisble() || pc.isGmInvis()) {
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
		if (pc.isGhost()) {
			status |= STATUS_GHOST;
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
		// writeC(addbyte);
		writeC(pc.getOwnLightSize());
		writeC(pc.getMoveSpeed());
		writeD((int) pc.getExp());
		writeH(pc.getLawful());
		writeS(pc.getViewName());
		writeS(pc.getTitle());
		writeC(status); // 狀態
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
		writeC(pc.getClanRank() > 0 ? pc.getClanRank() << 4 : 0xb0); // 血盟階級

		writeC(0xff); // HP顯示
		if (pc.hasSkillEffect(STATUS_BRAVE3)) {
			writeC(0x08); // 巧克力蛋糕

		} else {
			writeC(0x00); // タルクック距離(通り)
		}
		writeC(0x00); // LV
		writeC(0x00);
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