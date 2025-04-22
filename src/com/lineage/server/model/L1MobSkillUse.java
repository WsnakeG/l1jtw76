package com.lineage.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.MobSkillTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1MobSkill;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.world.World;

/**
 * 怪物施放技能
 * 
 * @author terry0412
 */
public final class L1MobSkillUse {

	private L1MobSkill _mobSkillTemplate;

	private L1NpcInstance _attacker;

	private L1Character _target;

	private static final Random _rnd = new Random();

	private int _sleepTime;

	private int[] _skillUseCount;

	/**
	 * 怪物技能
	 * 
	 * @param npc
	 * @param mobSkill
	 */
	public L1MobSkillUse(final L1NpcInstance npc) {
		try {
			_mobSkillTemplate = MobSkillTable.getInstance().getTemplate(npc.getNpcTemplate().get_npcId());
			if (_mobSkillTemplate == null) {
				return;
			}
			_attacker = npc;
			_skillUseCount = new int[_mobSkillTemplate.getSkillSize()];

		} catch (final Exception e) {
		}
	}

	private final int getSkillUseCount(final int idx) {
		return _skillUseCount[idx];
	}

	private final void skillUseCountUp(final int idx) {
		_skillUseCount[idx]++;
	}

	/**
	 * 技能使用次數歸0 (repaired by terry0412)
	 */
	public final void resetAllSkillUseCount() {
		if (_mobSkillTemplate == null) {
			return;
		}
		for (int i = 0, n = _mobSkillTemplate.getSkillSize(); i < n; i++) {
			_skillUseCount[i] = 0; // 使用次數歸0
		}
	}

	/**
	 * 清除連結的暫存對象 (repaired by terry0412)
	 */
	public final void deleteAllTemplates() {
		if (_mobSkillTemplate == null) {
			return;
		}
		_mobSkillTemplate = null;
		_attacker = null;
		_target = null;
		_skillUseCount = null;
	}

	public final int getSleepTime() {
		return _sleepTime;
	}

