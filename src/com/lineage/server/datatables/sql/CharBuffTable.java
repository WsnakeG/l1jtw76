package com.lineage.server.datatables.sql;

import static com.lineage.server.model.skill.L1SkillId.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.data.item_etcitem.dominate.PowerItem1;
import com.lineage.data.item_etcitem.dominate.PowerItem2;
import com.lineage.data.item_etcitem.dominate.PowerItem3;
import com.lineage.data.item_etcitem.dominate.PowerItem4;
import com.lineage.data.item_etcitem.extra.ItemBuffTable;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.storage.CharBuffStorage;
import com.lineage.server.model.L1Cooking;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_Icons;
import com.lineage.server.serverpackets.S_Liquor;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxCooking;
import com.lineage.server.serverpackets.S_PacketBoxThirdSpeed;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.templates.L1BuffTmp;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 保留技能紀錄
 * 
 * @author dexc
 */
public class CharBuffTable implements CharBuffStorage {

	private static final Log _log = LogFactory.getLog(CharBuffTable.class);

	private static final Map<Integer, ArrayList<L1BuffTmp>> _buffMap = new HashMap<Integer, ArrayList<L1BuffTmp>>();

	// 執行保留的技能
	private static final int[] _buffSkill = { LIGHT, // 日光術

			SHAPE_CHANGE, // 變形術

			HASTE, // 加速術
			GREATER_HASTE, // 強力加速術

			SHIELD, // 保護罩
			SHADOW_ARMOR, // 影之防護
			EARTH_SKIN, // 大地防護
			EARTH_GUARDIAN, // 大地的祝福
			IRON_SKIN, // 鋼鐵防護

			STATUS_BRAVE, // 勇敢藥水效果
			STATUS_HASTE, // 加速藥水效果
			STATUS_ELFBRAVE, // 精靈餅乾效果
			HOLY_WALK, // 神聖疾走
			MOVING_ACCELERATION, // 行走加速
			WIND_WALK, // 風之疾走

			PHYSICAL_ENCHANT_DEX, // 通暢氣脈術
			PHYSICAL_ENCHANT_STR, // 體魄強健術
			DRESS_MIGHTY, // 力量提升
			DRESS_DEXTERITY, // 敏捷提升

			GLOWING_WEAPON, // 激勵士氣
			SHINING_AURA, // 鋼鐵士氣
			BRAVE_MENTAL, // 衝擊士氣

			FIRE_WEAPON, // 火焰武器
			DANCING_BLAZE, // 烈炎氣息
			BURNING_WEAPON, // 烈炎武器

			WIND_SHOT, // 風之神射
			STORM_EYE, // 暴風之眼
			STORM_SHOT, // 暴風神射

			STATUS_BLUE_POTION, // 魔力回復藥水效果
			STATUS_CHAT_PROHIBITED, // 禁言效果

			// 150130 By erics4179 加入
			HAPPY_TIME, // GM處罰指令-快樂時光

			COOKING_1_0_N, COOKING_1_0_S, COOKING_1_1_N, COOKING_1_1_S, // 料理(デザートは除く)
			COOKING_1_2_N, COOKING_1_2_S, COOKING_1_3_N, COOKING_1_3_S, COOKING_1_4_N, COOKING_1_4_S,
			COOKING_1_5_N, COOKING_1_5_S, COOKING_1_6_N, COOKING_1_6_S, COOKING_2_0_N, COOKING_2_0_S,
			COOKING_2_1_N, COOKING_2_1_S, COOKING_2_2_N, COOKING_2_2_S, COOKING_2_3_N, COOKING_2_3_S,
			COOKING_2_4_N, COOKING_2_4_S, COOKING_2_5_N, COOKING_2_5_S, COOKING_2_6_N, COOKING_2_6_S,
			COOKING_3_0_N, COOKING_3_0_S, COOKING_3_1_N, COOKING_3_1_S, COOKING_3_2_N, COOKING_3_2_S,
			COOKING_3_3_N, COOKING_3_3_S, COOKING_3_4_N, COOKING_3_4_S, COOKING_3_5_N, COOKING_3_5_S,
			COOKING_3_6_N, COOKING_3_6_S,

			EXP13, EXP15, EXP17, EXP20, EXP25, EXP30, EXP35, EXP40, EXP45, EXP50, EXP55, EXP60, EXP65, EXP70,
			EXP75, EXP80, // 第一段經驗加倍

			SEXP13, SEXP15, SEXP17, SEXP20, // 第二段經驗加倍

			REEXP20, // 第三段經驗加倍

			STATUS_BRAVE3, // 巧克力蛋糕

			STATUS_RIBRAVE, // 生命之樹果實
			DRESS_EVASION, // 迴避提升
			RESIST_FEAR, // 恐懼無助

			// 四大龍物品
			DRAGON1, DRAGON2, DRAGON3, DRAGON4, DRAGON5, DRAGON6, DRAGON7,

			// 附魔石(近戰)
			BS_GX01, BS_GX02, BS_GX03, BS_GX04, BS_GX05, BS_GX06, BS_GX07, BS_GX08, BS_GX09,
			// 附魔石(遠攻)
			BS_AX01, BS_AX02, BS_AX03, BS_AX04, BS_AX05, BS_AX06, BS_AX07, BS_AX08, BS_AX09,
			// 附魔石(恢復)
			BS_WX01, BS_WX02, BS_WX03, BS_WX04, BS_WX05, BS_WX06, BS_WX07, BS_WX08, BS_WX09,
			// 附魔石(防禦)
			BS_ASX01, BS_ASX02, BS_ASX03, BS_ASX04, BS_ASX05, BS_ASX06, BS_ASX07, BS_ASX08, BS_ASX09,

			// 龍印魔石(鬥士)
			DS_GX00, DS_GX01, DS_GX02, DS_GX03, DS_GX04, DS_GX05, DS_GX06, DS_GX07, DS_GX08, DS_GX09,
			// 龍印魔石(弓手)
			DS_AX00, DS_AX01, DS_AX02, DS_AX03, DS_AX04, DS_AX05, DS_AX06, DS_AX07, DS_AX08, DS_AX09,
			// 龍印魔石(賢者)
			DS_WX00, DS_WX01, DS_WX02, DS_WX03, DS_WX04, DS_WX05, DS_WX06, DS_WX07, DS_WX08, DS_WX09,
			// 龍印魔石(衝鋒)
			DS_ASX00, DS_ASX01, DS_ASX02, DS_ASX03, DS_ASX04, DS_ASX05, DS_ASX06, DS_ASX07, DS_ASX08,
			DS_ASX09,

			// 三國
			SCORE02, // 積分加倍(2倍)
			SCORE03, // 積分加倍(3倍)
			SCORE04, // 積分加倍(4倍)
			CHAT_STOP, // 禁言卡

			// XX色霸氣 (重登效果保留) by terry0412
			DOMINATE_POWER_A, DOMINATE_POWER_B, DOMINATE_POWER_C, DOMINATE_POWER_D,

			AICHECK_PN, MAZU_STATUS,AIFORSTART,
			
			3345678

	};

