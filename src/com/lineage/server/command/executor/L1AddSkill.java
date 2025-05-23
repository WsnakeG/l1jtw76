package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_AddSkill;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_WarriorSkill;
import com.lineage.server.templates.L1Skills;

/**
 * 賦予該gm職業所有技能
 * 
 * @author dexc
 */
public class L1AddSkill implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1AddSkill.class);

	private L1AddSkill() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AddSkill();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			int cnt = 0; // ループカウンタ
			String skill_name = ""; // スキル名
			int skill_id = 0; // スキルID

			final int object_id = pc.getId(); // キャラクタのobjectidを取得
			pc.sendPacketsX8(new S_SkillSound(object_id, '\343')); // 魔法習得の効果音を鳴らす

			if (pc.isCrown()) {// 王族
				pc.sendPackets(new S_AddSkill(pc, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

				for (cnt = 1; cnt <= 16; cnt++) {// LV1~2魔法
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}

				for (cnt = 113; cnt <= 120; cnt++) {// プリ魔法
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}

			} else if (pc.isKnight()) {// 騎士
				pc.sendPackets(new S_AddSkill(pc, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 192, 7, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

				for (cnt = 1; cnt <= 8; cnt++) {// LV1魔法
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}

				for (cnt = 87; cnt <= 91; cnt++) {// ナイト魔法
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}

			} else if (pc.isElf()) {// 精靈
				pc.sendPackets(new S_AddSkill(pc, 255, 255, 127, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						127, 3, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0));
				for (cnt = 1; cnt <= 48; cnt++) {// LV1~6魔法
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}
				for (cnt = 129; cnt <= 176; cnt++) {// エルフ魔法
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}

			} else if (pc.isWizard()) {// 法師
				pc.sendPackets(new S_AddSkill(pc, 255, 255, 127, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
				for (cnt = 1; cnt <= 80; cnt++) {// LV1~10魔法
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}

			} else if (pc.isDarkelf()) {// 黑妖
				pc.sendPackets(new S_AddSkill(pc, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
				for (cnt = 1; cnt <= 16; cnt++) {// LV1~2魔法
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}
				for (cnt = 97; cnt <= 112; cnt++) {// DE魔法
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}

			} else if (pc.isDragonKnight()) {// 龍騎
				pc.sendPackets(new S_AddSkill(pc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 240, 255, 7, 0, 0, 0, 0, 0));
				for (cnt = 181; cnt <= 195; cnt++) {// ドラゴンナイト秘技
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}

			} else if (pc.isIllusionist()) {// 幻術師
				pc.sendPackets(new S_AddSkill(pc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 255, 255, 15, 0, 0));
				for (cnt = 201; cnt <= 220; cnt++) {// イリュージョニスト魔法
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}

			} else if (pc.isWarrior()) {// 戰士
				pc.sendPackets(new S_AddSkill(pc, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 127));

				for (cnt = 1; cnt <= 8; cnt++) {// LV1魔法
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}

				for (cnt = 225; cnt <= 230; cnt++) {// 戰士魔法lv1
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}

				for (cnt = 233; cnt <= 239; cnt++) {// 戰士魔法lv2
					final L1Skills l1skills = SkillsTable.get().getTemplate(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録

					pc.sendPackets(new S_WarriorSkill(S_WarriorSkill.ADD, l1skills.getSkillNumber()));
				}
			}

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
