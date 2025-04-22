/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1WeaponSkill;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_UseAttackSkill;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;

/**
 * 武器魔法DIY系統(DB自製)
 * 
 * @author terry0412
 */
public class L1MagicWeapon {

	private static final Random _random = new Random();

	private final int _item_id; // 魔法石道具編號

	private final String _skill_name; // 會顯示在武器上的魔法說明

	private final int _success_random; // 強化成功機率

	private final int _max_use_time; // 附加在武器上的魔法時間 (單位:秒)

	private final String _success_msg; // 加持成功 對話欄顯示

	private final String _failure_msg; // 加持失敗 對話欄顯示

	private final int _probability; // 效果發動機率

	private final boolean _isLongRange; // 是否對遠距武器(弓or鐵手甲) 發動率除以二

	private final int _fixDamage; // 固定傷害

	private final int _randomDamage; // 隨機傷害

	private final double _doubleDmgValue; // 暴擊倍率

	private final int _gfxId; // 發動特效編號

	private final boolean _gfxIdTarget; // 目標對象 (0 = 自身, 1 = 對方)

	private final List<int[]> _gfxIdOtherLoc; // 額外發動特效座標 (請輸入座標格式)

	private final int _area; // 幾格範圍傷害

	private final boolean _arrowType; // 飛行效果 (0 = 關閉, 1 = 開啟)

	private final int _effectId; // 額外技能skill編號

	private final int _effectTime; // 額外技能持續效果時間 (單位:秒)

	private final int _attr; // 傷害附加屬性 (0:無, 1:地, 2:火, 4:水, 8:風, 16:光)

	private final int _hpAbsorb; // 吸血量

	private final int _mpAbsorb; // 吸魔量

	private final int _steps;
	
	public L1MagicWeapon(final int item_id, final String skill_name, final int success_random,
			final int max_use_time, final String success_msg, final String failure_msg, final int probability,
			final boolean isLongRange, final int fixDamage, final int randomDamage,
			final double doubleDmgValue, final int gfxId, final boolean gfxIdTarget,
			final List<int[]> gfxIdOtherLoc, final int area, final boolean arrowType, final int effectId,
			final int effectTime, final int attr, final int hpAbsorb, final int mpAbsorb, final int steps) {
		_item_id = item_id;
		_skill_name = skill_name;
		_success_random = success_random;
		_max_use_time = max_use_time;
		_success_msg = success_msg;
		_failure_msg = failure_msg;
		_probability = probability;
		_isLongRange = isLongRange;
		_fixDamage = fixDamage;
		_randomDamage = randomDamage;
		_doubleDmgValue = doubleDmgValue;
		_gfxId = gfxId;
		_gfxIdTarget = gfxIdTarget;
		_gfxIdOtherLoc = gfxIdOtherLoc;
		_area = area;
		_arrowType = arrowType;
		_effectId = effectId;
		_effectTime = effectTime;
		_attr = attr;
		_hpAbsorb = hpAbsorb;
		_mpAbsorb = mpAbsorb;
		_steps = steps;
	}
	
	public final int get_steps() {
		return _steps;
	}
	
	public final int getItemId() {
		return _item_id;
	}

	public final String getSkillName() {
		return _skill_name;
	}

	public final int getSuccessRandom() {
		return _success_random;
	}

	public final int getMaxUseTime() {
		return _max_use_time;
	}

	public final String getSuccessMsg() {
		return _success_msg;
	}

	public final String getFailureMsg() {
		return _failure_msg;
	}

