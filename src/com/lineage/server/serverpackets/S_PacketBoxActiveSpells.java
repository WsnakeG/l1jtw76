package com.lineage.server.serverpackets;

import static com.lineage.server.model.skill.L1SkillId.ADLV80_1;
import static com.lineage.server.model.skill.L1SkillId.ADLV80_2;
import static com.lineage.server.model.skill.L1SkillId.BS_ASX01;
import static com.lineage.server.model.skill.L1SkillId.BS_ASX02;
import static com.lineage.server.model.skill.L1SkillId.BS_ASX03;
import static com.lineage.server.model.skill.L1SkillId.BS_ASX04;
import static com.lineage.server.model.skill.L1SkillId.BS_ASX05;
import static com.lineage.server.model.skill.L1SkillId.BS_ASX06;
import static com.lineage.server.model.skill.L1SkillId.BS_ASX07;
import static com.lineage.server.model.skill.L1SkillId.BS_ASX08;
import static com.lineage.server.model.skill.L1SkillId.BS_ASX09;
import static com.lineage.server.model.skill.L1SkillId.BS_AX01;
import static com.lineage.server.model.skill.L1SkillId.BS_AX02;
import static com.lineage.server.model.skill.L1SkillId.BS_AX03;
import static com.lineage.server.model.skill.L1SkillId.BS_AX04;
import static com.lineage.server.model.skill.L1SkillId.BS_AX05;
import static com.lineage.server.model.skill.L1SkillId.BS_AX06;
import static com.lineage.server.model.skill.L1SkillId.BS_AX07;
import static com.lineage.server.model.skill.L1SkillId.BS_AX08;
import static com.lineage.server.model.skill.L1SkillId.BS_AX09;
import static com.lineage.server.model.skill.L1SkillId.BS_GX01;
import static com.lineage.server.model.skill.L1SkillId.BS_GX02;
import static com.lineage.server.model.skill.L1SkillId.BS_GX03;
import static com.lineage.server.model.skill.L1SkillId.BS_GX04;
import static com.lineage.server.model.skill.L1SkillId.BS_GX05;
import static com.lineage.server.model.skill.L1SkillId.BS_GX06;
import static com.lineage.server.model.skill.L1SkillId.BS_GX07;
import static com.lineage.server.model.skill.L1SkillId.BS_GX08;
import static com.lineage.server.model.skill.L1SkillId.BS_GX09;
import static com.lineage.server.model.skill.L1SkillId.BS_WX01;
import static com.lineage.server.model.skill.L1SkillId.BS_WX02;
import static com.lineage.server.model.skill.L1SkillId.BS_WX03;
import static com.lineage.server.model.skill.L1SkillId.BS_WX04;
import static com.lineage.server.model.skill.L1SkillId.BS_WX05;
import static com.lineage.server.model.skill.L1SkillId.BS_WX06;
import static com.lineage.server.model.skill.L1SkillId.BS_WX07;
import static com.lineage.server.model.skill.L1SkillId.BS_WX08;
import static com.lineage.server.model.skill.L1SkillId.BS_WX09;
import static com.lineage.server.model.skill.L1SkillId.DRAGON1;
import static com.lineage.server.model.skill.L1SkillId.DRAGON2;
import static com.lineage.server.model.skill.L1SkillId.DRAGON3;
import static com.lineage.server.model.skill.L1SkillId.DRAGON4;
import static com.lineage.server.model.skill.L1SkillId.DRAGON5;
import static com.lineage.server.model.skill.L1SkillId.DRAGON6;
import static com.lineage.server.model.skill.L1SkillId.DRAGON7;
import static com.lineage.server.model.skill.L1SkillId.DRESS_EVASION;
import static com.lineage.server.model.skill.L1SkillId.RESIST_FEAR;
import static com.lineage.server.model.skill.L1SkillId.STATUS_RIBRAVE;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_PacketBoxActiveSpells extends ServerBasePacket {

	private byte[] _byte = null;

	public S_PacketBoxActiveSpells(final L1PcInstance pc) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x14);
		final int[] type = activeSpells(pc);
		for (final int i : type) {
			switch (i) {
			case 72:
				writeD((int) (System.currentTimeMillis() / 1000));
				break;
			default:
				writeC(i);
				break;
			}
		}
	}

	// 登入時給于角色狀態剩餘時間
	private int[] activeSpells(final L1PcInstance pc) {
		final int[] data = new int[104];
		// 生命之樹果實
		if (pc.hasSkillEffect(STATUS_RIBRAVE)) {
			data[61] = pc.getSkillEffectTimeSec(STATUS_RIBRAVE) >> 2;// / 4;
		}

		// 迴避提升
		if (pc.hasSkillEffect(DRESS_EVASION)) {
			data[17] = pc.getSkillEffectTimeSec(DRESS_EVASION) >> 2;// / 4;
		}

		// 恐懼無助
		if (pc.hasSkillEffect(RESIST_FEAR)) {
			data[57] = pc.getSkillEffectTimeSec(RESIST_FEAR) >> 2;// / 4;
		}
		/*
		 * // 象牙塔妙藥 if (pc.hasSkillEffect(COOKING_WONDER_DRUG)) { data[42] =
		 * pc.getSkillEffectTimeSec(COOKING_WONDER_DRUG) / 4; if (data[42] != 0)
		 * { data[43] = 54; // 因為妙藥，身心都很輕鬆。提升體力回復量和魔力回復量。 } } // 戰鬥藥水 if
		 * (pc.hasSkillEffect(EFFECT_POTION_OF_BATTLE)) { data[45] =
		 * pc.getSkillEffectTimeSec(EFFECT_POTION_OF_BATTLE) / 16; if (data[45]
		 * != 0) { data[62] = 20; // 經驗值加成20%。 } } // 150% ~ 250% 神力藥水 for (int
		 * i = 0; i < 5; i++) { if (pc.hasSkillEffect(EFFECT_POTION_OF_EXP_150 +
		 * i)) { data[45] = pc.getSkillEffectTimeSec(EFFECT_POTION_OF_EXP_150 +
		 * i) / 16; if (data[45] != 0) { data[62] = 50; // 狩獵經驗值將會增加。 } } } //
		 * 媽祖的祝福 if (pc.hasSkillEffect(EFFECT_BLESS_OF_MAZU)) { data[48] =
		 * pc.getSkillEffectTimeSec(EFFECT_BLESS_OF_MAZU) / 16; if (data[48] !=
		 * 0) { data[49] = 44; // 感受到媽祖的祝福。 } } // 體力增強卷軸、魔力增強卷軸、強化戰鬥卷軸 for (int
		 * i = 0; i < 3; i++) { if (pc.hasSkillEffect(EFFECT_STRENGTHENING_HP +
		 * i)) { data[46] = pc.getSkillEffectTimeSec(EFFECT_STRENGTHENING_HP +
		 * i) / 16; if (data[46] != 0) { data[47] = i; // 體力上限+50，體力回復+4。 } } }
		 */

		// 附魔石(近戰)
		final int[] bs_gx = new int[] { BS_GX01, BS_GX02, BS_GX03, BS_GX04, BS_GX05, BS_GX06, BS_GX07,
				BS_GX08, BS_GX09, };
		for (int i = 0; i < bs_gx.length; i++) {
			if (pc.hasSkillEffect(bs_gx[i])) {
				data[102] = pc.getSkillEffectTimeSec(bs_gx[i]) >> 5;// /32
				if (data[102] != 0) {
					data[103] = bs_gx[i] - 4317;
				}
			}
		}

		// 附魔石(遠攻)
		final int[] bs_ax = new int[] { BS_AX01, BS_AX02, BS_AX03, BS_AX04, BS_AX05, BS_AX06, BS_AX07,
				BS_AX08, BS_AX09, };
		for (int i = 0; i < bs_ax.length; i++) {
			if (pc.hasSkillEffect(bs_ax[i])) {
				data[102] = pc.getSkillEffectTimeSec(bs_ax[i]) >> 5;// /32
				if (data[102] != 0) {
					data[103] = bs_ax[i] - 4318;
				}
			}
		}

		// 附魔石(恢復)
		final int[] bs_wx = new int[] { BS_WX01, BS_WX02, BS_WX03, BS_WX04, BS_WX05, BS_WX06, BS_WX07,
				BS_WX08, BS_WX09, };
		for (int i = 0; i < bs_wx.length; i++) {
			if (pc.hasSkillEffect(bs_wx[i])) {
				data[102] = pc.getSkillEffectTimeSec(bs_wx[i]) >> 5;// /32
				if (data[102] != 0) {
					data[103] = bs_wx[i] - 4319;
				}
			}
		}

		// 附魔石(防禦)
		final int[] bs_asx = new int[] { BS_ASX01, BS_ASX02, BS_ASX03, BS_ASX04, BS_ASX05, BS_ASX06, BS_ASX07,
				BS_ASX08, BS_ASX09, };
		for (int i = 0; i < bs_asx.length; i++) {
			if (pc.hasSkillEffect(bs_asx[i])) {
				data[102] = pc.getSkillEffectTimeSec(bs_asx[i]) >> 5;// /32
				if (data[102] != 0) {
					data[103] = bs_asx[i] - 4320;
				}
			}
		}

		// 龍之魔眼
		if (pc.hasSkillEffect(DRAGON1)) {// 火
			data[78] = pc.getSkillEffectTimeSec(DRAGON1) >> 5;// /32
			if (data[78] != 0) {
				data[79] = 49;
			}
		}
		if (pc.hasSkillEffect(DRAGON2)) {// 地
			data[78] = pc.getSkillEffectTimeSec(DRAGON2) >> 5;// /32
			if (data[78] != 0) {
				data[79] = 46;
			}
		}
		if (pc.hasSkillEffect(DRAGON3)) {// 水
			data[78] = pc.getSkillEffectTimeSec(DRAGON3) >> 5;// /32
			if (data[78] != 0) {
				data[79] = 47;
			}
		}
		if (pc.hasSkillEffect(DRAGON4)) {// 風
			data[78] = pc.getSkillEffectTimeSec(DRAGON4) >> 5;// /32
			if (data[78] != 0) {
				data[79] = 48;
			}
		}
		if (pc.hasSkillEffect(DRAGON5)) {// 生命
			data[78] = pc.getSkillEffectTimeSec(DRAGON5) >> 5;// /32
			if (data[78] != 0) {
				data[79] = 52;
			}
		}
		if (pc.hasSkillEffect(DRAGON6)) {// 誕生
			data[78] = pc.getSkillEffectTimeSec(DRAGON6) >> 5;// /32
			if (data[78] != 0) {
				data[79] = 50;
			}
		}
		if (pc.hasSkillEffect(DRAGON7)) {// 形象
			data[78] = pc.getSkillEffectTimeSec(DRAGON7) >> 5;// /32
			if (data[78] != 0) {
				data[79] = 51;
			}
		}
		// 卡瑞的祝福
		if (pc.hasSkillEffect(ADLV80_1)) {
			data[76] = pc.getSkillEffectTimeSec(ADLV80_1) >> 5;// /32
			if (data[76] != 0) {
				data[77] = 45;
			}
		}
		// 莎爾的祝福
		if (pc.hasSkillEffect(ADLV80_2)) {
			data[76] = pc.getSkillEffectTimeSec(ADLV80_2) >> 5;// /32
			if (data[76] != 0) {
				data[77] = 60;
			}
		}

		return data;
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