	/**
	 * 初始化載入
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `character_buff`");
			rs = ps.executeQuery();
			while (rs.next()) {
				final int char_obj_id = rs.getInt("char_obj_id");

				// 檢查該資料所屬是否遺失
				if (CharObjidTable.get().isChar(char_obj_id) != null) {
					final int skill_id = rs.getInt("skill_id");
					final int remaining_time = rs.getInt("remaining_time");
					final int poly_id = rs.getInt("poly_id");

					final L1BuffTmp buffTmp = new L1BuffTmp();
					buffTmp.set_char_obj_id(char_obj_id);
					buffTmp.set_skill_id(skill_id);
					buffTmp.set_remaining_time(remaining_time);
					buffTmp.set_poly_id(poly_id);

					addMap(char_obj_id, buffTmp);

				} else {
					delete(char_obj_id);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入保留技能紀錄資料數量: " + _buffMap.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 刪除遺失資料
	 * 
	 * @param objid
	 */
	private static void delete(final int objid) {
		final ArrayList<L1BuffTmp> list = _buffMap.get(objid);
		if (list != null) {
			list.clear();// 移除此列表中的所有元素
		}
		_buffMap.remove(objid);

		// 清空資料庫紀錄
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `character_buff` WHERE `char_obj_id`=?");
			ps.setInt(1, objid);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);

		}
	}

	/**
	 * 加入清單
	 * 
	 * @param objId
	 * @param buffTmp
	 */
	private static void addMap(final int objId, final L1BuffTmp buffTmp) {
		final ArrayList<L1BuffTmp> list = _buffMap.get(objId);
		if (list == null) {
			final ArrayList<L1BuffTmp> newlist = new ArrayList<L1BuffTmp>();
			newlist.add(buffTmp);
			_buffMap.put(objId, newlist);

		} else {
			list.add(buffTmp);
		}
	}

	/**
	 * 增加保留技能紀錄
	 * 
	 * @param pc
	 */
	@Override
	public void saveBuff(final L1PcInstance pc) {
		for (final int skillId : _buffSkill) {
			final int timeSec = pc.getSkillEffectTimeSec(skillId);
			if (0 < timeSec) {
				int polyId = -1;
				if (skillId == SHAPE_CHANGE) {
					polyId = pc.getTempCharGfx();
				}
				storeBuff(pc.getId(), skillId, timeSec, polyId);
			}
			ItemBuffTable.get().checkBuffSave(pc);
		}
		// 刪除全部技能效果
		pc.clearSkillEffectTimer();
	}

	/**
	 * 寫入保留技能紀錄
	 * 
	 * @param objId
	 * @param skillId
	 * @param time
	 * @param polyId
	 */
