package com.lineage.server.model.skill;

import static com.lineage.server.model.skill.L1SkillId.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 衝突技能抵銷
 * 
 * @author dexc
 */
public class L1BuffUtil {

	private static final Log _log = LogFactory.getLog(L1BuffUtil.class);

	/**
	 * 全物件無法使用
	 * 
	 * @param pc 檢查對象
	 * @return
	 */
	public static boolean getUseItemAll(final L1PcInstance pc) {
		if (pc.hasSkillEffect(STATUS_CURSE_PARALYZED)) {
			return false;
		}
		if (pc.hasSkillEffect(STATUS_POISON_PARALYZED)) {
			return false;
		}
		if (pc.hasSkillEffect(FOG_OF_SLEEPING)) {
			return false;
		}
		if (pc.hasSkillEffect(SHOCK_STUN)) {
			return false;
		}
		if (pc.hasSkillEffect(ICE_LANCE)) {
			return false;
		}
		if (pc.hasSkillEffect(FREEZING_BREATH)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 無法使用指定類型技能(傳送技能)
	 * 
	 * @param pc 檢查對象
	 * @return
	 */
	public static boolean getUseSkillTeleport(final L1PcInstance pc) {
		// added by Erics4179
		if (pc.hasSkillEffect(MOVE_STOP)) {
			return false;
		}
		return true;
	}

	/**
	 * 無法使用指定類型道具(傳送卷軸)
	 * 
	 * @param pc 檢查對象
	 * @return
	 */
	public static boolean getUseItemTeleport(final L1PcInstance pc) {
		if (pc.hasSkillEffect(EARTH_BIND)) {
			return false;
		}
		if (pc.hasSkillEffect(SHOCK_SKIN)) {
			return false;
		}
		// added by terry0412
		if (pc.hasSkillEffect(MOVE_STOP)) {
			return false;
		}
		return true;
	}

	/**
	 * 無法使用藥水
	 * 
	 * @param pc
	 * @return true:可以使用 false:無法使用
	 */
	public static boolean stopPotion(final L1PcInstance pc) {
		if (pc.is_decay_potion()) { // 藥水霜化術
			// 698 喉嚨灼熱，無法喝東西。
			pc.sendPackets(new S_ServerMessage(698));
			return false;
		}
		return true;
	}

	/**
	 * 解除魔法技能绝对屏障
	 * 
	 * @param pc
	 */
	public static void cancelAbsoluteBarrier(final L1PcInstance pc) { // 解除魔法技能绝对屏障
		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
		}
	}

	/**
	 * 加速效果 抵銷對應技能
	 * 
	 * @param pc
	 */
	public static void hasteStart(final L1PcInstance pc) {
		try {
			// 解除加速術
			if (pc.hasSkillEffect(HASTE)) {
				pc.killSkillEffectTimer(HASTE);
				pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
				pc.setMoveSpeed(0);

			}

			// 解除強力加速術
			if (pc.hasSkillEffect(GREATER_HASTE)) {
				pc.killSkillEffectTimer(GREATER_HASTE);
				pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
				pc.setMoveSpeed(0);

			}

			// 解除加速藥水
			if (pc.hasSkillEffect(STATUS_HASTE)) {
				pc.killSkillEffectTimer(STATUS_HASTE);
				pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
				pc.setMoveSpeed(0);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 加速藥水效果
	 * 
	 * @param pc
	 * @param timeMillis
	 */
	public static void haste(final L1PcInstance pc, final int timeMillis) {
		try {
			hasteStart(pc);

			// 加速藥水效果
			pc.setSkillEffect(STATUS_HASTE, timeMillis);

			final int objId = pc.getId();
			pc.sendPackets(new S_SkillHaste(objId, 1, timeMillis / 1000));
			pc.broadcastPacketAll(new S_SkillHaste(objId, 1, 0));

			pc.sendPacketsX8(new S_SkillSound(objId, 191));
			pc.setMoveSpeed(1);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 勇敢效果 抵銷對應技能
	 * 
	 * @param pc
	 */
	public static void braveStart(final L1PcInstance pc) {
		try {
			/*
			 * { HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE,
			 * STATUS_BRAVE2, STATUS_ELFBRAVE, STATUS_RIBRAVE, BLOODLUST },
			 */

			// 解除神聖疾走
			if (pc.hasSkillEffect(HOLY_WALK)) {
				pc.killSkillEffectTimer(HOLY_WALK);
				pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}

			// 解除行走加速
			if (pc.hasSkillEffect(MOVING_ACCELERATION)) {
				pc.killSkillEffectTimer(MOVING_ACCELERATION);
				pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}

			// 解除風之疾走
			if (pc.hasSkillEffect(WIND_WALK)) {
				pc.killSkillEffectTimer(WIND_WALK);
				pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}

			// 解除勇敢藥水效果
			if (pc.hasSkillEffect(STATUS_BRAVE)) {
				pc.killSkillEffectTimer(STATUS_BRAVE);
				pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}

			// 解除精靈餅乾效果
			if (pc.hasSkillEffect(STATUS_ELFBRAVE)) {
				pc.killSkillEffectTimer(STATUS_ELFBRAVE);
				pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}

			// 解除生命之樹果實效果
			if (pc.hasSkillEffect(STATUS_RIBRAVE)) {
				pc.killSkillEffectTimer(STATUS_RIBRAVE);
				// XXX ユグドラの実のアイコンを消す方法が不明
				pc.setBraveSpeed(0);
			}

			// 解除血之渴望
			if (pc.hasSkillEffect(BLOODLUST)) {
				pc.killSkillEffectTimer(BLOODLUST);
				pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 勇敢藥水效果
	 * 
	 * @param pc 對象
	 * @param timeMillis TIME
	 */
	public static void brave(final L1PcInstance pc, final int timeMillis) {
		try {
			braveStart(pc);

			// 勇敢藥水效果
			pc.setSkillEffect(STATUS_BRAVE, timeMillis);

			final int objId = pc.getId();
			pc.sendPackets(new S_SkillBrave(objId, 1, timeMillis / 1000));
			pc.broadcastPacketAll(new S_SkillBrave(objId, 1, 0));

			pc.sendPacketsX8(new S_SkillSound(objId, 751));
			pc.setBraveSpeed(1);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 經驗加倍技能判斷(第一段)
	 * 
	 * @param pc
	 * @return
	 */
	public static boolean cancelExpSkill(final L1PcInstance pc) {
		// 停止初段技能
		if (pc.hasSkillEffect(COOKING_1_7_N)) {
			pc.removeSkillEffect(COOKING_1_7_N);
		}
		if (pc.hasSkillEffect(COOKING_1_7_S)) {
			pc.removeSkillEffect(COOKING_1_7_S);
		}
		if (pc.hasSkillEffect(COOKING_2_7_N)) {
			pc.removeSkillEffect(COOKING_2_7_N);
		}
		if (pc.hasSkillEffect(COOKING_2_7_S)) {
			pc.removeSkillEffect(COOKING_2_7_S);
		}
		if (pc.hasSkillEffect(COOKING_3_7_N)) {
			pc.removeSkillEffect(COOKING_3_7_N);
		}
		if (pc.hasSkillEffect(COOKING_3_7_S)) {
			pc.removeSkillEffect(COOKING_3_7_S);
		}

		// 返回已有技能
		if (pc.hasSkillEffect(EXP13)) {
			final int time = pc.getSkillEffectTimeSec(EXP13);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段130%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP15)) {
			final int time = pc.getSkillEffectTimeSec(EXP15);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段150%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP17)) {
			final int time = pc.getSkillEffectTimeSec(EXP17);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段170%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP20)) {
			final int time = pc.getSkillEffectTimeSec(EXP20);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段200%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP25)) {
			final int time = pc.getSkillEffectTimeSec(EXP25);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段250%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP30)) {
			final int time = pc.getSkillEffectTimeSec(EXP30);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段300%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP35)) {
			final int time = pc.getSkillEffectTimeSec(EXP35);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段350%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP40)) {
			final int time = pc.getSkillEffectTimeSec(EXP40);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段400%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP45)) {
			final int time = pc.getSkillEffectTimeSec(EXP45);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段450%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP50)) {
			final int time = pc.getSkillEffectTimeSec(EXP50);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段500%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP55)) {
			final int time = pc.getSkillEffectTimeSec(EXP55);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段550%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP60)) {
			final int time = pc.getSkillEffectTimeSec(EXP60);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段600%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP65)) {
			final int time = pc.getSkillEffectTimeSec(EXP65);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段650%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP70)) {
			final int time = pc.getSkillEffectTimeSec(EXP70);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段700%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP75)) {
			final int time = pc.getSkillEffectTimeSec(EXP75);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段750%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP80)) {
			final int time = pc.getSkillEffectTimeSec(EXP80);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段800%EXP 剩餘時間(秒):" + time));
			return false;
		}
		return true;
	}

	/**
	 * 經驗加倍技能判斷(第二段)
	 * 
	 * @param pc
	 * @return
	 */
	public static boolean cancelExpSkill_2(final L1PcInstance pc) {
		// 返回已有技能
		if (pc.hasSkillEffect(SEXP11)) {
			final int time = pc.getSkillEffectTimeSec(SEXP11);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("第二段110%EXP 剩餘時間(秒):" + time));
			return false;
		}
		// 返回已有技能
		if (pc.hasSkillEffect(SEXP13)) {
			final int time = pc.getSkillEffectTimeSec(SEXP13);
			// 3083 第二段經驗1.3倍效果時間尚有 %0 秒。
			pc.sendPackets(new S_ServerMessage("第二段130%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(SEXP15)) {
			final int time = pc.getSkillEffectTimeSec(SEXP15);
			// 3084 第二段經驗1.5倍效果時間尚有 %0 秒。
			pc.sendPackets(new S_ServerMessage("第二段150%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(SEXP17)) {
			final int time = pc.getSkillEffectTimeSec(SEXP17);
			// 3085 第二段經驗1.7倍效果時間尚有 %0 秒。
			pc.sendPackets(new S_ServerMessage("第二段170%EXP 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(SEXP20)) {
			final int time = pc.getSkillEffectTimeSec(SEXP20);
			// 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
			pc.sendPackets(new S_ServerMessage("第二段200%EXP 剩餘時間(秒):" + time));
			return false;
		}
		return true;
	}

	/**
	 * 四大龍物品
	 * 
	 * @param pc
	 * @return
	 */
	public static int cancelDragon(final L1PcInstance pc) {
		if (pc.hasSkillEffect(DRAGON1)) {
			return pc.getSkillEffectTimeSec(DRAGON1);
		}
		if (pc.hasSkillEffect(DRAGON2)) {
			return pc.getSkillEffectTimeSec(DRAGON2);
		}
		if (pc.hasSkillEffect(DRAGON3)) {
			return pc.getSkillEffectTimeSec(DRAGON3);
		}
		if (pc.hasSkillEffect(DRAGON4)) {
			return pc.getSkillEffectTimeSec(DRAGON4);
		}
		if (pc.hasSkillEffect(DRAGON5)) {
			return pc.getSkillEffectTimeSec(DRAGON5);
		}
		if (pc.hasSkillEffect(DRAGON6)) {
			return pc.getSkillEffectTimeSec(DRAGON6);
		}
		if (pc.hasSkillEffect(DRAGON7)) {
			return pc.getSkillEffectTimeSec(DRAGON7);
		}
		return -1;
	}

	public static void cancelBuffStone(final L1PcInstance pc) {
		final int[] skillids = new int[] {
				// 附魔石(近戰)
				BS_GX01, BS_GX02, BS_GX03, BS_GX04, BS_GX05, BS_GX06, BS_GX07, BS_GX08, BS_GX09,

				// 附魔石(遠攻)
				BS_AX01, BS_AX02, BS_AX03, BS_AX04, BS_AX05, BS_AX06, BS_AX07, BS_AX08, BS_AX09,

				// 附魔石(恢復)
				BS_WX01, BS_WX02, BS_WX03, BS_WX04, BS_WX05, BS_WX06, BS_WX07, BS_WX08, BS_WX09,

				// 附魔石(防禦)
				BS_ASX01, BS_ASX02, BS_ASX03, BS_ASX04, BS_ASX05, BS_ASX06, BS_ASX07, BS_ASX08, BS_ASX09, };
		for (int i = 0; i < skillids.length; i++) {
			if (pc.hasSkillEffect(skillids[i])) {
				pc.killSkillEffectTimer(skillids[i]);
			}
		}
	}

	public static int cancelDragonSign(final L1PcInstance pc) {
		final int[] skillids = new int[] {
				// 龍印魔石(鬥士)
				DS_GX00, DS_GX01, DS_GX02, DS_GX03, DS_GX04, DS_GX05, DS_GX06, DS_GX07, DS_GX08, DS_GX09,

				// 龍印魔石(弓手)
				DS_AX00, DS_AX01, DS_AX02, DS_AX03, DS_AX04, DS_AX05, DS_AX06, DS_AX07, DS_AX08, DS_AX09,

				// 龍印魔石(賢者)
				DS_WX00, DS_WX01, DS_WX02, DS_WX03, DS_WX04, DS_WX05, DS_WX06, DS_WX07, DS_WX08, DS_WX09,

				// 龍印魔石(衝鋒)
				DS_ASX00, DS_ASX01, DS_ASX02, DS_ASX03, DS_ASX04, DS_ASX05, DS_ASX06, DS_ASX07, DS_ASX08,
				DS_ASX09, };
		for (int i = 0; i < skillids.length; i++) {
			if (pc.hasSkillEffect(skillids[i])) {
				return pc.getSkillEffectTimeSec(skillids[i]);
			}
		}
		return -1;
	}

	/**
	 * XX色霸氣 by terry0412 [效果不能重疊]
	 * 
	 * @param pc
	 * @return
	 */
	public static final int getDominatePower(final L1PcInstance pc) {
		if (pc.hasSkillEffect(DOMINATE_POWER_A)) {
			return pc.getSkillEffectTimeSec(DOMINATE_POWER_A);
		}
		if (pc.hasSkillEffect(DOMINATE_POWER_B)) {
			return pc.getSkillEffectTimeSec(DOMINATE_POWER_B);
		}
		if (pc.hasSkillEffect(DOMINATE_POWER_C)) {
			return pc.getSkillEffectTimeSec(DOMINATE_POWER_C);
		}
		if (pc.hasSkillEffect(DOMINATE_POWER_D)) {
			return pc.getSkillEffectTimeSec(DOMINATE_POWER_D);
		}
		if (pc.hasSkillEffect(MAGIC_ITEM_POWER_A)) {
			return pc.getSkillEffectTimeSec(MAGIC_ITEM_POWER_A);
		}
		if (pc.hasSkillEffect(MAGIC_ITEM_POWER_B)) {
			return pc.getSkillEffectTimeSec(MAGIC_ITEM_POWER_B);
		}
		if (pc.hasSkillEffect(MAGIC_ITEM_POWER_C)) {
			return pc.getSkillEffectTimeSec(MAGIC_ITEM_POWER_C);
		}
		if (pc.hasSkillEffect(MAGIC_ITEM_POWER_D)) {
			return pc.getSkillEffectTimeSec(MAGIC_ITEM_POWER_D);
		}
		return -1;
	}
}
