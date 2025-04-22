package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.CURSE_BLIND;
import static com.lineage.server.model.skill.L1SkillId.MOVE_STOP;
import static com.lineage.server.model.skill.L1SkillId.SILENCE;

import java.util.Iterator;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.server.datatables.ExtraAttrWeaponTable;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.L1AttrWeapon;
import com.lineage.server.world.World;

/**
 * 武器屬性能力系統
 * 
 * @author dexc
 */
public class L1AttackPower {

	private static final Log _log = LogFactory.getLog(L1AttackPower.class);

	private static final Random _random = new Random();
	// 執行PC
	private final L1PcInstance _pc;

	private final L1Character _target;
	// 目標PC
	private L1PcInstance _targetPc;
	// 目標NPC
	private L1NpcInstance _targetNpc;
    // 武器屬性
	private int _weaponAttrEnchantKind;
    // 武器屬性階段
	private int _weaponAttrEnchantLevel;

	public L1AttackPower(final L1PcInstance attacker, final L1Character target, int weaponAttrEnchantKind,
			int weaponAttrEnchantLevel) {
		_pc = attacker;
		_target = target;
		if (_target instanceof L1NpcInstance) {
			_targetNpc = (L1NpcInstance) _target;

		} else if (_target instanceof L1PcInstance) {
			_targetPc = (L1PcInstance) _target;
		}
		_weaponAttrEnchantKind = weaponAttrEnchantKind;
		_weaponAttrEnchantLevel = weaponAttrEnchantLevel;
	}