//	private void storeBuff(final int objId, final int skillId, final int time, final int polyId) {
//		final L1BuffTmp buffTmp = new L1BuffTmp();
//		buffTmp.set_char_obj_id(objId);
//		buffTmp.set_skill_id(skillId);
//		buffTmp.set_remaining_time(time);
//		buffTmp.set_poly_id(polyId);
//		// 加入MAP
//		addMap(objId, buffTmp);
//		// 寫入資料庫
//		storeBuffR(buffTmp);
//	}
	
	/**
	 * 寫入保留技能紀錄
	 * 
	 * @param objId
	 * @param skillId
	 * @param time
	 * @param polyId
	 */
	// private void storeBuff(int objId, int skillId, int time, int polyId) {
	public static void storeBuff(int objId, int skillId, int time, int polyId) {
		L1BuffTmp buffTmp = new L1BuffTmp();
		buffTmp.set_char_obj_id(objId);
		buffTmp.set_skill_id(skillId);
		buffTmp.set_remaining_time(time);
		buffTmp.set_poly_id(polyId);

		// 加入MAP
		addMap(objId, buffTmp);
		// 寫入資料庫
		storeBuffR(buffTmp);
	}

	/**
	 * 取回保留技能紀錄
	 * 
	 * @param pc
	 */
	@Override
	public void buff(final L1PcInstance pc) {
		final int objid = pc.getId();
		final ArrayList<L1BuffTmp> list = _buffMap.get(objid);
		if (list != null) {
			for (final L1BuffTmp buffTmp : list) {
				// 取回資料
				final int skill_id = buffTmp.get_skill_id();
				final int remaining_time = buffTmp.get_remaining_time();// 秒
				final int poly_id = buffTmp.get_poly_id();

				if (remaining_time > 0) {
					if (poly_id != -1) { // 變身
						L1PolyMorph.doPoly(pc, poly_id, remaining_time, L1PolyMorph.MORPH_BY_LOGIN);

					} else {
						switch (skill_id) {
						case MAZU_STATUS:
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							pc.sendPackets(new S_Icons(pc));
							break;
						case STATUS_BRAVE3: // 巧克力蛋糕
							pc.sendPackets(new S_PacketBoxThirdSpeed(remaining_time));
							pc.sendPacketsAll(new S_Liquor(pc.getId(), 0x08));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case STATUS_BRAVE: // 勇敢藥水效果
							pc.sendPackets(new S_SkillBrave(pc.getId(), 1, remaining_time));
							pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 1, 0));
							pc.setBraveSpeed(1);
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case STATUS_ELFBRAVE: // 精靈餅乾效果
							pc.sendPackets(new S_SkillBrave(pc.getId(), 3, remaining_time));
							pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 3, 0));
							pc.setBraveSpeed(1);
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case STATUS_HASTE: // 加速藥水效果
							pc.sendPackets(new S_SkillHaste(pc.getId(), 1, remaining_time));
							pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
							pc.setMoveSpeed(1);
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case STATUS_BLUE_POTION: // 藍水
							pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_BLUEPOTION, remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case STATUS_CHAT_PROHIBITED: // 禁言
						case CHAT_STOP: // 禁言卡
							pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_CHATBAN, remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case EXP13: // 第一段1.3倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段1.3倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP15: // 第一段1.5倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段1.5倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP17: // 第一段1.7倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段1.7倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP20: // 第一段2.0倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段2.0倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP25: // 第一段2.5倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段2.5倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP30: // 第一段3.0倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段3.0倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP35: // 第一段3.5倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段3.5倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP40: // 第一段4.0倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段4.0倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP45: // 第一段4.5倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段4.5倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP50: // 第一段5.0倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段5.0倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP55: // 第一段5.5倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段5.5倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP60: // 第一段6.0倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段6.0倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP65: // 第一段6.5倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段6.5倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP70: // 第一段7.0倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段7.0倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP75: // 第一段7.5倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段7.5倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP80: // 第一段8.0倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段8.0倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case SEXP13: // 第二段1.3倍經驗
							// 3083 第二段經驗1.3倍效果時間尚有 %0 秒。
							pc.sendPackets(new S_ServerMessage("第二段1.3倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case SEXP15: // 第二段1.5倍經驗
							// 3084 第二段經驗1.5倍效果時間尚有 %0 秒。
							pc.sendPackets(new S_ServerMessage("第二段1.5倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case SEXP17: // 第二段1.7倍經驗
							// 3085 第二段經驗1.7倍效果時間尚有 %0 秒。
							pc.sendPackets(new S_ServerMessage("第二段1.7倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case SEXP20: // 第二段2.0倍經驗
							// 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
							pc.sendPackets(new S_ServerMessage("第二段2.0倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case REEXP20: // 第三段雙倍經驗
							// 3086 特殊經驗雙倍效果時間尚有 %0 秒。
							pc.sendPackets(new S_ServerMessage("第三段2.0倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case SCORE02: // 積分加倍(2倍)
							pc.sendPackets(new S_ServerMessage("積分加倍(2倍)作用中"));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case SCORE03: // 積分加倍(3倍)
							pc.sendPackets(new S_ServerMessage("積分加倍(3倍)作用中"));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;
							
						case SCORE04: // 積分加倍(4倍)
							pc.sendPackets(new S_ServerMessage("積分加倍(4倍)作用中"));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						// XX色霸氣 (重登效果保留) by terry0412
						case DOMINATE_POWER_A: // 霸王色霸氣
							pc.sendPackets(new S_ServerMessage("[特殊技能] 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							final String[] set1 = PowerItem1.get().get_set();
							// 霸氣效果 暫存
							pc.setValue(Integer.parseInt(set1[0]));
							// 特效編號
							pc.setEffectId(Integer.parseInt(set1[1]));
							break;

						case DOMINATE_POWER_B: // 聞見色霸氣
							pc.sendPackets(new S_ServerMessage("[特殊技能] 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							final String[] set2 = PowerItem2.get().get_set();
							// 霸氣效果 暫存
							pc.setValue(Integer.parseInt(set2[0]));
							// 特效編號
							pc.setEffectId(Integer.parseInt(set2[1]));
							break;

						case DOMINATE_POWER_C: // 武裝色霸氣
							pc.sendPackets(new S_ServerMessage("[特殊技能] 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							final String[] set3 = PowerItem3.get().get_set();
							// 霸氣效果 暫存
							pc.setValue(Integer.parseInt(set3[0]));
							// 特效編號
							pc.setEffectId(Integer.parseInt(set3[1]));
							break;

						case DOMINATE_POWER_D: // 魔化色霸氣
							pc.sendPackets(new S_ServerMessage("[特殊技能] 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							final String[] set4 = PowerItem4.get().get_set();
							// 霸氣效果 暫存
							pc.setValue(Integer.parseInt(set4[0]));
							// 特效編號
							pc.setEffectId(Integer.parseInt(set4[1]));
							break;

						case COOKING_1_0_N:
						case COOKING_1_1_N:
						case COOKING_1_2_N:
						case COOKING_1_3_N:
						case COOKING_1_4_N:
						case COOKING_1_5_N:
						case COOKING_1_6_N:
						case COOKING_1_7_N:
						case COOKING_1_0_S:
						case COOKING_1_1_S:
						case COOKING_1_2_S:
						case COOKING_1_3_S:
						case COOKING_1_4_S:
						case COOKING_1_5_S:
						case COOKING_1_6_S:
						case COOKING_1_7_S:
						case COOKING_2_0_N:
						case COOKING_2_1_N:
						case COOKING_2_2_N:
						case COOKING_2_3_N:
						case COOKING_2_4_N:
						case COOKING_2_5_N:
						case COOKING_2_6_N:
						case COOKING_2_7_N:
						case COOKING_2_0_S:
						case COOKING_2_1_S:
						case COOKING_2_2_S:
						case COOKING_2_3_S:
						case COOKING_2_4_S:
						case COOKING_2_5_S:
						case COOKING_2_6_S:
						case COOKING_2_7_S:
						case COOKING_3_0_N:
						case COOKING_3_1_N:
						case COOKING_3_2_N:
						case COOKING_3_3_N:
						case COOKING_3_4_N:
						case COOKING_3_5_N:
						case COOKING_3_6_N:
						case COOKING_3_7_N:
						case COOKING_3_0_S:
						case COOKING_3_1_S:
						case COOKING_3_2_S:
						case COOKING_3_3_S:
						case COOKING_3_4_S:
						case COOKING_3_5_S:
						case COOKING_3_6_S:
						case COOKING_3_7_S:
							L1Cooking.eatCooking(pc, skill_id, remaining_time);
							break;
						default:
							// SKILL移轉
							final SkillMode mode = L1SkillMode.get().getSkill(skill_id);
							if (mode != null) {
								try {
									mode.start(pc, pc, null, remaining_time);

								} catch (final Exception e) {
									_log.error(e.getLocalizedMessage(), e);
								}

							}  else {// 沒有SkillMode
								L1SkillUse l1skilluse = new L1SkillUse();
								l1skilluse.handleCommands(pc, skill_id, pc.getId(), pc.getX(), pc.getY(),
										remaining_time, L1SkillUse.TYPE_LOGIN);
							}
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * 刪除全部保留技能紀錄
	 * 
	 * @param pc
	 */
	@Override
	public void deleteBuff(final L1PcInstance pc) {
		delete(pc.getId());
	}

	/**
	 * 刪除全部保留技能紀錄
	 * 
	 * @param objid
	 */
	@Override
	public void deleteBuff(final int objid) {
		delete(objid);
	}

	/**
	 * 寫入保留技能紀錄
	 * 
	 * @param buffTmp
	 */
	private static void storeBuffR(final L1BuffTmp buffTmp) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement(
					"INSERT INTO `character_buff` SET `char_obj_id`=?,`skill_id`=?,`remaining_time`=?,`poly_id`=?");
			ps.setInt(1, buffTmp.get_char_obj_id());
			ps.setInt(2, buffTmp.get_skill_id());
			ps.setInt(3, buffTmp.get_remaining_time());
			ps.setInt(4, buffTmp.get_poly_id());

			ps.execute();

		} catch (final SQLException e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}
}
