package com.lineage.data.item_etcitem.skill;

import static com.lineage.server.model.skill.L1SkillId.DESPERADO;
import static com.lineage.server.model.skill.L1SkillId.GIGANTIC;
import static com.lineage.server.model.skill.L1SkillId.HOWL;
import static com.lineage.server.model.skill.L1SkillId.PASSIVE_ARMORGARDE;
import static com.lineage.server.model.skill.L1SkillId.PASSIVE_CRASH;
import static com.lineage.server.model.skill.L1SkillId.PASSIVE_FURY;
import static com.lineage.server.model.skill.L1SkillId.PASSIVE_SLAYER;
import static com.lineage.server.model.skill.L1SkillId.PASSIVE_TITANBULLET;
import static com.lineage.server.model.skill.L1SkillId.PASSIVE_TITANMAGIC;
import static com.lineage.server.model.skill.L1SkillId.PASSIVE_TITANROCK;
import static com.lineage.server.model.skill.L1SkillId.POWERGRIP;
import static com.lineage.server.model.skill.L1SkillId.TOMAHAWK;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>印記(戰士魔法)</font><BR>
 * Dark Spirit Crystal
 * 
 * @author simlin
 */
public class Skill_Warrior extends ItemExecutor {

	/**
	 *
	 */
	private Skill_Warrior() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_Warrior();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		// 不是戰士
		if (!pc.isWarrior()) {
			// 79 沒有任何事情發生
			final S_ServerMessage msg = new S_ServerMessage(79);
			pc.sendPackets(msg);

		} else {
			// 取得名稱
			final String nameId = item.getItem().getNameId();
			// 技能編號
			int skillid = 0;
			// 技能屬性 0:中立屬性魔法 1:正義屬性魔法 2:邪惡屬性魔法
			// 技能屬性 3:精靈專屬魔法 4:王族專屬魔法 5:騎士專屬技能 6:黑暗精靈專屬魔法
			// 技能屬性 7:龍騎士專屬魔法 8:幻術師專屬魔法 9:戰士專屬魔法
			final int attribute = 9;
			// 分組
			int magicLv = 0;
			/*
			 * 戰士的印記(粉碎)$17823 戰士的印記(狂暴)$17824 戰士的印記(迅猛雙斧)$17825
			 * 戰士的印記(護甲身軀)$17827 戰士的印記(泰坦: 岩石)$17828 戰士的印記(泰坦: 子彈)$17829
			 * 戰士的印記(泰坦: 魔法)$17830 戰士的印記(咆哮)$17831 戰士的印記(體能強化)$17832
			 * 戰士的印記(拘束移動)$17834 戰士的印記(戰斧投擲)$17840 戰士的印記(亡命之徒)$17856
			 */
			// TODO 1
			if (nameId.equalsIgnoreCase("$17823")) {// 戰士的印記(粉碎)
				// 技能編號
				skillid = PASSIVE_CRASH;
				// 分組
				magicLv = 73;

			} else if (nameId.equalsIgnoreCase("$17824")) {// 戰士的印記(狂暴)
				// 技能編號
				skillid = PASSIVE_FURY;
				// 分組
				magicLv = 76;

			} else if (nameId.equalsIgnoreCase("$17825")) {// 戰士的印記(迅猛雙斧)
				// 技能編號
				skillid = PASSIVE_SLAYER;
				// 分組
				magicLv = 71;

			} else if (nameId.equalsIgnoreCase("$17827")) {// 戰士的印記(護甲身軀)
				// 技能編號
				skillid = PASSIVE_ARMORGARDE;
				// 分組
				magicLv = 74;

			} else if (nameId.equalsIgnoreCase("$17828")) {// 戰士的印記(泰坦: 岩石)
				// 技能編號
				skillid = PASSIVE_TITANROCK;
				// 分組
				magicLv = 76;

			} else if (nameId.equalsIgnoreCase("$17829")) {// 戰士的印記(泰坦: 子彈)
				// 技能編號
				skillid = PASSIVE_TITANBULLET;
				// 分組
				magicLv = 79;

			} else if (nameId.equalsIgnoreCase("$17830")) {// 戰士的印記(泰坦: 魔法)
				// 技能編號
				skillid = PASSIVE_TITANMAGIC;
				// 分組
				magicLv = 78;

			} else if (nameId.equalsIgnoreCase("$17831")) {// 戰士的印記(咆哮)
				// 技能編號
				skillid = HOWL;
				// 分組
				magicLv = 72;

				// TODO 2
			} else if (nameId.equalsIgnoreCase("$17832")) {// 戰士的印記(體能強化)
				// 技能編號
				skillid = GIGANTIC;
				// 分組
				magicLv = 75;

			} else if (nameId.equalsIgnoreCase("$17834")) {// 戰士的印記(拘束移動)
				// 技能編號
				skillid = POWERGRIP;
				// 分組
				magicLv = 77;

			} else if (nameId.equalsIgnoreCase("$17840")) {// 戰士的印記(戰斧投擲)
				// 技能編號
				skillid = TOMAHAWK;
				// 分組
				magicLv = 73;

			} else if (nameId.equalsIgnoreCase("$17856")) {// 戰士的印記(亡命之徒)
				// 技能編號
				skillid = DESPERADO;
				// 分組
				magicLv = 76;
			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