	/**
	 * トリガーの条件のみチェック。 (repaired by terry0412)
	 * 
	 * @param tg
	 * @return
	 */
	public final boolean isSkillTrigger(final L1Character tg) {
		if (_mobSkillTemplate == null) {
			return false;
		}
		// _target = tg; // 註解掉 by terry0412

		final int type = _mobSkillTemplate.getType(0);
		if (type == L1MobSkill.TYPE_NONE) {
			return false;
		}

		int i = 0;
		for (i = 0; (i < _mobSkillTemplate.getSkillSize())
				&& (_mobSkillTemplate.getType(i) != L1MobSkill.TYPE_NONE); i++) {
			if (isSkillUseble(tg, i, false)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * スキル攻撃 スキル攻撃可能ならばtrueを返す。 攻撃できなければfalseを返す。 (repaired by terry0412)
	 * 
	 * @param tg
	 * @param isTriRnd
	 * @return
	 */
	public final boolean skillUse(final L1Character tg, final boolean isTriRnd) {
		if (_mobSkillTemplate == null) {
			return false;
		}
		// _target = tg; // 註解掉 by terry0412

		final int type = _mobSkillTemplate.getType(0);
		if (type == L1MobSkill.TYPE_NONE) {
			return false;
		}

		int[] skills = null;
		int skillSizeCounter = 0;

		final int skillSize = _mobSkillTemplate.getSkillSize();
		if (skillSize >= 0) {
			skills = new int[skillSize];
		}

		int i = 0;
		for (i = 0; (i < _mobSkillTemplate.getSkillSize())
				&& (_mobSkillTemplate.getType(i) != L1MobSkill.TYPE_NONE); i++) {
			if (isSkillUseble(tg, i, isTriRnd) == false) {
				continue;

			} else { // 条件にあうスキルが存在する
				skills[skillSizeCounter] = i;
				skillSizeCounter++;
			}
		}

		if (skillSizeCounter != 0) {
			final int actNo = skills[_rnd.nextInt(skillSizeCounter)];

			// 修復 怪物技能會重複搜尋對象 的問題 by terry0412
			final int changeType = _mobSkillTemplate.getChangeTarget(actNo);
			if (changeType > 0) {
				// changeTargetが設定されている場合、ターゲットの入れ替え
				_target = changeTarget(tg, changeType, i);

			} else {
				// 設定されてない場合は本来のターゲットにする
				_target = tg;
			}

			// スキル使用
			if (useSkill(actNo)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 怪物施放技能 (repaired by terry0412)
	 * 
	 * @param i
	 * @return
	 */
	private final boolean useSkill(final int i) {
		return useSkill(i, 0);
	}

	/**
	 * 怪物施放技能 (repaired by terry0412)
	 * 
	 * @param i
	 * @param extraLeverage
	 * @return
	 */
	private final boolean useSkill(final int i, final int extraLeverage) {
		boolean isUseSkill = false;
		final int type = _mobSkillTemplate.getType(i);
		switch (type) {
		case L1MobSkill.TYPE_PHYSICAL_ATTACK: // 物理攻撃
			if (physicalAttack(i, extraLeverage) == true) {
				skillUseCountUp(i);
				isUseSkill = true;
			}
			break;

		case L1MobSkill.TYPE_MAGIC_ATTACK: // 魔法攻撃
			if (magicAttack(i, extraLeverage) == true) {
				skillUseCountUp(i);
				isUseSkill = true;
			}
			break;

		case L1MobSkill.TYPE_SUMMON: // サモンする
			if (summon(i) == true) {
				skillUseCountUp(i);
				isUseSkill = true;
			}
			break;

		case L1MobSkill.TYPE_POLY: // 強制変身させる
			if (poly(i) == true) {
				skillUseCountUp(i);
				isUseSkill = true;
			}
			break;

		case L1MobSkill.AREA_SKILLS: // 群體技能 (changed by terry0412)
			// System.out.println("群體技能");
			if (area_skills(i, extraLeverage) == true) {
				skillUseCountUp(i);
				isUseSkill = true;
			}
			break;

		}
		return isUseSkill;
	}

	/**
	 * 群體技能 (changed by terry0412)
	 * 
	 * @param idx
	 * @param extraLeverage
	 * @return
	 */
	private final boolean area_skills(final int idx, final int extraLeverage) {
		final int skillid = _mobSkillTemplate.getSkillId(idx);
		final L1Skills skill = SkillsTable.get().getTemplate(skillid);

		final int areaWidth = _mobSkillTemplate.getAreaWidth(idx);
		final int areaHeight = _mobSkillTemplate.getAreaHeight(idx);

		final List<L1Object> objects;
		// 指定範圍物件
		if ((areaWidth > 0) || (areaHeight > 0)) {
			objects = World.get().getVisibleBoxObjects(_attacker, _attacker.getHeading(), areaWidth,
					areaHeight);

			// 全範圍物件
		} else {
			objects = World.get().getVisibleObjects(_attacker);
		}

		// 範圍目標搜尋
		final List<L1Character> targetList = new ArrayList<L1Character>();
		for (final L1Object obj : objects) {
			if ((obj instanceof L1PcInstance) || (obj instanceof L1PetInstance)
					|| (obj instanceof L1SummonInstance)) {
				final L1Character cha = (L1Character) obj;

				final int distance = _attacker.getLocation().getTileLineDistance(cha.getLocation());

				// 発動範囲外のキャラクターは対象外
				if (!_mobSkillTemplate.isTriggerDistance(idx, distance)) {
					continue;
				}

				// 障害物がある場合は対象外
				if (!_attacker.glanceCheck(cha.getX(), cha.getY())) {
					continue;
				}

				// ヘイトがない場合対象外
				if (!_attacker.getHateList().containsKey(cha)) {
					continue;
				}

				// 死んでるキャラクターは対象外
				if ((cha.getCurrentHp() <= 0) || cha.isDead()) {
					continue;
				}

				// 玩家對象檢查
				if (cha instanceof L1PcInstance) {
					final L1PcInstance pc = (L1PcInstance) cha;
					// added by terry0412
					if (pc.isGhost() || pc.isGmInvis()) {
						continue;
					}
				}
				targetList.add((L1Character) obj);
			}
		}

		final L1SkillUse skillUse = new L1SkillUse();

		// 技能傷害比例 (extra added by terry0412)
		final int leverage = _mobSkillTemplate.getLeverage(idx);
		if ((leverage > 0) || (extraLeverage > 0)) {
			skillUse.setLeverage(leverage + extraLeverage);
		}

		// 技能持續時間 (秒) by terry0412
		// final int timeSec = _mobSkillTemplate.getTimeSec(idx);

		// 施放技能處理
		for (final L1Character cha : targetList) {
			skillUse.handleCommands(null, skillid, cha.getId(), cha.getX(), cha.getX(), 0,
					L1SkillUse.TYPE_GMBUFF, _attacker);
		}

		// 施法動作
		// final int actId = _mobSkillTemplate.getActid(idx);
		// final S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(),
		// actId);
		// _attacker.broadcastPacketX10(gfx);

		// 使用スキルによるsleepTimeの設定
		if (skill.getTarget().equals("attack") && (skillid != 18)) { // 有方向魔法
			_sleepTime = _attacker.getNpcTemplate().getAtkMagicSpeed();

		} else { // 無方向魔法
			_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
		}
		return true;
	}

	/**
	 * 召喚怪物 (repaired by terry0412)
	 * 
	 * @param idx
	 * @return
	 */
	private final boolean summon(final int idx) {
		final int summonId = _mobSkillTemplate.getSummon(idx);
		if (summonId <= 0) {
			return false;
		}

		final int min = _mobSkillTemplate.getSummonMin(idx);
		final int max = _mobSkillTemplate.getSummonMax(idx);
		final int count = _rnd.nextInt(max) + min;

		final L1MobSkillUseSpawn skillUseSpawn = new L1MobSkillUseSpawn();
		skillUseSpawn.mobspawn(_attacker, _target, summonId, count);

		// 魔法を使う動作のエフェクト
		final S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), ActionCodes.ACTION_SkillBuff);

		// 魔方陣の表示
		// _attacker.broadcastPacketX8(new S_SkillSound(_attacker.getId(),
		// 761));

		if (summonId == 97350) {
			_attacker.hideOnGround();
		}

		_attacker.broadcastPacketX10(gfx);
		_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();

		return true;
	}

	/**
	 * 15セル以内で射線が通るPCを指定したモンスターに強制変身させる。 対PCしか使えない。
	 */
	private final boolean poly(final int idx) {
		final int polyId = _mobSkillTemplate.getPolyId(idx);
		if (polyId <= 0) {
			return false;
		}

		boolean usePoly = false;

		for (final L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
			// 部分狀態不判斷 by terry0412
			if ((pc.getCurrentHp() <= 0) || pc.isDead() || pc.isGhost() || pc.isInvisble()
					|| pc.isGmInvis()) {
				continue;
			}

			if (_attacker.glanceCheck(pc.getX(), pc.getY()) == false) {
				continue; // 射線が通らない
			}

			final int npcId = _attacker.getNpcTemplate().get_npcId();
			switch (npcId) {
			case 81082: // ヤヒの場合
				pc.getInventory().takeoffEquip(945); // 牛のpolyIdで装備を全部外す。
				break;

			default:
				break;
			}
			L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_NPC);

			usePoly = true;
		}

		if (usePoly) {
			// 変身させた場合、オレンジの柱を表示する。
			for (final L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), 230));
				break;
			}

			// 魔法を使う動作のエフェクト
			final S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), ActionCodes.ACTION_SkillBuff);
			_attacker.broadcastPacketX10(gfx);