	/**
	 * 武器屬性能力系統
	 * 
	 * @param damage
	 * @return
	 */
	public int set_item_power(final int damage) {
		int reset_dmg = damage;
		try {
			if (_weaponAttrEnchantKind > 0) {
				// 屬性武器系統(DB自製) by terry0412
				final L1AttrWeapon attrWeapon = ExtraAttrWeaponTable.getInstance().get(_weaponAttrEnchantKind,
						_weaponAttrEnchantLevel);
				if (attrWeapon == null) {
					return damage;
				}

				// 發動機率
				if (attrWeapon.getProbability() <= _random.nextInt(1000)) {
					return damage;
				}

				// 屬性抵抗卷: 購買後帶在身上可抵抗一次發動效果
				if (_targetPc != null && _targetPc.getInventory().consumeItem(ConfigAlt.ResistStone, 1)) { // 原44073
					_targetPc.sendPackets(new S_SystemMessage("\\aE身上持有抵禦石，已幫您成功阻擋！"));
					return damage;
				}

				// 地-能力(束缚) (單位:秒)
				if (attrWeapon.getTypeBind() > 0) {
					// 凍結状態
					if (!L1WeaponSkill.isFreeze(_target)) {
						final int time = (int) (attrWeapon.getTypeBind() * 1000);
						final ServerBasePacket packet = new S_SkillSound(_target.getId(), ConfigAlt.AttackPower1); // 原4184.5261
						_target.broadcastPacketX8(packet);

						if (_targetPc != null) {
							_targetPc.sendPackets(packet);
							_targetPc.setSkillEffect(MOVE_STOP, time);
							_targetPc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));

						} else if (_targetNpc != null) {
							_targetNpc.setSkillEffect(MOVE_STOP, time);
							_targetNpc.setParalyzed(true);
						}
					}
				}

				// 水-能力(吸血) (單位:傷害倍率) & 能力(吸魔) (單位:傷害隨機值)
				if (attrWeapon.getTypeDrainHp() > 0 && attrWeapon.getTypeDrainMp() > 0) {
					final int drainHp = (int) (reset_dmg * attrWeapon.getTypeDrainHp());
					final int drainMp = 1 + _random.nextInt(attrWeapon.getTypeDrainMp());

					final ServerBasePacket packet = new S_SkillSound(_target.getId(), ConfigAlt.AttackPower2); // 原7749.8173
					_target.broadcastPacketX8(packet);

					int newHp = _pc.getCurrentHp() + drainHp;
					if (newHp >= _pc.getMaxHp()) {
						newHp = _pc.getMaxHp();
					}
					_pc.setCurrentHp(newHp);
					_pc.setCurrentMp((short) (_pc.getCurrentMp() + drainMp));
					if (_targetPc != null) {
						_targetPc.sendPackets(packet);
						_targetPc.setCurrentMp(Math.max(_targetPc.getCurrentMp() - drainMp, 0));

					} else if (_targetNpc != null) {
						_targetNpc.setCurrentMp(Math.max(_targetNpc.getCurrentMp() - drainMp, 0));
					}
				}

				// 火-額外傷害倍率
				if (attrWeapon.getTypeDmgup() > 0) {
					final ServerBasePacket packet = new S_SkillSound(_target.getId(), ConfigAlt.AttackPower3); // 原7756
					_target.broadcastPacketX8(packet);

					if (_targetPc != null) {
						_targetPc.sendPackets(packet);
					}
					reset_dmg = (int) (reset_dmg * attrWeapon.getTypeDmgup());
				}

				// 風-範圍傷害 (XX格) (固定輸出XX + 隨機0~80)
				if (attrWeapon.getTypeRange() > 0 && attrWeapon.getTypeRangeDmg() > 0) {
					final ServerBasePacket packet = new S_SkillSound(_target.getId(), ConfigAlt.AttackPower4); // 原7752
					_target.broadcastPacketX8(packet);
					final int dmg = attrWeapon.getTypeRangeDmg() + _random.nextInt(81); // 原51

					if (_targetPc != null) {
						for (L1Object tgobj : World.get().getVisibleObjects(_pc, attrWeapon.getTypeRange())) {
							if (tgobj instanceof L1PcInstance) {
								final L1PcInstance tgpc = (L1PcInstance) tgobj;
								if (tgpc.isDead()) {
									continue;
								}
								// 排除同盟
								if (tgpc.getClanid() == _pc.getClanid()) {
									if (tgpc.getClanid() != 0) {
										continue;
									}
								}
								// 排除安全區
								if (tgpc.getMap().isSafetyZone(tgpc.getLocation())) {
									continue;
								}
								tgpc.receiveDamage(_pc, dmg, false, false); // 物理傷害
							}
						}

					} else if (_targetNpc != null) {
						for (L1Object tgobj : World.get().getVisibleObjects(_pc, attrWeapon.getTypeRange())) {
							if (tgobj instanceof L1MonsterInstance) {
								final L1MonsterInstance tgmob = (L1MonsterInstance) tgobj;
								if (tgmob.isDead()) {
									continue;
								}
								tgmob.receiveDamage(_pc, dmg); // 物理傷害
							}
						}
					}
				}

				// 光裂術傷害 (固定輸出XX + 隨機0~250)
				if (attrWeapon.getTypeLightDmg() > 0) {
					final int dmg = attrWeapon.getTypeLightDmg() + _random.nextInt(251); // 原101

					final ServerBasePacket packet = new S_SkillSound(_target.getId(), ConfigAlt.AttackPower5); // 原1815
					_target.broadcastPacketX8(packet);

					if (_targetPc != null) {
						_targetPc.sendPackets(packet);
						_targetPc.receiveDamage(_pc, dmg, false, false);// 物理傷害

					} else if (_targetNpc != null) {
						_targetNpc.receiveDamage(_pc, dmg);// 物理傷害
					}
				}

				// 冰裂術傷害 (固定輸出XX + 隨機0~250) 150417新增
				if (attrWeapon.getTypeiceDmg() > 0) {
					final int dmg = attrWeapon.getTypeiceDmg() + _random.nextInt(251);

					final ServerBasePacket packet = new S_SkillSound(_target.getId(), ConfigAlt.AttackPower6);// 原763
					_target.broadcastPacketX8(packet);

					if (_targetPc != null) {
						_targetPc.sendPackets(packet);
						_targetPc.receiveDamage(_pc, dmg, false, false);// 物理傷害

					} else if (_targetNpc != null) {
						_targetNpc.receiveDamage(_pc, dmg);// 物理傷害
					}
				}

				// 發動闇盲術 (0:沒效果 1:有效果)
				// 2015/01/14 修正闇盲術圖示效果錯誤 746改為10703
				if (attrWeapon.getTypeSkill1() && attrWeapon.getTypeSkillTime() > 0) {
					final int timeSec = (int) (attrWeapon.getTypeSkillTime());
					// SKILL移轉
					final SkillMode mode = L1SkillMode.get().getSkill(CURSE_BLIND);
					if (mode != null) {
						mode.start(_pc, _target, null, timeSec);
					}
					_target.broadcastPacketX8(new S_SkillSound(_target.getId(), 10703));
					if (_targetPc != null) {
						_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 10703, timeSec));
					}
				}

				// 發動魔法封印 (0:沒效果 1:有效果)
				if (attrWeapon.getTypeSkill2() && attrWeapon.getTypeSkillTime() > 0) {
					final int timeSec = (int) (attrWeapon.getTypeSkillTime());

					if (!_target.hasSkillEffect(SILENCE)) {
						_target.setSkillEffect(SILENCE, timeSec * 1000);
						_target.broadcastPacketX8(new S_SkillSound(_target.getId(), 2177));
						if (_targetPc != null) {
							_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2177, timeSec));
						}
					}
				}

				// 發動變形術 (0:沒效果 1:有效果)
				if (attrWeapon.getTypeSkill3() && attrWeapon.getTypeSkillTime() > 0
				// 有變身列表
						&& attrWeapon.getTypePolyList() != null) {
					// 隨機取得一種變身編號
					final int polyId = Integer.parseInt(attrWeapon.getTypePolyList()[_random
							.nextInt(attrWeapon.getTypePolyList().length)]);
					if (_targetPc != null) {
						// 變形控制戒指
						if (_targetPc.getInventory().checkEquipped(20281)) {
							return reset_dmg;
						}
						_targetPc.sendPacketsX8(new S_SkillSound(_target.getId(), 230));
						// poly
						L1PolyMorph.doPoly(_targetPc, polyId, (int) attrWeapon.getTypeSkillTime(),
								L1PolyMorph.MORPH_BY_ITEMMAGIC);

					} else if (_targetNpc != null) {
						// 不是BOSS召喚表物件
						if (!_targetNpc.getNpcTemplate().is_boss()) {
							_target.broadcastPacketX8(new S_SkillSound(_target.getId(), 230));
							// poly
							L1PolyMorph.doPoly(_target, polyId, (int) attrWeapon.getTypeSkillTime(),
									L1PolyMorph.MORPH_BY_ITEMMAGIC);
						}
					}
				}

				// 發動緩速術 (0:沒效果 1:有效果)
				if (attrWeapon.getTypeSkill4() && attrWeapon.getTypeSkillTime() > 0) {
					final int timeSec = (int) (attrWeapon.getTypeSkillTime());

					if (!_target.hasSkillEffect(L1SkillId.SLOW)) {
						_target.setSkillEffect(L1SkillId.SLOW, timeSec * 1000);
						_target.broadcastPacketX8(new S_SkillSound(_target.getId(), 752));
						if (_targetPc != null) {
							_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 752, timeSec));
						}
					}
				}

				// 解除玩家武器 (0:沒效果 1:有效果)
				if (attrWeapon.getTypeRemoveWeapon()) {
					if (_targetPc != null && _targetPc.getWeapon() != null) {
						// 解除裝備
						_targetPc.getInventory().setEquipped(_targetPc.getWeapon(), false, false, false);
						// 裝備的武器被強制解除。
						_targetPc.sendPackets(new S_ServerMessage(1027));
						_target.broadcastPacketX8(new S_SkillSound(_target.getId(), ConfigAlt.AttackPower7));
					}
				}

				// 解除玩家的娃娃 (0:沒效果 1:有效果)
				if (attrWeapon.getTypeRemoveDoll()) {
					if (_targetPc != null) {
						if (!_targetPc.getDolls().isEmpty()) {
							for (final Iterator<L1DollInstance> iter = _targetPc.getDolls().values()
									.iterator(); iter.hasNext();) {
								final L1DollInstance doll = iter.next();
								// 移除魔法娃娃 (固定一隻)
								doll.deleteDoll();
								break;
							}
						}
					}
				}

				// 解除玩家裝備 (含件數)
				if (attrWeapon.getTypeRemoveArmor() > 0) {
					if (_targetPc != null) {
						// 可移除件數 (隨機 1~X)
						int counter = _random.nextInt(attrWeapon.getTypeRemoveArmor()) + 1;
						// StringBuffer 緩衝字串
						final StringBuffer sbr = new StringBuffer();
						for (final L1ItemInstance item : _targetPc.getInventory().getItems()) {
							// 該道具為裝備`L1Armor` 且 在穿著中
							if (item.getItem().getType2() == 2 && item.isEquipped()) {
								// 解除裝備
								_targetPc.getInventory().setEquipped(item, false, false, false);
								// 連結字串
								sbr.append("[").append(item.getNumberedName(1, false)).append("]");
								// 次數-1
								if (--counter <= 0) {
									break;
								}
							}
						}
						if (sbr.length() > 0) {
							_target.broadcastPacketX8(new S_SkillSound(_target.getId(), ConfigAlt.AttackPower8));
							_targetPc.sendPackets(new S_SystemMessage("以下裝備被對方卸除:" + sbr.toString()));
							_pc.sendPackets(new S_SystemMessage("成功卸除對方以下裝備:" + sbr.toString()));
						}
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return reset_dmg;
	}
}