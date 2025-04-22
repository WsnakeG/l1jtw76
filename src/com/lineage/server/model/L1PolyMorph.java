package com.lineage.server.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.PolyTable;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_ChangeShape;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1WilliamGfxIdOrginal;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 變形控制項
 * 
 * @author daien
 */
public class L1PolyMorph {

	private static final Log _log = LogFactory.getLog(L1PolyMorph.class);

	// weapon equip bit(2047)
	private static final int DAGGER_EQUIP = 1;

	private static final int SWORD_EQUIP = 2;

	private static final int TWOHANDSWORD_EQUIP = 4;

	private static final int AXE_EQUIP = 8;

	private static final int SPEAR_EQUIP = 16;

	private static final int STAFF_EQUIP = 32;

	private static final int EDORYU_EQUIP = 64;

	private static final int CLAW_EQUIP = 128;

	private static final int BOW_EQUIP = 256; // ガントレット含む

	private static final int KIRINGKU_EQUIP = 512;

	private static final int CHAINSWORD_EQUIP = 1024;

	// armor equip bit
	private static final int HELM_EQUIP = 1;

	private static final int AMULET_EQUIP = 2;

	private static final int EARRING_EQUIP = 4;

	private static final int TSHIRT_EQUIP = 8;

	private static final int ARMOR_EQUIP = 16;

	private static final int CLOAK_EQUIP = 32;

	private static final int BELT_EQUIP = 64;

	private static final int SHIELD_EQUIP = 128;

	private static final int GLOVE_EQUIP = 256;

	private static final int RING_EQUIP = 512;

	private static final int BOOTS_EQUIP = 1024;

	private static final int GUARDER_EQUIP = 2048;

	// 変身の原因を示すbit
	public static final int MORPH_BY_ITEMMAGIC = 1;

	public static final int MORPH_BY_GM = 2;

	public static final int MORPH_BY_NPC = 4; // 占星術師ケプリシャ以外のNPC

	public static final int MORPH_BY_KEPLISHA = 8;

	public static final int MORPH_BY_LOGIN = 0;

	private static final Map<Integer, Integer> weaponFlgMap = new HashMap<Integer, Integer>();

	static {
		weaponFlgMap.put(1, SWORD_EQUIP);// 劍
		weaponFlgMap.put(2, DAGGER_EQUIP);// 匕首
		weaponFlgMap.put(3, TWOHANDSWORD_EQUIP);// 雙手劍
		weaponFlgMap.put(4, BOW_EQUIP);// 弓
		weaponFlgMap.put(5, SPEAR_EQUIP);// 矛(雙手)
		weaponFlgMap.put(6, AXE_EQUIP);// 斧(單手)
		weaponFlgMap.put(7, STAFF_EQUIP);// 魔杖
		weaponFlgMap.put(8, BOW_EQUIP);// 飛刀
		weaponFlgMap.put(9, BOW_EQUIP);// 箭
		weaponFlgMap.put(10, BOW_EQUIP);// 鐵手甲
		weaponFlgMap.put(11, CLAW_EQUIP);// 鋼爪
		weaponFlgMap.put(12, EDORYU_EQUIP);// 雙刀
		weaponFlgMap.put(13, BOW_EQUIP);// 弓(單手)
		weaponFlgMap.put(14, SPEAR_EQUIP);// 矛(單手)
		weaponFlgMap.put(15, AXE_EQUIP);// 雙手斧
		weaponFlgMap.put(16, STAFF_EQUIP);// 魔杖(雙手)
		weaponFlgMap.put(17, KIRINGKU_EQUIP);// 奇古獸
		weaponFlgMap.put(18, CHAINSWORD_EQUIP);// 鎖鏈劍
	}

	private static final Map<Integer, Integer> armorFlgMap = new HashMap<Integer, Integer>();

	static {
		armorFlgMap.put(1, HELM_EQUIP);
		armorFlgMap.put(2, ARMOR_EQUIP);
		armorFlgMap.put(3, TSHIRT_EQUIP);
		armorFlgMap.put(4, CLOAK_EQUIP);
		armorFlgMap.put(5, GLOVE_EQUIP);
		armorFlgMap.put(6, BOOTS_EQUIP);
		armorFlgMap.put(7, SHIELD_EQUIP);
		armorFlgMap.put(8, AMULET_EQUIP);
		armorFlgMap.put(9, RING_EQUIP);
		armorFlgMap.put(10, BELT_EQUIP);
		armorFlgMap.put(12, EARRING_EQUIP);
		armorFlgMap.put(13, GUARDER_EQUIP);
	}