			_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
		}
		return usePoly;
	}

	/**
	 * 魔法攻擊 (repaired by terry0412)
	 * 
	 * @param idx
	 * @param extraLeverage
	 * @return
	 */
	private final boolean magicAttack(final int idx, final int extraLeverage) {
		final L1SkillUse skillUse = new L1SkillUse();
		final int skillid = _mobSkillTemplate.getSkillId(idx);
		boolean canUseSkill = false;

		if (skillid > 0) {
			canUseSkill = skillUse.checkUseSkill(null, skillid, _target.getId(), _target.getX(),
					_target.getY(), 0, L1SkillUse.TYPE_NORMAL, _attacker);
		}

		if (canUseSkill == true) {
			// 技能傷害比例 (extra added by terry0412)
			final int leverage = _mobSkillTemplate.getLeverage(idx);
			if ((leverage > 0) || (extraLeverage > 0)) {
				skillUse.setLeverage(leverage + extraLeverage);
			}

			skillUse.handleCommands(null, skillid, _target.getId(), _target.getX(), _target.getX(), 0,
					L1SkillUse.TYPE_NORMAL, _attacker);

			// 使用スキルによるsleepTimeの設定
			final L1Skills skill = SkillsTable.get().getTemplate(skillid);
			if (skill.getTarget().equals("attack") && (skillid != 18)) { // 有方向魔法
				_sleepTime = _attacker.getNpcTemplate().getAtkMagicSpeed();

			} else { // 無方向魔法
				_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
			}
			return true;
		}
		return false;
	}

	/**
	 * 物理攻撃 (repaired by terry0412)
	 * 
	 * @param idx
	 * @param extraLeverage
	 * @return
	 */
	private final boolean physicalAttack(final int idx, final int extraLeverage) {
		final Map<Integer, Integer> targetList = new HashMap<Integer, Integer>();
		final int areaWidth = _mobSkillTemplate.getAreaWidth(idx);
		final int areaHeight = _mobSkillTemplate.getAreaHeight(idx);
		final int range = _mobSkillTemplate.getRange(idx);
		final int actId = _mobSkillTemplate.getActid(idx);
		final int gfxId = _mobSkillTemplate.getGfxid(idx);

		// レンジ外
		if (_attacker.getLocation().getTileLineDistance(_target.getLocation()) > range) {
			return false;
		}

		// 障害物がある場合攻撃不可能
		if (!_attacker.glanceCheck(_target.getX(), _target.getY())) {
			return false;
		}

		// 向きのセット
		_attacker.setHeading(_attacker.targetDirection(_target.getX(), _target.getY()));

		if (areaHeight > 0) {
			// 範囲攻撃
			final ArrayList<L1Object> objs = World.get().getVisibleBoxObjects(_attacker,
					_attacker.getHeading(), areaWidth, areaHeight);

			for (final L1Object obj : objs) {
				// ターゲットがキャラクター以外の場合何もしない。
				if (!(obj instanceof L1Character)) {
					continue;
				}

				final L1Character cha = (L1Character) obj;
				// 死んでるキャラクターは対象外
				if (cha.isDead()) {
					continue;
				}

				// ゴースト状態は対象外
				if (cha instanceof L1PcInstance) {
					if (((L1PcInstance) cha).isGhost()) {
						continue;
					}
				}

				// 障害物がある場合は対象外
				if (!_attacker.glanceCheck(cha.getX(), cha.getY())) {
					continue;
				}

				if ((_target instanceof L1PcInstance) || (_target instanceof L1SummonInstance)
						|| (_target instanceof L1PetInstance)) {
					// 対PC
					if (((obj instanceof L1PcInstance) && !((L1PcInstance) obj).isGhost()
							&& !((L1PcInstance) obj).isGmInvis()) || (obj instanceof L1SummonInstance)
							|| (obj instanceof L1PetInstance)) {
						targetList.put(obj.getId(), 0);
					}

				} else {
					// 対NPC
					if (obj instanceof L1MonsterInstance) {
						targetList.put(obj.getId(), 0);
					}
				}
			}

		} else {
			// 単体攻撃
			targetList.put(_target.getId(), 0); // ターゲットのみ追加
		}

		if (targetList.size() <= 0) {
			return false;
		}

		final Iterator<Integer> ite = targetList.keySet().iterator();
		while (ite.hasNext()) {
			final int targetId = ite.next();
			final L1Object object = World.get().findObject(targetId);

			final L1AttackMode attack = new L1AttackNpc(_attacker, (L1Character) object);

			if (attack.calcHit()) {
				// 技能傷害比例 (extra added by terry0412)
				final int leverage = _mobSkillTemplate.getLeverage(idx);
				if ((leverage > 0) || (extraLeverage > 0)) {
					attack.setLeverage(leverage + extraLeverage);
				}
				attack.calcDamage();
			}

			if (actId > 0) {
				attack.setActId(actId);
			}

			// 攻撃モーションは実際のターゲットに対してのみ行う
			if (targetId == _target.getId()) {
				if (gfxId > 0) {
					_attacker.broadcastPacketX8(new S_SkillSound(_attacker.getId(), gfxId));
				}
				attack.action();
			}
			attack.commit();
		}
		_sleepTime = _attacker.getAtkspeed();
		return true;
	}

	/**
	 * トリガーの条件のみチェック
	 * 
	 * @param tg
	 * @param skillIdx
	 * @param isTriRnd
	 * @return
	 */
	private final boolean isSkillUseble(final L1Character tg, final int skillIdx, final boolean isTriRnd) {
		boolean useble = false;
		final int type = _mobSkillTemplate.getType(skillIdx);

		if (isTriRnd || (type == L1MobSkill.TYPE_SUMMON) || (type == L1MobSkill.TYPE_POLY)) {
			if (_mobSkillTemplate.getTriggerRandom(skillIdx) > 0) {
				final int chance = _rnd.nextInt(100) + 1;
				if (chance < _mobSkillTemplate.getTriggerRandom(skillIdx)) {
					useble = true;

				} else {
					return false;
				}
			}
		}

		if (_mobSkillTemplate.getTriggerHp(skillIdx) > 0) {
			final int hpRatio = (_attacker.getCurrentHp() * 100) / _attacker.getMaxHp();
			if (hpRatio <= _mobSkillTemplate.getTriggerHp(skillIdx)) {
				useble = true;

			} else {
				return false;
			}
		}

		if (_mobSkillTemplate.getTriggerCompanionHp(skillIdx) > 0) {
			final L1NpcInstance companionNpc = searchMinCompanionHp();
			if (companionNpc == null) {
				return false;
			}

			final int hpRatio = (companionNpc.getCurrentHp() * 100) / companionNpc.getMaxHp();
			if (hpRatio <= _mobSkillTemplate.getTriggerCompanionHp(skillIdx)) {
				useble = true;
				_target = companionNpc; // ターゲットの入れ替え

			} else {
				return false;
			}
		}

		if (_mobSkillTemplate.getTriggerRange(skillIdx) != 0) {
			final int distance = _attacker.getLocation().getTileLineDistance(tg.getLocation());

			if (_mobSkillTemplate.isTriggerDistance(skillIdx, distance)) {
				useble = true;

			} else {
				return false;
			}
		}

		if (_mobSkillTemplate.getTriggerCount(skillIdx) > 0) {
			if (getSkillUseCount(skillIdx) < _mobSkillTemplate.getTriggerCount(skillIdx)) {
				useble = true;

			} else {
				return false;
			}
		}
		return useble;
	}

	private final L1NpcInstance searchMinCompanionHp() {
		L1NpcInstance npc;
		L1NpcInstance minHpNpc = null;
		int hpRatio = 100;
		int companionHpRatio;
		final int family = _attacker.getNpcTemplate().get_family();

		for (final L1Object object : World.get().getVisibleObjects(_attacker)) {
			if (object instanceof L1NpcInstance) {
				npc = (L1NpcInstance) object;

				if (npc.getNpcTemplate().get_family() == family) {
					companionHpRatio = (npc.getCurrentHp() * 100) / npc.getMaxHp();
					if (companionHpRatio < hpRatio) {
						hpRatio = companionHpRatio;
						minHpNpc = npc;
					}
				}
			}
		}
		return minHpNpc;
	}

	/**
	 * 現在ChangeTargetで有効な値は2,3のみ (修復 怪物技能會重複搜尋對象 的問題 by terry0412)
	 * 
	 * @param tg
	 * @param type
	 * @param idx
	 * @return
	 */
	private final L1Character changeTarget(final L1Character tg, final int type, final int idx) {
		L1Character target;

		switch (type) {
		case L1MobSkill.CHANGE_TARGET_ME:
			target = _attacker;
			break;

		case L1MobSkill.CHANGE_TARGET_RANDOM:
			// System.out.println("L1MobSkill.CHANGE_TARGET_RANDOM:");
			// ターゲット候補の選定
			final List<L1Character> targetList = new ArrayList<L1Character>();
			for (final L1Object obj : World.get().getVisibleObjects(_attacker)) {
				if ((obj instanceof L1PcInstance) || (obj instanceof L1PetInstance)
						|| (obj instanceof L1SummonInstance)) {
					final L1Character cha = (L1Character) obj;

					final int distance = _attacker.getLocation().getTileLineDistance(cha.getLocation());

					// 発動範囲外のキャラクターは対象外
					if (!_mobSkillTemplate.isTriggerDistance(idx, distance)) {
						continue;
					}

					// 障害物がある場合は対象外
					if (!_attacker.glanceCheck(cha.getX(), cha.getY())) {
						continue;
					}

					// ヘイトがない場合対象外
					if (!_attacker.getHateList().containsKey(cha)) {
						continue;
					}

					// 死んでるキャラクターは対象外
					if ((cha.getCurrentHp() <= 0) || cha.isDead()) {
						continue;
					}

					// 玩家對象檢查
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						// added by terry0412
						if (pc.isGhost() || pc.isGmInvis()) {
							continue;
						}
					}
					targetList.add((L1Character) obj);
				}
			}

			// 找不到其他目標
			if (targetList.size() <= 0) {
				target = tg;

			} else {
				final int randomSize = targetList.size() * 100;
				final int targetIndex = _rnd.nextInt(randomSize) / 100;
				target = targetList.get(targetIndex);
			}
			break;

		default:
			target = tg;
			break;
		}
		return target;
	}

	/**
	 * 對應怪物技能組用 by terry0412
	 * 
	 * @param tg
	 * @param actId
	 * @param extraLeverage
	 * @return
	 */
	public final boolean skillGroupUse(final L1Character tg, final int actId, final int extraLeverage) {
		if (_mobSkillTemplate == null) {
			return false;
		}
		final int type = _mobSkillTemplate.getType(0);
		if (type == L1MobSkill.TYPE_NONE) {
			return false;
		}

		int[] skills = null;
		int skillSizeCounter = 0;

		final int skillSize = _mobSkillTemplate.getSkillSize();
		if (skillSize >= 0) {
			skills = new int[skillSize];
		}

		for (int i = 0; (i < _mobSkillTemplate.getSkillSize())
				&& (_mobSkillTemplate.getType(i) != L1MobSkill.TYPE_NONE); i++) {
			skills[skillSizeCounter] = i;
			skillSizeCounter++;
		}

		if ((skillSizeCounter != 0) && (skillSizeCounter > actId)) {
			// 修復 怪物技能會重複搜尋對象 的問題 by terry0412
			final int changeType = _mobSkillTemplate.getChangeTarget(actId);
			if (changeType > 0) {
				// changeTargetが設定されている場合、ターゲットの入れ替え
				_target = changeTarget(tg, changeType, actId);

			} else {
				// 設定されてない場合は本来のターゲットにする
				_target = tg;
			}

			if (useSkill(skills[actId], extraLeverage)) { // スキル使用
				return true;
			}
		}
		return false;
	}
}
