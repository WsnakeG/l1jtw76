package com.lineage.server.command.executor;

import static com.lineage.server.model.skill.L1SkillId.ADDITIONAL_FIRE;
import static com.lineage.server.model.skill.L1SkillId.ADVANCE_SPIRIT;
import static com.lineage.server.model.skill.L1SkillId.AQUA_PROTECTER;
import static com.lineage.server.model.skill.L1SkillId.BERSERKERS;
import static com.lineage.server.model.skill.L1SkillId.BLESS_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.BOUNCE_ATTACK;
import static com.lineage.server.model.skill.L1SkillId.BRAVE_MENTAL;
import static com.lineage.server.model.skill.L1SkillId.BURNING_SLASH;
import static com.lineage.server.model.skill.L1SkillId.BURNING_SPIRIT;
import static com.lineage.server.model.skill.L1SkillId.BURNING_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.CLEAR_MIND;
import static com.lineage.server.model.skill.L1SkillId.DOUBLE_BRAKE;
import static com.lineage.server.model.skill.L1SkillId.DRAGON_SKIN;
import static com.lineage.server.model.skill.L1SkillId.DRESS_EVASION;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_FIRE;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_PROTECTION;
import static com.lineage.server.model.skill.L1SkillId.EXOTIC_VITALIZE;
import static com.lineage.server.model.skill.L1SkillId.GLOWING_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static com.lineage.server.model.skill.L1SkillId.IRON_SKIN;
import static com.lineage.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static com.lineage.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;
import static com.lineage.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static com.lineage.server.model.skill.L1SkillId.RESIST_MAGIC;
import static com.lineage.server.model.skill.L1SkillId.SOLID_CARRIAGE;
import static com.lineage.server.model.skill.L1SkillId.SOUL_OF_FLAME;
import static com.lineage.server.model.skill.L1SkillId.UNCANNY_DODGE;
import static com.lineage.server.model.skill.L1SkillId.VENOM_RESIST;
import static com.lineage.server.model.skill.L1SkillId.WATER_LIFE;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_PacketBoxGm;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 全技能狀態(參數:對象(gm,all))
 * 
 * @author dexc
 */
public class L1AllBuff implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1AllBuff.class);

	private static final int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON,
			BERSERKERS, IMMUNE_TO_HARM, //
			ADVANCE_SPIRIT, //

			REDUCTION_ARMOR, BOUNCE_ATTACK, //
			SOLID_CARRIAGE, BURNING_SPIRIT,

			VENOM_RESIST, DOUBLE_BRAKE, UNCANNY_DODGE, DRESS_EVASION, //
			GLOWING_WEAPON, BRAVE_MENTAL,

			RESIST_MAGIC, CLEAR_MIND, ELEMENTAL_PROTECTION, AQUA_PROTECTER, BURNING_WEAPON, IRON_SKIN,
			EXOTIC_VITALIZE, WATER_LIFE, ELEMENTAL_FIRE, SOUL_OF_FLAME, ADDITIONAL_FIRE,

			DRAGON_SKIN, BURNING_SLASH };

	private L1AllBuff() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AllBuff();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer st = new StringTokenizer(arg);
			final String name = st.nextToken();
			// 全玩家
			if (name.equalsIgnoreCase("all")) {
				final AllBuffRunnable allBuffRunnable = new AllBuffRunnable();
				GeneralThreadPool.get().execute(allBuffRunnable);
				return;
			}
			// GM 本身
			if (name.equalsIgnoreCase("gm")) {
				startGm(pc);
				return;
			}
			// 指定人物
			final L1PcInstance target = World.get().getPlayer(name);
			if (target == null) {
				// 73 \f1%0%d 不在線上。
				pc.sendPackets(new S_ServerMessage(73, name));
				return;
			}

			startPc(target);

		} catch (final Exception e) {
			final int mode = 4;
			pc.sendPackets(new S_PacketBoxGm(pc, mode));
		}
	}

	public static void startPc(final L1PcInstance target) {
		L1BuffUtil.haste(target, 3600 * 1000);
		L1BuffUtil.brave(target, 3600 * 1000);

		final L1ItemInstance weapon = target.getWeapon();
		if (weapon != null) {
			final int weaponType = weapon.getItem().getType();
			int polyid = -1;

			switch (weaponType) {
			case 1:// 劍
			case 2:// 匕首
			case 3:// 雙手劍
			case 6:// 斧(單手)
			case 15:// 斧(雙手)
				polyid = 6276;// 白金騎士
				break;

			case 11:// 鋼爪
			case 12:// 雙刀
				polyid = 6282;// 白金刺客
				break;

			case 16:// 魔杖(雙手)
			case 7:// 魔杖(單手)
			case 17:// 奇古獸
				polyid = 6277;// 白金法師
				break;

			case 4:// 弓(雙手)
			case 13:// 弓(單手)
			case 10:// 鐵手甲
				polyid = 6278;// 白金巡守
				break;

			case 18:// 鎖鏈劍
			case 14:// 矛(單手)
			case 5:// 矛(雙手)
				polyid = 7341;// 狂暴將軍
				break;
			}
			if (polyid != -1) {
				L1PolyMorph.doPoly(target, polyid, 7200, L1PolyMorph.MORPH_BY_ITEMMAGIC);
			}
		}

		for (int i = 0; i < allBuffSkill.length; i++) {
			final L1Skills skill = SkillsTable.get().getTemplate(allBuffSkill[i]);
			final L1SkillUse skillUse = new L1SkillUse();
			skillUse.handleCommands(target, allBuffSkill[i], target.getId(), target.getX(), target.getY(),
					skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);// */
		}
	}

	public static void startGm(final L1PcInstance target) {
		L1BuffUtil.haste(target, 3600 * 1000);

		L1BuffUtil.brave(target, 3600 * 1000);

		L1PolyMorph.doPoly(target, 5641, 7200, L1PolyMorph.MORPH_BY_GM);

		for (int i = 0; i < allBuffSkill.length; i++) {
			final L1Skills skill = SkillsTable.get().getTemplate(allBuffSkill[i]);
			new L1SkillUse().handleCommands(target, allBuffSkill[i], target.getId(), target.getX(),
					target.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
		}
	}

	/**
	 * 執行多數物件技能狀態給予
	 * 
	 * @author daien
	 */
	private class AllBuffRunnable implements Runnable {

		@Override
		public void run() {
			try {
				// int i = 0;
				for (final L1PcInstance tgpc : World.get().getAllPlayers()) {
					startPc(tgpc);
					Thread.sleep(1);
					// System.out.println(i++);
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}

		}
	}
}