	private final int _id;
	private final String _name;
	private final int _polyId;
	private final int _minLevel;
	private final int _weaponEquipFlg;
	private final int _armorEquipFlg;
	private final boolean _canUseSkill;
	private final int _causeFlg;

	public L1PolyMorph(final int id, final String name, final int polyId, final int minLevel,
			final int weaponEquipFlg, final int armorEquipFlg, final boolean canUseSkill,
			final int causeFlg) {
		_id = id;
		_name = name;
		_polyId = polyId;
		_minLevel = minLevel;
		_weaponEquipFlg = weaponEquipFlg;
		_armorEquipFlg = armorEquipFlg;
		_canUseSkill = canUseSkill;
		_causeFlg = causeFlg;
	}

	public int getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public int getPolyId() {
		return _polyId;
	}

	public int getMinLevel() {
		return _minLevel;
	}

	public int getWeaponEquipFlg() {
		return _weaponEquipFlg;
	}

	public int getArmorEquipFlg() {
		return _armorEquipFlg;
	}

	public boolean canUseSkill() {
		return _canUseSkill;
	}

	public int getCauseFlg() {
		return _causeFlg;
	}

	public static void handleCommands(final L1PcInstance pc, final String s) {
		if ((pc == null) || pc.isDead()) {
			return;
		}
		final L1PolyMorph poly = PolyTable.get().getTemplate(s);
		if ((poly != null) || s.equals("none")) {
			if (s.equals("none")) {
				if ((pc.getTempCharGfx() == 6034) || (pc.getTempCharGfx() == 6035)) {
				} else {
					pc.removeSkillEffect(L1SkillId.SHAPE_CHANGE);
					pc.sendPackets(new S_CloseList(pc.getId()));
				}

			} else if ((pc.getLevel() >= poly.getMinLevel()) || pc.isGm()) {
				if ((pc.getTempCharGfx() == 6034) || (pc.getTempCharGfx() == 6035)) {
					// 181:\f1無法變成你指定的怪物。
					pc.sendPackets(new S_ServerMessage(181));

				} else {
					doPoly(pc, poly.getPolyId(), 7200, MORPH_BY_ITEMMAGIC);
					pc.sendPackets(new S_CloseList(pc.getId()));
				}

			} else {
				// 181:\f1無法變成你指定的怪物。
				pc.sendPackets(new S_ServerMessage(181));
			}
		}
	}

