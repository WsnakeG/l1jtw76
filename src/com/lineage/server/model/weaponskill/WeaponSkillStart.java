package com.lineage.server.model.weaponskill;

import static com.lineage.server.model.skill.L1SkillId.COUNTER_MAGIC;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.WeaponSkillPowerTable;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 發動武器技能
 * 
 * @author daien 2014/07/24 by Roy修正魔法屏障物理化也會解除的BUG
 */
public class WeaponSkillStart {

	private static final Log _log = LogFactory.getLog(WeaponSkillStart.class);

	private static final Random _random = new Random();

	public static double start_weapon_skill(final L1PcInstance pc, final L1Character target,
			final L1ItemInstance weapon, final double srcdmg) {
		try {
			if (weapon == null) {
				return 0;
			}

			// 魔法屏障作用中

			final ArrayList<L1WeaponSkillType> list = WeaponSkillPowerTable.get()
					.getTemplate(weapon.getItemId());
			if (list != null) {
				final L1WeaponSkillType tmp = list.get(_random.nextInt(list.size()));
				if (tmp != null) {
					if (tmp.get_boss_holdout()) {// 技能對BOSS無效
						if (target instanceof L1NpcInstance) {
							final L1NpcInstance npc = (L1NpcInstance) target;
							if (npc.getNpcTemplate().is_boss()) {
								return 0;
							}
						}
					}
					final double dmg = tmp.start_weapon_skill(pc, target, weapon, srcdmg);
					if (target.hasSkillEffect(COUNTER_MAGIC)) {
						target.removeSkillEffect(COUNTER_MAGIC);// 移除技能
						final int castgfx = SkillsTable.get().getTemplate(COUNTER_MAGIC).getCastGfx2();
						target.broadcastPacketX8(new S_SkillSound(target.getId(), castgfx));
						if (target instanceof L1PcInstance) {
							final L1PcInstance tgpc = (L1PcInstance) target;
							tgpc.sendPacketsX8(new S_SkillSound(tgpc.getId(), castgfx));
						}
						return 0;
					}
					return dmg;
				}
			}
			return 0;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}
}