	/**
	 * 附魔武器傷害判定
	 * 
	 * @param pc
	 * @param cha
	 * @param damage
	 * @param magicWeapon
	 * @return
	 */
	public static final double getWeaponSkillDamage(final L1PcInstance pc, final L1Character cha,
			final double damage, final L1MagicWeapon magicWeapon, final boolean isLongRange) {
		if ((pc == null) || (cha == null) || (magicWeapon == null)) {
			return 0;
		}

		// 隨機機率
		final int chance;

		// 是否對遠距武器(弓or鐵手甲) 發動率除以二
		if (isLongRange && magicWeapon._isLongRange) {
			chance = _random.nextInt(2000);

		} else {
			chance = _random.nextInt(1000);
		}

		// 發動機率 (近距)
		if (magicWeapon._probability < chance) {
			return 0;
		}

		// 戰爭期間 by terry0412
		final boolean isNowWar = ServerWarExecutor.get().isNowWar();

		// 魔法特效編號
		final int gfxId = magicWeapon._gfxId;
		if (gfxId != 0) {
			int locX;
			int locY;
			int targetId;
			// 顯示特效於目標對象
			if (magicWeapon._gfxIdTarget) {
				locX = cha.getX();
				locY = cha.getY();
				targetId = cha.getId();

			} else {
				locX = pc.getX();
				locY = pc.getY();
				targetId = pc.getId();
			}

			// 發送特效
			sendGfxids(pc, magicWeapon, locX, // X座標
					locY, // Y座標
					targetId, gfxId, isNowWar);

			// 額外發動特效座標 (請輸入座標格式)
			if ((magicWeapon._gfxIdOtherLoc != null) && !magicWeapon._gfxIdOtherLoc.isEmpty()) {
				for (final int[] location : magicWeapon._gfxIdOtherLoc) {
					// 發送特效
					sendGfxids(pc, magicWeapon, locX + location[0], // X座標
							locY + location[1], // Y座標
							targetId, gfxId, isNowWar);
				}
			}
		}

		// 負面效果判斷
		int effectTime = magicWeapon._effectTime;
		if (effectTime > 0) {
			effectTime = effectTime * 1000;
		}

		// 修正為 直接對應技能編號 by terry0412
		final int effectId = magicWeapon._effectId;
		if (effectId > 0) {
			final L1Character target;
			if (magicWeapon._gfxIdTarget) {
				target = cha;
			} else {
				target = pc;
			}

			final L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(pc, magicWeapon._effectId, target.getId(), target.getX(), target.getY(),
					magicWeapon._effectTime, L1SkillUse.TYPE_GMBUFF);
		}

		// 固定傷害 + 隨機傷害
		double fixDamage = magicWeapon._fixDamage;
		final int randomDamage = magicWeapon._randomDamage;
		if (randomDamage > 0) {
			fixDamage += _random.nextInt(randomDamage);
		}

		// 暴擊倍率 (只計算武器的物理總傷害)
		final double doubleDmgValue = magicWeapon._doubleDmgValue;
		if (doubleDmgValue > 0) {
			fixDamage += damage * doubleDmgValue;
		}

		// 吸取體力
		if (magicWeapon._hpAbsorb > 0) {
			// 不造成額外吸血傷害
			pc.setCurrentHp(pc.getCurrentHp() + magicWeapon._hpAbsorb);
		}

		// 吸取魔力
		if (magicWeapon._mpAbsorb > 0) {
			if (cha.getCurrentMp() > 0) {
				cha.setCurrentMp(Math.max(cha.getCurrentMp() - magicWeapon._mpAbsorb, 0));
				pc.setCurrentMp(pc.getCurrentMp() + magicWeapon._mpAbsorb);
			}
		}

		// 幾格範圍傷害 (範圍傷害 = 總傷害的50%)
		final int area = magicWeapon._area;
		if (area != 0) {
			for (final L1Object object : World.get().getVisibleObjects(cha, area)) {
				if (object == null) {
					continue;
				}
				if (!(object instanceof L1Character)) {
					continue;
				}
				if (object.getId() == pc.getId()) {
					continue;
				}
				// 攻撃対象はL1Attackで処理するため除外
				if (object.getId() == cha.getId()) {
					continue;
				}

				// 攻撃対象がMOBの場合は、範囲内のMOBにのみ当たる
				// 攻撃対象がPC,Summon,Petの場合は、範囲内のPC,Summon,Pet,MOBに当たる
				if (cha instanceof L1MonsterInstance) {
					if (!(object instanceof L1MonsterInstance)) {
						continue;
					}
				}
				if ((cha instanceof L1PcInstance) || (cha instanceof L1SummonInstance)
						|| (cha instanceof L1PetInstance)) {
					if (!((object instanceof L1PcInstance) || (object instanceof L1SummonInstance)
							|| (object instanceof L1PetInstance) || (object instanceof L1MonsterInstance))) {
						continue;
					}
				}

				final double fixDamageArea = L1WeaponSkill.calcDamageReduction(pc, (L1Character) object,
						(fixDamage * 0.5), magicWeapon._attr);
				if (fixDamageArea <= 0) {
					continue;
				}
				if (object instanceof L1PcInstance) {
					final L1PcInstance targetPc = (L1PcInstance) object;
					// 受傷動作
					targetPc.sendPacketsX8(new S_DoActionGFX(targetPc.getId(), ActionCodes.ACTION_Damage));
					targetPc.receiveDamage(pc, (int) fixDamageArea, false, false);

				} else if ((object instanceof L1SummonInstance) || (object instanceof L1PetInstance)
						|| (object instanceof L1MonsterInstance)) {
					final L1NpcInstance targetNpc = (L1NpcInstance) object;
					// 受傷動作
					targetNpc.broadcastPacketX8(
							new S_DoActionGFX(targetNpc.getId(), ActionCodes.ACTION_Damage));
					targetNpc.receiveDamage(pc, (int) fixDamageArea);
				}
			}
		}

		// 屬性減免傷害判定
		return L1WeaponSkill.calcDamageReduction(pc, cha, fixDamage, magicWeapon._attr);
	}

	/**
	 * 發送特效到指定座標
	 * 
	 * @param pc
	 * @param magicWeapon
	 * @param locX
	 * @param locY
	 * @param targetId
	 * @param gfxId
	 * @param isNowWar
	 */
	private static final void sendGfxids(final L1PcInstance pc, final L1MagicWeapon magicWeapon,
			final int locX, final int locY, final int targetId, final int gfxId, final boolean isNowWar) {
		// 10格內畫面可見人物
		final ArrayList<L1PcInstance> pc_list = World.get().getVisiblePlayer(pc, 10);

		// 飛行效果
		if (magicWeapon._arrowType) {
			final S_UseAttackSkill packet = new S_UseAttackSkill(pc, targetId, gfxId, locX, locY,
					ActionCodes.ACTION_Attack, false);
			pc.sendPackets(packet);

			// 非戰爭期間 by terry0412
			if (!isNowWar) {
				for (final L1PcInstance otherPc : pc_list) {
					otherPc.sendPackets(packet);
				}
			}

		} else {
			final S_EffectLocation packet = new S_EffectLocation(locX, locY, gfxId);
			pc.sendPackets(packet);

			// 非戰爭期間 by terry0412
			if (!isNowWar) {
				for (final L1PcInstance otherPc : pc_list) {
					otherPc.sendPackets(packet);
				}
			}
		}
	}
}