	/** 380 商村變身 */
	/**
	 * @param cha
	 * @param polyIndex 1-8
	 */
	public static void doPolyPraivateShop(final L1Character cha, final int polyIndex) {
		if ((cha == null) || cha.isDead()) {
			return;
		}
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			final int PolyList[] = { 11479, 11427, 10047, 9688, 11322, 10069, 10034, 10032 };
			if (pc.getTempCharGfx() != PolyList[polyIndex - 1]) {
				pc.getInventory().takeoffEquip(PolyList[polyIndex - 1]);
				pc.setTempCharGfx(PolyList[polyIndex - 1]);
				pc.sendPackets(new S_ChangeShape(pc, PolyList[polyIndex - 1]));
				if (!pc.isGmInvis() && !pc.isInvisble()) {
					pc.broadcastPacketAll(new S_ChangeShape(pc, PolyList[polyIndex - 1]));
				}
				pc.sendPacketsAll(new S_CharVisualUpdate(pc));
			}
		}
	}

	/**
	 * 380 解除商村變身
	 * 
	 * @param cha
	 */
	public static void undoPolyPrivateShop(final L1Character cha) {
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			final int classId = pc.getClassId();
			pc.setTempCharGfx(classId);
			if (!pc.isDead()) {
				pc.sendPacketsAll(new S_ChangeShape(pc, classId));
				pc.sendPacketsAll(new S_CharVisualUpdate(pc));
			}
		}
	}

	/** 3.80시장리뉴얼 */

	/**
	 * 執行變身
	 * 
	 * @param cha
	 * @param polyId
	 * @param timeSecs
	 * @param cause
	 */
	public static void doPoly(final L1Character cha, final int polyId, final int timeSecs, final int cause) {
		try {
			if ((cha == null) || cha.isDead()) {
				return;
			}
			// 變身禁止使用
			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				// added by terry0412
				if (pc.getMapId() == 5490) {
					// 這裡不可以變身。
					pc.sendPackets(new S_ServerMessage(1170));
					return;
				}
				if ((pc.getTempCharGfx() == 6034) || (pc.getTempCharGfx() == 6035)) {
					// 181:\f1無法變成你指定的怪物。
					pc.sendPackets(new S_ServerMessage(181));
					return;
				}
				if (!isMatchCause(polyId, cause)) {
					// 181:\f1無法變成你指定的怪物。
					pc.sendPackets(new S_ServerMessage(181));
					return;
				}

				pc.killSkillEffectTimer(L1SkillId.SHAPE_CHANGE);
				pc.setSkillEffect(L1SkillId.SHAPE_CHANGE, timeSecs * 1000);

				if (pc.getTempCharGfx() != polyId) { // 同じ変身の場合はアイコン送信以外が必要ない
					L1ItemInstance weapon = pc.getWeapon();
					// 変身によって武器が外れるか
					final boolean weaponTakeoff = ((weapon != null)
							&& !isEquipableWeapon(polyId, weapon.getItem().getType()));
					// 特殊變身功能
					L1WilliamGfxIdOrginal.getReductionGfxIdOrginal(pc, pc.getTempCharGfx());
					pc.setTempCharGfx(polyId); // 移動位置 by terry0412

					// repaired by terry0412
					if (weaponTakeoff) { // 武器強制脫除
						pc.setCurrentWeapon(0);
					}

					pc.sendPackets(new S_ChangeShape(pc, polyId, weaponTakeoff));
					if (!pc.isGmInvis() && !pc.isInvisble()) {
						pc.broadcastPacketAll(new S_ChangeShape(pc, polyId));
					}

					pc.getInventory().takeoffEquip(polyId);
					weapon = pc.getWeapon();

					if (weapon != null) {
						pc.sendPacketsAll(new S_CharVisualUpdate(pc));
					}

					boolean check = false;
					int range = 1;
					int type = 1;
					weapon = pc.getWeapon();
					if (weapon == null) {
						pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 0, check));
					} else {
						if (weapon.getItem().getType() == 4) {
							range = 17;
						} else if ((weapon.getItem().getType() == 10) || (weapon.getItem().getType() == 13)) {
							range = 14;
						} else if ((weapon.getItem().getType() == 5) || (weapon.getItem().getType() == 14)
								|| (weapon.getItem().getType() == 18)) {
							range = 1;
							if ((polyId == 11330) || (polyId == 11344) || (polyId == 11351)
									|| (polyId == 11368) || (polyId == 12240) || (polyId == 12237)
									|| (polyId == 11447) || (polyId == 11408) || (polyId == 11409)
									|| (polyId == 11410) || (polyId == 11411) || (polyId == 11418)
									|| (polyId == 11419) || (polyId == 12613) || (polyId == 12614)) {
								range = 2;
							} else if (!pc.hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
								range = 2;
							}
						}

						if (pc.isKnight()) {
							if (weapon.getItem().getType() == 3) {
								check = true;
							}
						} else if (pc.isElf()) {
							if (pc.hasSkillEffect(L1SkillId.DANCING_BLAZE)) {
								check = true;
							}
							if (((weapon.getItem().getType() == 4) || (weapon.getItem().getType() == 13))
									&& (weapon.getItem().getType1() == 20)) {
								type = 3;
								check = true;
							}
						} else if (pc.isDragonKnight()) {
							check = true;
							if ((weapon.getItem().getType() == 14) || (weapon.getItem().getType() == 18)) {
								type = 10;
							}
						}

						if ((weapon.getItem().getType1() != 20) && (weapon.getItem().getType1() != 62)) {
							pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, type, check));
						} else {
							pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 3, check));
						}
						pc.setRange(range);
					}
				}
				// 特殊變身功能
				L1WilliamGfxIdOrginal.getAddGfxIdOrginal(pc, polyId);
				pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_POLYMORPH, timeSecs));

			} else if (cha instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) cha;
				mob.killSkillEffectTimer(L1SkillId.SHAPE_CHANGE);
				mob.setSkillEffect(L1SkillId.SHAPE_CHANGE, timeSecs * 1000);
				if (mob.getTempCharGfx() != polyId) { // 同じ変身の場合はアイコン送信以外が必要ない
					mob.setTempCharGfx(polyId);
					mob.broadcastPacketAll(new S_ChangeShape(mob, polyId));
				}

			} else if (cha instanceof L1DeInstance) {
				final L1DeInstance de = (L1DeInstance) cha;
				de.killSkillEffectTimer(L1SkillId.SHAPE_CHANGE);
				de.setSkillEffect(L1SkillId.SHAPE_CHANGE, timeSecs * 1000);
				if (de.getTempCharGfx() != polyId) { // 同じ変身の場合はアイコン送信以外が必要ない
					de.setTempCharGfx(polyId);
					de.broadcastPacketAll(new S_ChangeShape(de, polyId));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 執行變身
	 * 
	 * @param cha
	 * @param polyId
	 * @param timeSecs
	 * @param cause
	 */
	public static void doPolyR(L1Character cha, int polyId, int timeSecs, int cause) {
		try {
			if ((cha == null) || (cha.isDead())) {
				return;
			}

			if ((cha instanceof L1PcInstance)) {
				L1PcInstance pc = (L1PcInstance) cha;

				if ((pc.getMapId() == 5300) || (pc.getMapId() == 9000) || (pc.getMapId() == 9100) || (pc.getMapId() == 9101) || (pc.getMapId() == 9102) || (pc.getMapId() == 9202)) {
					// 這裡不可以變身。
					pc.sendPackets(new S_ServerMessage(1170));
					return;
				}

				final L1PolyMorph poly = PolyTable.get().getTemplate(polyId);

				if (poly == null) {
					pc.sendPackets(new S_ServerMessage("此變身不在系統設置內,所以無法變身"));
					return;
				}

				if ((pc.getTempCharGfx() == 6034) || (pc.getTempCharGfx() == 6035)) {
					// 181:\f1無法變成你指定的怪物。
					pc.sendPackets(new S_ServerMessage(181));
					return;
				}

				if (!isMatchCause(polyId, cause)) {
					// 181:\f1無法變成你指定的怪物。
					pc.sendPackets(new S_ServerMessage(181));
					return;
				}

				pc.removeSkillEffect(SHAPE_CHANGE);// 刪除變身效果
				pc.setSkillEffect(SHAPE_CHANGE, timeSecs * 1000);// 重新給予變身效果
				L1WilliamGfxIdOrginal.getReductionGfxIdOrginal(pc, pc.getTempCharGfx());

				if (pc.getTempCharGfx() != polyId) {
					L1ItemInstance weapon = pc.getWeapon();

					boolean weaponTakeoff = (weapon != null) && (!isEquipableWeapon(polyId, weapon.getItem().getType()));

					pc.setTempCharGfx(polyId);

					// if (weaponTakeoff) { // 武器强制脱除
					// pc.setCurrentWeapon(0);
					// }

					pc.sendPackets(new S_ChangeShape(pc, polyId, weaponTakeoff));
					if ((!pc.isGmInvis()) && (!pc.isInvisble())) {
						pc.broadcastPacketAll(new S_ChangeShape(pc, polyId));
					}
					pc.getInventory().takeoffEquip(polyId);

					weapon = pc.getWeapon();
					if (weapon != null) {
						pc.sendPacketsAll(new S_CharVisualUpdate(pc));
					}

					pc.getWeaponRange(); // 設置攻擊距離
				}

				if (timeSecs != 0) { // src013
					// 特殊變身功能
					L1WilliamGfxIdOrginal.getAddGfxIdOrginal(pc, polyId);
					pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_POLYMORPH, timeSecs));// 右上角變身圖示
				}
				// 变身之后原地刷新
				L1Teleport.teleport(pc, L1Location.randomLocation(pc.getLocation(), 0, 0, true), (int) (System.currentTimeMillis() % 8), false);


			} else if ((cha instanceof L1MonsterInstance)) {
				L1MonsterInstance mob = (L1MonsterInstance) cha;
				mob.removeSkillEffect(SHAPE_CHANGE);// 刪除變身效果
				mob.setSkillEffect(SHAPE_CHANGE, timeSecs * 1000);
				if (mob.getTempCharGfx() != polyId) {
					mob.setTempCharGfx(polyId);
					mob.broadcastPacketAll(new S_ChangeShape(mob, polyId));
				}

			} else if ((cha instanceof L1DeInstance)) {
				L1DeInstance de = (L1DeInstance) cha;
				de.removeSkillEffect(SHAPE_CHANGE);// 刪除變身效果
				de.setSkillEffect(SHAPE_CHANGE, timeSecs * 1000);
				if (de.getTempCharGfx() != polyId) {
					de.setTempCharGfx(polyId);
					de.broadcastPacketAll(new S_ChangeShape(de, polyId));
				}
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 解除變身
	 * 
	 * @param cha
	 */
	public static void undoPoly(final L1Character cha) {
		try {
			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				// 特殊變身功能
				L1WilliamGfxIdOrginal.getReductionGfxIdOrginal(pc, pc.getTempCharGfx());
				final int classId = pc.getClassId();
				pc.setTempCharGfx(classId);
				pc.sendPacketsAll(new S_ChangeShape(pc, classId));
				final L1ItemInstance weapon = pc.getWeapon();
				if (weapon != null) {
					pc.sendPacketsAll(new S_CharVisualUpdate(pc));
				}
				boolean check = false;
				int range = 1;
				int type = 1;
				if (weapon == null) {
					pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 0, check));
				} else {
					if (weapon.getItem().getType() == 4) {
						range = 15;
					} else if ((weapon.getItem().getType() == 10) || (weapon.getItem().getType() == 13)) {
						range = 14;
					} else if ((weapon.getItem().getType() == 5) || (weapon.getItem().getType() == 14)
							|| (weapon.getItem().getType() == 18)) {
						range = 1;
						final int polyId = pc.getTempCharGfx();
						if ((polyId == 11330) || (polyId == 11344) || (polyId == 11351) || (polyId == 11368)
								|| (polyId == 12240) || (polyId == 12237) || (polyId == 11447)
								|| (polyId == 11408) || (polyId == 11409) || (polyId == 11410)
								|| (polyId == 11411) || (polyId == 11418) || (polyId == 11419)
								|| (polyId == 12613) || (polyId == 12614)) {
							range = 2;
						} else if (!pc.hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
							range = 2;
						}
					}

					if (pc.isKnight()) {
						if (weapon.getItem().getType() == 3) {
							check = true;
						}
					} else if (pc.isElf()) {
						if (pc.hasSkillEffect(L1SkillId.DANCING_BLAZE)) {
							check = true;
						}
						if (((weapon.getItem().getType() == 4) || (weapon.getItem().getType() == 13))
								&& (weapon.getItem().getType1() == 20)) {
							type = 3;
							check = true;
						}
					} else if (pc.isDragonKnight()) {
						check = true;
						if ((weapon.getItem().getType() == 14) || (weapon.getItem().getType() == 18)) {
							type = 10;
						}
					}

					if ((weapon.getItem().getType1() != 20) && (weapon.getItem().getType1() != 62)) {
						pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, type, check));
					} else {
						pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 3, check));
					}
					pc.setRange(range);
				}
			} else if (cha instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) cha;
				mob.setTempCharGfx(0);
				mob.broadcastPacketAll(new S_ChangeShape(mob, mob.getGfxId()));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 指定したpolyIdがweapontTypeの武器を装備出来るか？
	public static boolean isEquipableWeapon(final int polyId, final int weaponType) {
		try {
			final L1PolyMorph poly = PolyTable.get().getTemplate(polyId);
			if (poly == null) {
				return true;
			}

			final Integer flg = weaponFlgMap.get(weaponType);
			if (flg != null) {
				return 0 != (poly.getWeaponEquipFlg() & flg);
			}
			return true;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return true;
	}

	// 指定したpolyIdがarmorTypeの防具を装備出来るか？
	public static boolean isEquipableArmor(final int polyId, final int armorType) {
		try {
			final L1PolyMorph poly = PolyTable.get().getTemplate(polyId);
			if (poly == null) {
				return true;
			}

			final Integer flg = armorFlgMap.get(armorType);
			if (flg != null) {
				return 0 != (poly.getArmorEquipFlg() & flg);
			}
			return true;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return true;
	}

	// 指定したpolyIdが何によって変身し、それが変身させられるか？
	public static boolean isMatchCause(final int polyId, final int cause) {
		try {
			final L1PolyMorph poly = PolyTable.get().getTemplate(polyId);
			if (poly == null) {
				return true;
			}
			if (cause == MORPH_BY_LOGIN) {
				return true;
			}
			return 0 != (poly.getCauseFlg() & cause);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}
}
